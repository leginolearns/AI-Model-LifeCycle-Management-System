import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatasetDAO {

    public static int nextId() {
        String sql = "SELECT COALESCE(MAX(dataset_id), 500) + 1 FROM datasets";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement st = c.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 501;
        } catch (SQLException e) { throw new IllegalStateException("Cannot generate dataset ID: " + e.getMessage(), e); }
    }

    public static boolean saveDataset(Dataset dataset) {

        String sql = "INSERT INTO datasets " +
                     "(dataset_id, name, source, records) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dataset.getDatasetId());
            stmt.setString(2, dataset.getName());
            stmt.setString(3, dataset.getSource());
            stmt.setInt(4, dataset.getRecords());

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error saving dataset: " + e.getMessage());
            return false;
        }
    }

    public static java.util.ArrayList<Dataset> loadAll() {
        java.util.ArrayList<Dataset> result = new java.util.ArrayList<>();
        String sql = "SELECT dataset_id, name, source, records FROM datasets ORDER BY dataset_id";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement st = c.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) result.add(new Dataset(rs.getInt("dataset_id"), rs.getString("name"), rs.getString("source"), rs.getInt("records")));
        } catch (SQLException e) { System.out.println("Could not load datasets: " + e.getMessage()); }
        return result;
    }

    public static void displayDatasets() {

        String sql = "SELECT * FROM datasets";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n========== DATABASE DATASETS ==========");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("Dataset ID: " + rs.getInt("dataset_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Source: " + rs.getString("source"));
                System.out.println("Records: " + rs.getInt("records"));
                System.out.println("---------------------------------------");
            }

            if (!found) {
                System.out.println("No datasets found in database.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving datasets: " + e.getMessage());
        }
    }
}