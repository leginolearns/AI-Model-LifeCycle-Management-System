import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Runs the real scikit-learn engine from the Java lifecycle application. */
public class MLBridge {
    private static final Map<Integer, Process> SERVERS = new ConcurrentHashMap<>();

    private static Path projectRoot() throws IOException {
        Path cwd = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        if (Files.exists(cwd.resolve("ml_engine.py"))) return cwd;
        if (Files.exists(cwd.resolve(".." ).resolve("ml_engine.py"))) return cwd.resolve("..").normalize();
        throw new IOException("Cannot find ml_engine.py. Run from the project root or src folder.");
    }

    private static List<String> runEngine(String... args) throws IOException, InterruptedException {
        Path root = projectRoot();
        List<String> command = new ArrayList<>();
        String configured = System.getenv("PYTHON");
        command.add(configured == null || configured.isBlank() ? "python" : configured);
        command.add(root.resolve("ml_engine.py").toString());
        command.addAll(List.of(args));
        Process process = new ProcessBuilder(command).directory(root.toFile()).redirectErrorStream(true).start();
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                System.out.println("[ML] " + line);
            }
        }
        int exit = process.waitFor();
        if (exit != 0) {
            String error = lines.stream().filter(s -> s.startsWith("ERROR=")).findFirst().orElse("ML engine exited with code " + exit);
            throw new IOException(error + "\nInstall dependencies: python -m pip install -r requirements.txt");
        }
        return lines;
    }

    public static boolean train(int versionId, String algorithm, String csvPath, String target) {
        try {
            runEngine("train", String.valueOf(versionId), algorithm, csvPath, target);
            return true;
        } catch (Exception e) {
            System.out.println("Real model training failed: " + e.getMessage());
            return false;
        }
    }

    public static Double test(int versionId) {
        try {
            List<String> lines = runEngine("test", String.valueOf(versionId));
            for (String line : lines) {
                if (line.startsWith("ACCURACY=")) return Double.parseDouble(line.substring("ACCURACY=".length()));
            }
            if (lines.stream().anyMatch(s -> s.equals("RESULT=TESTED"))) return Double.NaN; // regression task; use MAE/RMSE/R2 in metrics JSON
            return null;
        } catch (Exception e) {
            System.out.println("Model testing failed: " + e.getMessage());
            return null;
        }
    }

    public static boolean hasArtifact(int versionId) {
        try { return Files.exists(projectRoot().resolve("artifacts").resolve("model_version_" + versionId + ".joblib")); }
        catch (IOException e) { return false; }
    }

    public static boolean startServer(int versionId, int port) {
        try {
            if (SERVERS.containsKey(versionId) && SERVERS.get(versionId).isAlive()) return true;
            Path root = projectRoot();
            Path log = root.resolve("artifacts").resolve("server_version_" + versionId + ".log");
            Files.createDirectories(log.getParent());
            String configured = System.getenv("PYTHON");
            String python = configured == null || configured.isBlank() ? "python" : configured;
            Process process = new ProcessBuilder(python, root.resolve("ml_engine.py").toString(), "serve", String.valueOf(versionId), String.valueOf(port))
                    .directory(root.toFile()).redirectErrorStream(true).redirectOutput(log.toFile()).start();
            SERVERS.put(versionId, process);
            for (int i = 0; i < 20; i++) {
                if (healthCheck("http://127.0.0.1:" + port)) return true;
                if (!process.isAlive()) break;
                Thread.sleep(250);
            }
            System.out.println("Prediction API did not start. Check " + log);
            return false;
        } catch (Exception e) {
            System.out.println("Could not start prediction API: " + e.getMessage());
            return false;
        }
    }

    public static boolean healthCheck(String endpoint) {
        try {
            String base = endpoint.replaceAll("/predict/?$", "").replaceAll("/health/?$", "");
            HttpURLConnection connection = (HttpURLConnection) new URL(base + "/health").openConnection();
            connection.setConnectTimeout(500); connection.setReadTimeout(500); connection.setRequestMethod("GET");
            int code = connection.getResponseCode(); connection.disconnect();
            return code == 200;
        } catch (Exception e) { return false; }
    }
}
