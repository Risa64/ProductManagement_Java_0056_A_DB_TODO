/*
 * 【このクラスの役割】
 * CSV版で商品一覧を読み込み、変更後の一覧をCSVへ保存するクラス。
 *
 * TODO【担当1：DB共通】
 * ・DB版ではこのクラスを使用しない。DB移行完了後に削除する。
 * ・代わりにDatabaseConnectionManagerとPostgreSqlProductRepositoryを完成させる。
 */
package 商品管理システム;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class CsvManager {

	
	// CSVの1行をProductへ変換し、全商品をArrayListとして返す。
	// TODO【担当1】DB版ではSELECTへ置き換わるため、このメソッドは最終的に不要。
	public static ArrayList<Product> readCsv() {

		ArrayList<Product> products = new ArrayList<>();

		Path csvPath = Path.of("products_0056_A.csv");

		try {
			if (Files.notExists(csvPath)) {
				Files.createFile(csvPath);
				System.out.println("products_0056_A.csvを作成しました");
			}

			try (BufferedReader br = Files.newBufferedReader(csvPath, Charset.forName("MS932"))) {

				String line;

				while ((line = br.readLine()) != null) {

					
					// 1行のCSVをカンマで7項目に分解する。-1は末尾の空欄も残す指定。
					String[] data = line.split(",", -1);

					if (data.length != 7) {
						System.out.println("CSVの形式が不正な行を読み飛ばしました: " + line);
						
						continue;
					}

					Product product = new Product(
							data[0], 
							data[1], 
							data[2], 
							data[3], 
							data[4], 
							data[5], 
							data[6] 
					);

					products.add(product);

				}

			}
		
		} catch (IOException e) {
			System.out.println("CSVファイルの読み込みに失敗しました。");
			e.printStackTrace();
		}
		return products;
	}

	// 現在の全商品をCSVへ書き直す。
	// TODO【担当1】DB版は各機能がINSERT / UPDATEを直接実行するので、この一括保存は不要。
	public static void writeCsv(ArrayList<Product> products) {

		Path csvPath = Path.of("products_0056_A.csv");

		try (BufferedWriter bw = Files.newBufferedWriter(csvPath, Charset.forName("MS932"))) {
			
			for (Product product : products) {
				String line = product.getProductId() + "," +
						product.getProductCode() + "," +
						product.getProductName() + "," +
						product.getCategory() + "," +
						product.getSellingPrice() + "," +
						product.getPurchasePrice() + "," +
						product.getRegistrationDate();

				bw.write(line);
				
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("CSVファイルの書き込みに失敗しました");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
