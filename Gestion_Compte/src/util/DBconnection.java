package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_banque?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // changer
    private static final String PASSWORD = ""; // changer

    public static Connection getConnection() throws SQLException {
        return 	DriverManager.getConnection("jdbc:mysql://localhost:3306/gestion_banque", "root", "");

    }
}



