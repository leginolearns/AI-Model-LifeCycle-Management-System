#!/usr/bin/env python3
"""Real ML engine for the Java AI Model Lifecycle Management System.
Usage: train <version_id> <algorithm> <csv_path> <target_column>
       test <version_id>
       serve <version_id> <port>
"""
import json, sys, traceback
from pathlib import Path
from http.server import BaseHTTPRequestHandler, HTTPServer

ROOT = Path(__file__).resolve().parent
ARTIFACTS = ROOT / "artifacts"
ARTIFACTS.mkdir(exist_ok=True)

def artifact(version_id):
    return ARTIFACTS / f"model_version_{version_id}.joblib"

def metric_path(version_id):
    return ARTIFACTS / f"model_version_{version_id}_metrics.json"

def load_libs():
    try:
        import pandas as pd
        import joblib
        from sklearn.compose import ColumnTransformer
        from sklearn.pipeline import Pipeline
        from sklearn.impute import SimpleImputer
        from sklearn.preprocessing import OneHotEncoder, StandardScaler
        from sklearn.model_selection import train_test_split
        from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, mean_absolute_error, mean_squared_error, r2_score, confusion_matrix
        from sklearn.ensemble import RandomForestClassifier, RandomForestRegressor, GradientBoostingClassifier, GradientBoostingRegressor, ExtraTreesClassifier
        from sklearn.tree import DecisionTreeClassifier, DecisionTreeRegressor
        from sklearn.linear_model import LogisticRegression, LinearRegression
        from sklearn.svm import SVC, SVR
        return locals()
    except ImportError as e:
        print("ERROR=Missing Python package: " + str(e))
        print("Install dependencies with: python -m pip install -r requirements.txt")
        sys.exit(2)

def make_estimator(algorithm, is_classification, libs):
    key = algorithm.lower().replace('_',' ').replace('-',' ')
    if 'linear regression' in key or key.strip() == 'regression': return libs['LinearRegression']()
    if 'svm' in key or 'support vector' in key:
        return libs['SVC']() if is_classification else libs['SVR']()
    if 'logistic' in key: return libs['LogisticRegression'](max_iter=2000)
    if 'decision tree' in key: return libs['DecisionTreeClassifier'](random_state=42) if is_classification else libs['DecisionTreeRegressor'](random_state=42)
    if 'gradient' in key: return libs['GradientBoostingClassifier'](random_state=42) if is_classification else libs['GradientBoostingRegressor'](random_state=42)
    if 'extra trees' in key: return libs['ExtraTreesClassifier'](n_estimators=150, random_state=42)
    return libs['RandomForestClassifier'](n_estimators=150, random_state=42, class_weight='balanced') if is_classification else libs['RandomForestRegressor'](n_estimators=150, random_state=42)

def train(version_id, algorithm, csv_path, target):
    l = load_libs(); pd=l['pd']; joblib=l['joblib']
    source = Path(csv_path.strip('"'))
    if not source.exists(): raise ValueError(f"CSV file not found: {source}")
    df = pd.read_csv(source)
    if df.empty or len(df) < 5: raise ValueError("CSV must contain at least 5 data rows.")
    if target not in df.columns: raise ValueError(f"Target column '{target}' not found. Columns: {', '.join(map(str,df.columns))}")
    df = df.dropna(subset=[target])
    if len(df) < 5: raise ValueError("At least 5 rows with a non-empty target are required.")
    X = df.drop(columns=[target]); y = df[target]
    if X.shape[1] == 0: raise ValueError("Dataset needs at least one feature column besides the target.")
    if y.nunique() < 2: raise ValueError("Target column must contain at least two distinct values.")
    # infer classification for categorical targets or small-cardinality targets; numeric high-cardinality targets are regression
    is_classification = (not l['pd'].api.types.is_numeric_dtype(y)) or y.nunique() <= max(20, int(len(y)*0.05))
    if is_classification and y.nunique() < 2: raise ValueError("Classification requires at least two target classes.")
    stratify = y if is_classification and y.value_counts().min() >= 2 else None
    X_train, X_test, y_train, y_test = l['train_test_split'](X, y, test_size=0.2, random_state=42, stratify=stratify)
    numeric = X_train.select_dtypes(include=['number']).columns.tolist()
    categorical = [c for c in X_train.columns if c not in numeric]
    numeric_pipe = l['Pipeline']([('imputer', l['SimpleImputer'](strategy='median')), ('scaler', l['StandardScaler']())])
    categorical_pipe = l['Pipeline']([('imputer', l['SimpleImputer'](strategy='most_frequent')), ('encoder', l['OneHotEncoder'](handle_unknown='ignore'))])
    transformers=[]
    if numeric: transformers.append(('numeric', numeric_pipe, numeric))
    if categorical: transformers.append(('categorical', categorical_pipe, categorical))
    preprocess = l['ColumnTransformer'](transformers=transformers)
    estimator = make_estimator(algorithm, is_classification, l)
    pipeline = l['Pipeline']([('preprocess', preprocess), ('model', estimator)])
    pipeline.fit(X_train, y_train)
    predictions = pipeline.predict(X_test)
    metrics = {'version_id': int(version_id), 'algorithm': algorithm, 'source_csv': str(source.resolve()), 'target_column': target,
               'task': 'classification' if is_classification else 'regression', 'rows': int(len(df)), 'train_rows': int(len(X_train)), 'test_rows': int(len(X_test)),
               'features': list(map(str, X.columns)), 'target': target}
    if is_classification:
        metrics.update({'accuracy': float(l['accuracy_score'](y_test, predictions)),
                        'precision_weighted': float(l['precision_score'](y_test, predictions, average='weighted', zero_division=0)),
                        'recall_weighted': float(l['recall_score'](y_test, predictions, average='weighted', zero_division=0)),
                        'f1_weighted': float(l['f1_score'](y_test, predictions, average='weighted', zero_division=0)),
                        'confusion_matrix': l['confusion_matrix'](y_test, predictions).tolist()})
    else:
        metrics.update({'mae': float(l['mean_absolute_error'](y_test, predictions)),
                        'rmse': float(l['mean_squared_error'](y_test, predictions) ** 0.5),
                        'r2': float(l['r2_score'](y_test, predictions))})
    joblib.dump({'pipeline': pipeline, 'features': list(X.columns), 'target': target, 'task': metrics['task']}, artifact(version_id))
    # Store the deterministic held-out set so the separate Java Test option evaluates a real unseen test split.
    test_frame = X_test.copy(); test_frame[target] = y_test
    test_frame.to_csv(ARTIFACTS / f'model_version_{version_id}_test.csv', index=False)
    metric_path(version_id).write_text(json.dumps(metrics, indent=2), encoding='utf-8')
    print('RESULT=TRAINED')
    print('ARTIFACT=' + str(artifact(version_id)))
    print('TASK=' + metrics['task'])
    print('TRAIN_ROWS=' + str(metrics['train_rows']))
    print('TEST_ROWS=' + str(metrics['test_rows']))
    print('ACCURACY=' + str(round(metrics.get('accuracy', float('nan'))*100, 2) if is_classification else float('nan')))
    print('METRICS=' + json.dumps(metrics))

def test(version_id):
    l=load_libs(); pd=l['pd']; joblib=l['joblib']
    ap=artifact(version_id); tp=ARTIFACTS / f'model_version_{version_id}_test.csv'
    if not ap.exists() or not tp.exists(): raise ValueError('No trained artifact found for this version. Choose Train Model first.')
    bundle=joblib.load(ap); df=pd.read_csv(tp); target=bundle['target']
    X=df.drop(columns=[target]); y=df[target]; pred=bundle['pipeline'].predict(X)
    metrics=json.loads(metric_path(version_id).read_text(encoding='utf-8')) if metric_path(version_id).exists() else {}
    if bundle['task']=='classification':
        metrics.update({'accuracy': float(l['accuracy_score'](y,pred)), 'precision_weighted': float(l['precision_score'](y,pred,average='weighted',zero_division=0)), 'recall_weighted': float(l['recall_score'](y,pred,average='weighted',zero_division=0)), 'f1_weighted': float(l['f1_score'](y,pred,average='weighted',zero_division=0)), 'confusion_matrix':l['confusion_matrix'](y,pred).tolist()})
        print('ACCURACY=' + str(round(metrics['accuracy']*100,2)))
    else:
        metrics.update({'mae':float(l['mean_absolute_error'](y,pred)), 'rmse':float(l['mean_squared_error'](y,pred)**0.5), 'r2':float(l['r2_score'](y,pred))})
    metric_path(version_id).write_text(json.dumps(metrics,indent=2),encoding='utf-8')
    print('RESULT=TESTED'); print('METRICS=' + json.dumps(metrics))

def serve(version_id, port):
    l=load_libs(); joblib=l['joblib']; ap=artifact(version_id)
    if not ap.exists(): raise ValueError('Trained model artifact not found. Train this version first.')
    bundle=joblib.load(ap)
    class Handler(BaseHTTPRequestHandler):
        def _send(self, code, payload):
            data=json.dumps(payload).encode(); self.send_response(code); self.send_header('Content-Type','application/json'); self.send_header('Content-Length',str(len(data))); self.end_headers(); self.wfile.write(data)
        def do_GET(self):
            if self.path == '/health': self._send(200, {'status':'healthy','version_id':int(version_id)})
            else: self._send(404, {'error':'Use GET /health or POST /predict'})
        def do_POST(self):
            if self.path != '/predict': return self._send(404, {'error':'Use POST /predict'})
            try:
                body=json.loads(self.rfile.read(int(self.headers.get('Content-Length','0'))))
                rows=body if isinstance(body,list) else [body]
                frame=l['pd'].DataFrame(rows)
                missing=[c for c in bundle['features'] if c not in frame.columns]
                if missing: return self._send(400, {'error':'Missing feature fields','missing':missing})
                preds=bundle['pipeline'].predict(frame[bundle['features']]).tolist()
                self._send(200, {'version_id':int(version_id),'predictions':[x.item() if hasattr(x,'item') else x for x in preds]})
            except Exception as e: self._send(400, {'error':str(e)})
        def log_message(self, fmt, *args): print('ML API:', fmt % args)
    server=HTTPServer(('127.0.0.1',int(port)),Handler)
    print(f'ML prediction API for version {version_id} listening on http://127.0.0.1:{port}')
    server.serve_forever()

def main():
    if len(sys.argv)<2: raise ValueError('Expected train, test, or serve command.')
    command=sys.argv[1]
    if command=='train' and len(sys.argv)>=6: train(int(sys.argv[2]),sys.argv[3],sys.argv[4],sys.argv[5])
    elif command=='test' and len(sys.argv)>=3: test(int(sys.argv[2]))
    elif command=='serve' and len(sys.argv)>=4: serve(int(sys.argv[2]),int(sys.argv[3]))
    else: raise ValueError('Invalid arguments.')
if __name__=='__main__':
    try: main()
    except Exception as e:
        print('ERROR=' + str(e))
        sys.exit(1)
