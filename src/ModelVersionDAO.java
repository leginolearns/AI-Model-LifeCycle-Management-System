import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ModelVersionDAO {

    public static int nextId() {
        String sql = "SELECT COALESCE(MAX(version_id), 0) + 1 FROM model_versions";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement st = c.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 1;
        } catch (SQLException e) { throw new IllegalStateException("Cannot generate version ID: " + e.getMessage(), e); }
    }

    public static boolean updateVersion(ModelVersion version) {
        String sql = "UPDATE model_versions SET status=?, accuracy=? WHERE version_id=?";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement st = c.prepareStatement(sql)) {
            st.setString(1, version.getStatus()); st.setDouble(2, version.getAccuracy()); st.setInt(3, version.getVersionId());
            return st.executeUpdate() > 0;
        } catch (SQLException e) { System.out.println("Error updating model version: " + e.getMessage()); return false; }
    }

    public static boolean saveVersion(ModelVersion version, int modelId) {

        String sql = "INSERT INTO model_versions " +
                     "(version_id, model_id, version_number, status, accuracy, dataset_id) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, version.getVersionId());
            stmt.setInt(2, modelId);
            stmt.setString(3, version.getVersionNumber());
            stmt.setString(4, version.getStatus());
            stmt.setDouble(5, version.getAccuracy());

            if (version.getDataset() != null) {
                stmt.setInt(6, version.getDataset().getDatasetId());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            System.out.println("Error saving model version: " + e.getMessage());
            return false;
        }
    }

    public static void loadInto(java.util.ArrayList<AIModel> models, java.util.ArrayList<Dataset> datasets) {
        String sql = "SELECT version_id, model_id, version_number, status, accuracy, dataset_id FROM model_versions ORDER BY version_id";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement st = c.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                int modelId = rs.getInt("model_id"); int datasetId = rs.getInt("dataset_id");
                AIModel owner = null; Dataset data = null;
                for (AIModel m : models) if (m.getModelId() == modelId) { owner = m; break; }
                for (Dataset d : datasets) if (d.getDatasetId() == datasetId) { data = d; break; }
                if (owner != null) {
                    ModelVersion v = new ModelVersion(rs.getInt("version_id"), rs.getString("version_number"), data);
                    v.setStatus(rs.getString("status")); v.setAccuracy(rs.getDouble("accuracy")); owner.getVersions().add(v);
                }
            }
        } catch (SQLException e) { System.out.println("Could not load model versions: " + e.getMessage()); }
    }

    public static void displayVersions() {

        String sql = "SELECT * FROM model_versions";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            System.out.println("\n========== DATABASE MODEL VERSIONS ==========");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("Version ID: " + rs.getInt("version_id"));
                System.out.println("Model ID: " + rs.getInt("model_id"));
                System.out.println("Version: " + rs.getString("version_number"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("Accuracy: " + rs.getDouble("accuracy"));
                System.out.println("Dataset ID: " + rs.getInt("dataset_id"));
                System.out.println("---------------------------------------------");
            }

            if (!found) {
                System.out.println("No model versions found in database.");
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving model versions: " + e.getMessage());
        }
    }
}