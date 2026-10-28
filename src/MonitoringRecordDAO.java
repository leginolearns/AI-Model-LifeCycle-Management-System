import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MonitoringRecordDAO {
    public static int nextId() {
        String q="SELECT COALESCE(MAX(record_id),800)+1 FROM monitoring_records";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(q); ResultSet r=s.executeQuery()){return r.next()?r.getInt(1):801;}
        catch(SQLException e){throw new IllegalStateException("Cannot generate monitoring ID: "+e.getMessage(),e);}
    }
    public static boolean save(MonitoringRecord r) {
        String q="INSERT INTO monitoring_records (record_id, deployment_id, metric, value, status, timestamp) VALUES (?,?,?,?,?,?)";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(q)){
            s.setInt(1,r.getRecordId()); s.setInt(2,r.getDeployment().getDeploymentId()); s.setString(3,r.getMetric()); s.setDouble(4,r.getValue()); s.setString(5,r.getStatus()); s.setTimestamp(6,Timestamp.valueOf(r.getTimestamp())); s.executeUpdate(); return true;
        } catch(SQLException e){System.out.println("Error saving monitoring record: "+e.getMessage());return false;}
    }
    public static void displayRecords() {
        String q="SELECT * FROM monitoring_records ORDER BY record_id";
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement s=c.prepareStatement(q); ResultSet r=s.executeQuery()){
            System.out.println("\n========== DATABASE MONITORING RECORDS =========="); boolean found=false;
            while(r.next()){found=true;System.out.println("Record "+r.getInt("record_id")+" | Deployment "+r.getInt("deployment_id")+" | "+r.getString("metric")+"="+r.getDouble("value")+" | "+r.getString("status")+" | "+r.getTimestamp("timestamp"));}
            if(!found)System.out.println("No monitoring records found in database.");
        } catch(SQLException e){System.out.println("Error reading monitoring records: "+e.getMessage());}
    }
}
