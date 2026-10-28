import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static ArrayList<AIModel> models = new ArrayList<>();
    static ArrayList<Dataset> datasets = new ArrayList<>();
    static ArrayList<Deployment> deployments = new ArrayList<>();
    static ArrayList<MonitoringRecord> monitoringRecords = new ArrayList<>();

    static int modelIdCounter = 1001;
    static int datasetIdCounter = 501;
    static int versionIdCounter = 1;
    static int deploymentIdCounter = 701;
    static int monitoringIdCounter = 801;

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("   AI MODEL LIFECYCLE MANAGEMENT SYSTEM");
        System.out.println("==========================================");

        if (DatabaseConnection.testConnection()) {
            datasets.addAll(DatasetDAO.loadAll());
            models.addAll(AIModelDAO.loadAll());
            ModelVersionDAO.loadInto(models, datasets);
            deployments.addAll(DeploymentDAO.loadAll(models));
            System.out.println("Loaded from MySQL: " + datasets.size() + " datasets, " + models.size() + " models, " + models.stream().mapToInt(m -> m.getVersions().size()).sum() + " versions, " + deployments.size() + " deployments.");
        }

        int choice;

        do {
            displayMenu();
            choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    createDataset();
                    break;

                case 2:
                    createModel();
                    break;

                case 3:
                    addModelVersion();
                    break;

                case 4:
                    trainModel();
                    break;

                case 5:
                    testModel();
                    break;

                case 6:
                    deployModel();
                    break;

                case 7:
                    monitorModel();
                    break;

                case 8:
                    displayModels();
                    break;

                case 9:
                    displayDeployments();
                    break;

                case 10:
                    MonitoringRecordDAO.displayRecords();
                    break;

                case 11:
                    updateModel();
                    break;

                case 12:
                    maintainModel();
                    break;

                case 13:
                    AIModelDAO.displayModels();
                    break;

                case 14:
                    DatasetDAO.displayDatasets();
                    break;

                case 15:
                    ModelVersionDAO.displayVersions();
                    break;

                case 16:
                    MonitoringRecordDAO.displayRecords();
                    break;

                case 0:
                    System.out.println("\nExiting system...");
                    break;

                default:
                    System.out.println("\nInvalid choice.");
            }

        } while (choice != 0);

        scanner.close();
    }

    public static void displayMenu() {
        System.out.println("\n==========================================");
        System.out.println("   AI MODEL LIFECYCLE MANAGEMENT SYSTEM");
        System.out.println("==========================================");
        System.out.println("1.  Create Dataset");
        System.out.println("2.  Create AI Model");
        System.out.println("3.  Add Model Version");
        System.out.println("4.  Train Model");
        System.out.println("5.  Test Model");
        System.out.println("6.  Deploy Model");
        System.out.println("7.  Monitor Model");
        System.out.println("8.  View Models");
        System.out.println("9.  View Deployments");
        System.out.println("10. View Monitoring Records");
        System.out.println("11. Update Model");
        System.out.println("12. Maintain Model");
        System.out.println("13. View Database Models");
        System.out.println("14. View Database Datasets");
        System.out.println("15. View Database Model Versions");
        System.out.println("16. View Database Monitoring Records");
        System.out.println("0.  Exit");
        System.out.println("==========================================");
    }

    public static int readInt(String message) {
        while (true) {
            System.out.print(message);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();
                return value;
            }

            System.out.println(
                    "Invalid input. Please enter a number."
            );
            scanner.nextLine();
        }
    }

    public static double readDouble(String message) {
        while (true) {
            System.out.print(message);

            if (scanner.hasNextDouble()) {
                double value = scanner.nextDouble();
                scanner.nextLine();
                return value;
            }

            System.out.println(
                    "Invalid input. Please enter a number."
            );
            scanner.nextLine();
        }
    }

    public static void createDataset() {
        System.out.println("\n========== CREATE DATASET ==========");
        System.out.print("Enter dataset name: "); String name = scanner.nextLine().trim();
        System.out.print("Enter dataset source: "); String source = scanner.nextLine().trim();
        int records;
        do { records = readInt("Enter number of records: "); if (records <= 0) System.out.println("Number of records must be greater than 0."); } while (records <= 0);
        try {
            Dataset dataset = new Dataset(DatasetDAO.nextId(), name, source, records);
            if (DatasetDAO.saveDataset(dataset)) { datasets.add(dataset); System.out.println("Dataset created and saved successfully. ID: " + dataset.getDatasetId()); }
            else System.out.println("Dataset was not saved; it was not added to the active session.");
        } catch (RuntimeException e) { System.out.println("Could not create dataset: " + e.getMessage()); }
    }

    public static void createModel() {
        System.out.println("\n========== CREATE AI MODEL ==========");
        System.out.print("Enter model name: "); String name = scanner.nextLine().trim();
        System.out.print("Enter algorithm (Random Forest, Decision Tree, Logistic Regression, SVM, Linear Regression): "); String algorithm = scanner.nextLine().trim();
        System.out.print("Enter description: "); String description = scanner.nextLine().trim();
        try {
            AIModel model = new AIModel(AIModelDAO.nextId(), name, algorithm, description);
            if (AIModelDAO.saveModel(model)) { models.add(model); System.out.println("AI model created and saved successfully. ID: " + model.getModelId()); }
            else System.out.println("AI model was not saved; it was not added to the active session.");
        } catch (RuntimeException e) { System.out.println("Could not create model: " + e.getMessage()); }
    }

    public static void addModelVersion() {

    System.out.println("\n========== ADD MODEL VERSION ==========");

    if (models.isEmpty()) {
        System.out.println("No models available.");
        return;
    }

    if (datasets.isEmpty()) {
        System.out.println("No datasets available.");
        return;
    }

    displayModels();

    int modelChoice = readInt("Enter model number: ") - 1;

    if (modelChoice < 0 || modelChoice >= models.size()) {
        System.out.println("Invalid model.");
        return;
    }

    displayDatasets();

    int datasetChoice = readInt("Enter dataset number: ") - 1;

    if (datasetChoice < 0 || datasetChoice >= datasets.size()) {
        System.out.println("Invalid dataset.");
        return;
    }

    System.out.print(
            "Enter version number (example: v1.0): "
    );

    String versionNumber = scanner.nextLine();

    ModelVersion version = new ModelVersion(
            ModelVersionDAO.nextId(),
            versionNumber,
            datasets.get(datasetChoice)
    );

    if (ModelVersionDAO.saveVersion(version, models.get(modelChoice).getModelId())) {
        models.get(modelChoice).addVersion(version);
        System.out.println("\nModel version added and saved to database successfully.");
    } else {
        System.out.println("\nModel version was not added because database save failed.");
    }
}

    private static AIModel parentModel(ModelVersion version) {
        for (AIModel model : models) for (ModelVersion candidate : model.getVersions()) if (candidate.getVersionId() == version.getVersionId()) return model;
        return null;
    }

    public static void trainModel() {
        System.out.println("\n========== REAL MODEL TRAINING ==========");
        ModelVersion version = selectModelVersion(); if (version == null) return;
        AIModel model = parentModel(version); if (model == null) { System.out.println("Could not identify the model for this version."); return; }
        System.out.println("Enter the path to a CSV file. Example: C:\\data\\students.csv");
        System.out.print("CSV path: "); String csv = scanner.nextLine().trim();
        System.out.print("Target column (the column to predict): "); String target = scanner.nextLine().trim();
        System.out.println("Training real " + model.getAlgorithm() + " model. This may take a moment...");
        if (MLBridge.train(version.getVersionId(), model.getAlgorithm(), csv, target)) {
            version.setStatus("Trained"); ModelVersionDAO.updateVersion(version);
            System.out.println("Training complete. Saved model artifact and held-out test data in the artifacts folder.");
            System.out.println("Choose option 5 to evaluate the trained model on the held-out test split.");
        }
    }

    public static void testModel() {
        System.out.println("\n========== REAL MODEL TESTING ==========");
        ModelVersion version = selectModelVersion(); if (version == null) return;
        if (!MLBridge.hasArtifact(version.getVersionId())) { System.out.println("No trained model artifact exists. Choose Train Model first."); return; }
        Double accuracy = MLBridge.test(version.getVersionId());
        if (accuracy == null) { System.out.println("Testing failed. See the ML engine error above."); return; }
        if (!accuracy.isNaN()) { version.setAccuracy(accuracy); System.out.println("Held-out test accuracy: " + accuracy + "%"); }
        else System.out.println("Regression evaluation completed. MAE, RMSE and R-squared are shown in the ML engine output/metrics JSON.");
        version.setStatus("Tested"); ModelVersionDAO.updateVersion(version);
        System.out.println("Test results saved. This version is now eligible for deployment.");
    }

    public static void deployModel() {
        System.out.println("\n========== DEPLOY REAL MODEL API ==========");
        ModelVersion version = selectModelVersion(); if (version == null) return;
        if (!version.getStatus().equals("Tested")) { System.out.println("Model must be trained and tested before deployment."); return; }
        if (!MLBridge.hasArtifact(version.getVersionId())) { System.out.println("Trained model artifact is missing. Train the version first."); return; }
        int port = readInt("Enter local API port (e.g. 8765): "); if (port < 1024 || port > 65535) { System.out.println("Port must be between 1024 and 65535."); return; }
        String endpoint = "http://127.0.0.1:" + port + "/predict";
        if (!MLBridge.startServer(version.getVersionId(), port)) { System.out.println("Deployment failed; prediction API did not start."); return; }
        System.out.print("Environment label (e.g. Local): "); String environment = scanner.nextLine().trim();
        Deployment deployment = new Deployment(DeploymentDAO.nextId(), environment.isBlank() ? "Local" : environment, endpoint, version);
        version.deploy(); deployment.deployModel();
        if (!deployment.getStatus().equals("Deployed")) { System.out.println("Deployment workflow did not reach Deployed status."); return; }
        if (DeploymentDAO.save(deployment)) {
            deployments.add(deployment); ModelVersionDAO.updateVersion(version);
            System.out.println("Real local prediction API is running.");
            System.out.println("Health check: http://127.0.0.1:" + port + "/health");
            System.out.println("Prediction endpoint: " + endpoint);
            System.out.println("Send POST JSON with feature column names to /predict.");
        } else System.out.println("API started, but deployment record could not be saved to MySQL.");
    }

    public static void monitorModel() {
        System.out.println("\n========== MONITOR DEPLOYED MODEL ==========");
        if (deployments.isEmpty()) { System.out.println("No deployments in this session. Deploy a trained and tested version first."); return; }
        displayDeployments(); int choice = readInt("Enter deployment number: ") - 1;
        if (choice < 0 || choice >= deployments.size()) { System.out.println("Invalid deployment."); return; }
        Deployment deployment = deployments.get(choice);
        boolean healthy = MLBridge.healthCheck(deployment.getEndpoint());
        double value = healthy ? 100.0 : 0.0;
        MonitoringRecord record = new MonitoringRecord(MonitoringRecordDAO.nextId(), "Service Availability", value, deployment);
        if (MonitoringRecordDAO.save(record)) { monitoringRecords.add(record); record.displayRecord(); }
        else System.out.println("Health check ran, but the monitoring record could not be saved.");
    }

    public static void updateModel() {
        System.out.println("\n========== UPDATE / RETRAIN MODEL ==========");
        System.out.println("Updating a model means training it again with a CSV dataset. The version must be tested again before deployment.");
        trainModel();
    }

    public static void maintainModel() {

        System.out.println(
                "\n========== MODEL MAINTENANCE =========="
        );

        ModelVersion version = selectModelVersion();

        if (version != null) {
            version.maintainModel();
            ModelVersionDAO.updateVersion(version);
        }
    }

    public static void displayModels() {

        System.out.println(
                "\n========== AVAILABLE MODELS =========="
        );

        if (models.isEmpty()) {
            System.out.println("No models available.");
            return;
        }

        for (int i = 0; i < models.size(); i++) {

            AIModel model = models.get(i);

            System.out.println(
                    (i + 1) + ". " +
                    model.getModelName() +
                    " [" +
                    model.getAlgorithm() +
                    "]"
            );
        }
    }

    public static void displayDatasets() {

        System.out.println(
                "\n========== AVAILABLE DATASETS =========="
        );

        for (int i = 0; i < datasets.size(); i++) {

            Dataset dataset = datasets.get(i);

            System.out.println(
                    (i + 1) + ". " +
                    dataset.getName()
            );
        }
    }

    public static void displayDeployments() {

        System.out.println(
                "\n========== DEPLOYMENTS =========="
        );

        if (deployments.isEmpty()) {
            System.out.println("No deployments available.");
            return;
        }

        for (int i = 0; i < deployments.size(); i++) {

            Deployment deployment = deployments.get(i);

            System.out.println(
                    (i + 1) +
                    ". Deployment " +
                    deployment.getDeploymentId() +
                    " [" +
                    deployment.getStatus() +
                    "]"
            );
        }
    }

    public static void displayMonitoringRecords() {

        System.out.println(
                "\n========== MONITORING RECORDS =========="
        );

        if (monitoringRecords.isEmpty()) {
            System.out.println(
                    "No monitoring records available."
            );
            return;
        }

        for (MonitoringRecord record : monitoringRecords) {
            record.displayRecord();
        }
    }

    public static ModelVersion selectModelVersion() {

        if (models.isEmpty()) {
            System.out.println("No models available.");
            return null;
        }

        displayModels();

        int modelChoice = readInt(
                "Enter model number: "
        ) - 1;

        if (modelChoice < 0 || modelChoice >= models.size()) {
            System.out.println("Invalid model.");
            return null;
        }

        AIModel model = models.get(modelChoice);

        if (model.getVersions().isEmpty()) {
            System.out.println(
                    "No versions available for this model."
            );
            return null;
        }

        model.displayVersions();

        int versionChoice = readInt(
                "Enter version number: "
        ) - 1;

        if (
                versionChoice < 0 ||
                versionChoice >= model.getVersions().size()
        ) {
            System.out.println("Invalid version.");
            return null;
        }

        return model.getVersions().get(versionChoice);
    }
}
