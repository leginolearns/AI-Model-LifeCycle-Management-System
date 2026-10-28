import java.time.LocalDateTime;

public class MonitoringRecord {

    private int recordId;
    private String metric;
    private double value;
    private String status;
    private LocalDateTime timestamp;
    private Deployment deployment;

    public MonitoringRecord(int recordId,
                            String metric,
                            double value,
                            Deployment deployment) {
        this.recordId = recordId;
        this.metric = metric;
        this.value = value;
        this.deployment = deployment;
        this.timestamp = LocalDateTime.now();

        if (value >= 80) {
            this.status = "Healthy";
        } else if (value >= 50) {
            this.status = "Warning";
        } else {
            this.status = "Critical";
        }
    }

    public int getRecordId() {
        return recordId;
    }

    public String getMetric() {
        return metric;
    }

    public double getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public void displayRecord() {
        System.out.println("\n--- Monitoring Record ---");
        System.out.println("Record ID   : " + recordId);
        System.out.println("Metric      : " + metric);
        System.out.println("Value       : " + value);
        System.out.println("Status      : " + status);
        System.out.println("Timestamp   : " + timestamp);
        System.out.println("Deployment  : " + deployment.getStatus());
    }
}
