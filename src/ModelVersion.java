public class ModelVersion {

    private int versionId;
    private String versionNumber;
    private String status;
    private double accuracy;
    private Dataset dataset;

    public ModelVersion(int versionId, String versionNumber, Dataset dataset) {
        this.versionId = versionId;
        this.versionNumber = versionNumber;
        this.dataset = dataset;
        this.status = "Created";
        this.accuracy = 0.0;
    }

    public int getVersionId() {
        return versionId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getStatus() {
        return status;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setStatus(String status) { this.status = status; }

    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public void train() {
        status = "Trained";
        System.out.println(
                "Model version " + versionNumber + " has been trained."
        );
    }

    public void test(double accuracy) {
        this.accuracy = accuracy;
        status = "Tested";

        System.out.println(
                "Model version " + versionNumber +
                " tested with accuracy: " + accuracy + "%"
        );
    }

    public void deploy() {
        if (status.equals("Tested")) {
            status = "Ready for Deployment";
            System.out.println(
                    "Model version " + versionNumber +
                    " is ready for deployment."
            );
        } else {
            System.out.println(
                    "Model must be tested before deployment."
            );
        }
    }

    public void updateModel(double newAccuracy) {
        this.accuracy = newAccuracy;
        this.status = "Updated";

        System.out.println(
                "Model version " + versionNumber +
                " has been updated."
        );

        System.out.println(
                "New accuracy: " + newAccuracy + "%"
        );
    }

    public void maintainModel() {
        if (status.equals("Deployed") || status.equals("Updated")) {
            status = "Under Maintenance";

            System.out.println(
                    "Model version " + versionNumber +
                    " is now under maintenance."
            );
        } else {
            System.out.println(
                    "Model is not currently available for maintenance."
            );
        }
    }

    public void displayVersion() {
        System.out.println("\n--- Model Version Details ---");
        System.out.println("Version ID : " + versionId);
        System.out.println("Version    : " + versionNumber);
        System.out.println("Status     : " + status);
        System.out.println("Accuracy   : " + accuracy + "%");

        if (dataset != null) {
            System.out.println("Dataset    : " + dataset.getName());
        }
    }
}
