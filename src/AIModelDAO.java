import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AIModelDAO {
    public static int nextId() {
        String sql = "SELECT COALESCE(MAX(model_id), 1000) + 1 FROM ai_models";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            return r.next() ? r.getInt(1) : 1001;
        } catch (SQLException e) { throw new IllegalStateException("Cannot generate model ID: " + e.getMessage(), e); }
    }
    public static boolean saveModel(AIModel model) {
        String sql = "INSERT INTO ai_models (model_id, model_name, algorithm, description) VALUES (?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setInt(1, model.getModelId()); s.setString(2, model.getModelName()); s.setString(3, model.getAlgorithm()); s.setString(4, model.getDescription());
            s.executeUpdate(); return true;
        } catch (SQLException e) { System.out.println("Error saving AI model: " + e.getMessage()); return false; }
    }
    public static java.util.ArrayList<AIModel> loadAll() {
        java.util.ArrayList<AIModel> result = new java.util.ArrayList<>();
        String sql = "SELECT model_id, model_name, algorithm, description FROM ai_models ORDER BY model_id";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement st = c.prepareStatement(sql); ResultSet rs = st.executeQuery()) {
            while (rs.next()) result.add(new AIModel(rs.getInt("model_id"), rs.getString("model_name"), rs.getString("algorithm"), rs.getString("description")));
        } catch (SQLException e) { System.out.println("Could not load models: " + e.getMessage()); }
        return result;
    }

    public static void displayModels() {
        String sql = "SELECT * FROM ai_models ORDER BY model_id";
        try (Connection c = DatabaseConnection.getConnection(); PreparedStatement s = c.prepareStatement(sql); ResultSet r = s.executeQuery()) {
            System.out.println("\n========== DATABASE MODELS =========="); boolean found=false;
            while (r.next()) { found=true; System.out.println("ID: " + r.getInt("model_id") + " | Name: " + r.getString("model_name") + " | Algorithm: " + r.getString("algorithm") + " | Description: " + r.getString("description")); }
            if (!found) System.out.println("No models stored in database.");
        } catch (SQLException e) { System.out.println("Error reading models: " + e.getMessage()); }
    }
}
