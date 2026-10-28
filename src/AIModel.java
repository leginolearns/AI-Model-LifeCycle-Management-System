import java.util.ArrayList;

public class AIModel {

    private int modelId;
    private String modelName;
    private String algorithm;
    private String description;

    private ArrayList<ModelVersion> versions;

    public AIModel(int modelId, String modelName, String algorithm) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.algorithm = algorithm;
        this.description = "No description available";
        this.versions = new ArrayList<>();
    }

    // Constructor overloading
    public AIModel(int modelId, String modelName,
                   String algorithm, String description) {
        this.modelId = modelId;
        this.modelName = modelName;
        this.algorithm = algorithm;
        this.description = description;
        this.versions = new ArrayList<>();
    }

    public int getModelId() {
        return modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<ModelVersion> getVersions() {
        return versions;
    }

    public void addVersion(ModelVersion version) {
        versions.add(version);

        System.out.println(
                "Version " + version.getVersionNumber() +
                " added to model " + modelName
        );
    }

    public void displayVersions() {
        System.out.println("\n=== Model Versions ===");

        if (versions.isEmpty()) {
            System.out.println("No versions available.");
            return;
        }

        for (ModelVersion version : versions) {
            version.displayVersion();
        }
    }

    public void displayModel() {
        System.out.println("\n==============================");
        System.out.println("       AI MODEL DETAILS");
        System.out.println("==============================");

        System.out.println("Model ID      : " + modelId);
        System.out.println("Model Name    : " + modelName);
        System.out.println("Algorithm     : " + algorithm);
        System.out.println("Description   : " + description);
        System.out.println("Total Versions: " + versions.size());
    }
}
