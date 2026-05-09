import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + "localhost:3306" + "/" + "student_management_system"; //Database URL and localhost port.
        String username = "application"; //Update with database username as needed.
        String password = "H84-3IX2r=lf"; //Update with database password as needed.
        return DriverManager.getConnection(url, username, password); //Get a connection to the database with the credentials provided.
    } 
}
