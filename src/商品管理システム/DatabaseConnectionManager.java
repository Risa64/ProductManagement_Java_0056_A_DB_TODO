package 商品管理システム;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {

    private DatabaseConnectionManager() {
    }

    public static Connection getConnection() throws SQLException, IOException {

        Properties properties = new Properties();

        try (FileReader reader = new FileReader("app.properties")) {
            properties.load(reader);
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");
        String driver = properties.getProperty("db.driver");

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("JDBCドライバーの読み込みに失敗しました。", e);
        }

        return DriverManager.getConnection(url, user, password);
    }
}