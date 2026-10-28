public class Deployment {

    private int deploymentId;
    private String environment;
    private String endpoint;
    private String status;
    private ModelVersion modelVersion;

    public Deployment(int deploymentId,
                      String environment,
                      String endpoint,
                      ModelVersion modelVersion) {
        this.deploymentId = deploymentId;
        this.environment = environment;
        this.endpoint = endpoint;
        this.modelVersion = modelVersion;
        this.status = "Not Deployed";
    }

    public void deployModel() {
        if (modelVersion.getStatus().equals("Ready for Deployment")) {
            status = "Deployed";
            modelVersion.setStatus("Deployed");

            System.out.println(
                    "\nModel version " +
                    modelVersion.getVersionNumber() +
                    " successfully deployed."
            );
        } else {
            System.out.println(
                    "\nDeployment failed."
            );

            System.out.println(
                    "Model must be ready for deployment first."
            );
        }
    }

    public void stopDeployment() {
        if (status.equals("Deployed")) {
            status = "Stopped";
            System.out.println("Deployment stopped successfully.");
        } else {
            System.out.println("No active deployment found.");
        }
    }

    public void displayDeployment() {
        System.out.println("\n--- Deployment Details ---");
        System.out.println("Deployment ID : " + deploymentId);
        System.out.println("Environment   : " + environment);
        System.out.println("Endpoint      : " + endpoint);
        System.out.println("Status        : " + status);
        System.out.println(
                "Model Version : " +
                modelVersion.getVersionNumber()
        );
    }

    public int getDeploymentId() {
        return deploymentId;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public ModelVersion getModelVersion() {
        return modelVersion;
    }
}
