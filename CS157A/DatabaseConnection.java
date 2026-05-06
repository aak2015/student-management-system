
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + "localhost:3306" + "/" + "student_management_system";
        String username = "application";
        String password = "H84-3IX2r=lf"; 
        return DriverManager.getConnection(url, username, password);
    } 
}
