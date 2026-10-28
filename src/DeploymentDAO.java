import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeploymentDAO {
    public static int nextId() {
        String q="SELECT COALESCE(MAX(deployment_id),700)+1 FROM deployments";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(q); ResultSet r=s.executeQuery()){return r.next()?r.getInt(1):701;}
        catch(SQLException e){throw new IllegalStateException("Cannot generate deployment ID: "+e.getMessage(),e);}
    }
    public static java.util.ArrayList<Deployment> loadAll(java.util.ArrayList<AIModel> models) {
        java.util.ArrayList<Deployment> result = new java.util.ArrayList<>();
        String q="SELECT deployment_id, version_id, environment, endpoint, status FROM deployments ORDER BY deployment_id";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(q); ResultSet r=s.executeQuery()) {
            while(r.next()) {
                ModelVersion version=null;
                for(AIModel m:models) for(ModelVersion v:m.getVersions()) if(v.getVersionId()==r.getInt("version_id")) version=v;
                if(version!=null) { Deployment d=new Deployment(r.getInt("deployment_id"),r.getString("environment"),r.getString("endpoint"),version); d.setStatus(r.getString("status")); result.add(d); }
            }
        } catch(SQLException e) { System.out.println("Could not load deployments: "+e.getMessage()); }
        return result;
    }
    public static boolean save(Deployment d) {
        String q="INSERT INTO deployments (deployment_id, version_id, environment, endpoint, status) VALUES (?,?,?,?,?)";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(q)){
            s.setInt(1,d.getDeploymentId()); s.setInt(2,d.getModelVersion().getVersionId()); s.setString(3,d.getEnvironment()); s.setString(4,d.getEndpoint()); s.setString(5,d.getStatus()); s.executeUpdate(); return true;
        } catch(SQLException e){System.out.println("Error saving deployment: "+e.getMessage());return false;}
    }
    public static void updateStatus(Deployment d) {
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement("UPDATE deployments SET status=? WHERE deployment_id=?")){s.setString(1,d.getStatus());s.setInt(2,d.getDeploymentId());s.executeUpdate();}
        catch(SQLException e){System.out.println("Error updating deployment: "+e.getMessage());}
    }
}
