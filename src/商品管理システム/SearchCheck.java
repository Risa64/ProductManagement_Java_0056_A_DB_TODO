package 商品管理システム;

import java.util.Scanner;

public class SearchCheck {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            InputScanner sc = new InputScanner(scanner);
            ProductRepository repository = new PostgreSqlProductRepository();

            Search.search(repository, sc);
        }
    }
}