package 商品管理システム;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionCheck {
    public static void main(String[] args) {
        try (Connection con = DatabaseConnectionManager.getConnection()) {
            System.out.println("DB接続成功");
        } catch (SQLException | IOException e) {
            System.out.println("DB接続失敗");
            e.printStackTrace();
        }
    }
}