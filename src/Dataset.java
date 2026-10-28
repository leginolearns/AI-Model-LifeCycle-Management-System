public class Dataset {

    private int datasetId;
    private String name;
    private String source;
    private int records;

    public Dataset(int datasetId, String name, String source, int records) {
        this.datasetId = datasetId;
        this.name = name;
        this.source = source;
        this.records = records;
    }

    public int getDatasetId() {
        return datasetId;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public int getRecords() {
        return records;
    }

    public void displayDataset() {
        System.out.println("Dataset ID : " + datasetId);
        System.out.println("Name       : " + name);
        System.out.println("Source     : " + source);
        System.out.println("Records    : " + records);
    }
}
