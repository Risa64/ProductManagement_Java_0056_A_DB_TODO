/*
 * 【このクラスの役割】
 * アプリの入口。メニューを表示し、検索・登録・変更・削除の各機能へ処理を振り分ける。
 *
 * TODO【担当3：検索・共通】
 * ・起動時のCSV全件読込をやめ、担当1が作るProductRepositoryを各機能へ渡す形に変更する。
 * ・メニューでexit / \qが入力された場合は、メニューへ戻さずプログラムを終了する。
 * ・メニュー番号エラーの文言を仕様書どおりに確認する。
 * ・DB接続失敗時は仕様書のメッセージを表示してプログラムを終了する。担当1と連携する。
 */
package 商品管理システム;

import java.nio.charset.Charset;
// import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

		// 実行環境の文字コードに合わせてScannerを作り、日本語入力の文字化けを起こしにくくする。
		Charset inputCharset = System.console() != null
				? System.console().charset()
				: Charset.defaultCharset();

		try (Scanner scanner = new Scanner(System.in, inputCharset)) {
			InputScanner sc = new InputScanner(scanner);

			// 現在は起動時にCSVの全商品をArrayListへ読み込んでいる。
			// TODO【担当1＋担当3】DB版ではこの一覧を持たず、Repositoryを作って各機能へ渡す。
			// ArrayList<Product> products = CsvManager.readCsv();
			
			// DB 化
			ProductRepository repository = new PostgreSqlProductRepository();


			while (true) {
				System.out.print("[メニュー] 1:検索 2:登録 3:変更 4:削除 0:終了>");

				try {
					String menuNumber = sc.nextLine();

					// メニュー番号に応じて担当クラスへ処理を渡す。
					switch (menuNumber) {
					case "1" -> Search.search(repository, sc);
					case "2" -> Register.register(repository, sc);
					case "3" -> Update.update(repository, sc);
					case "4" -> Delete.delete(repository, sc);
					case "0" -> {
						System.out.println("プログラムを終了します。");
						return;
					}
					default -> System.out.println("正しい番号を入力してください。");
					}

				// 各機能でexit / \qが入力された場合はここへ戻ってくる。
				// TODO【担当3】メニュー入力中のexit / \qだけはプログラム終了になるよう区別する。
				} catch (CancelException e) {
					System.out.println("現在の処理を終了して、メニューに戻ります。");
				}
			}
		}
	}
}
