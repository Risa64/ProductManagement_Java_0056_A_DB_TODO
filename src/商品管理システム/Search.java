/*
 * 【このクラスの役割】
 * 入力されたキーワードで商品を検索し、該当件数と商品一覧を表示する。
 *
 * TODO【担当3：検索・共通】
 * ・ArrayListを直接検索せず、ProductRepository.search(keyword)でDBを検索する形に変更する。
 * ・SELECTではdeleted=0の商品のみ対象にし、商品ID昇順で取得する。
 * ・空文字なら未削除の商品を全件表示する。
 * ・DB/SQLエラー時は仕様書どおり機能を中断してメニューへ戻る。担当1と連携する。
 */
package 商品管理システム;

import java.sql.SQLException;
import java.util.List;

public class Search {

	/**
	 * @佐伯
	 */
	public static void search(ProductRepository repository, InputScanner sc) {

		while (true) {
			System.out.println("'------------------------------------");
			System.out.println("商品情報を検索します。");
			System.out.println("検索キーワードを入力してください。");
			System.out.print("キーワード >");
			String keyword = sc.nextLine();

			// 入力されたキーワードに合う商品だけを集める。
			List<Product> result;

			try {
				result = searchProducts(repository, keyword);
			} catch (SQLException e) {
				System.out.println("処理を実行できませんでした。システム管理者に連絡してください。");
                e.printStackTrace();
                return;
			}

			System.out.println("検索結果は" + result.size() + "件です。");
			System.out.println("'------------------------------------");
			for (Product product : result) {
				System.out.println(product);
			}
			System.out.println("'------------------------------------");

			while (true) {
				System.out.println("続けて商品を検索しますか？");
				System.out.print("1:続けて検索する 2:メニューへ戻る > ");
				String continueInput = sc.nextLine();
				
				if (continueInput.equals("1")) {
					break;
				} else if (continueInput.equalsIgnoreCase("2")) {
					return;
				} else {
					System.out.println("正しい番号を入力してください。");
				}
			}
		}
	}

	// 商品ID・商品コード・商品名のどれかにキーワードが含まれれば検索結果に入れる。
	// 空文字の場合は全商品が条件に一致する。
	/**
	 * @佐伯
	 */
	private static List<Product> searchProducts(ProductRepository repository, String keyword) throws SQLException {

		return repository.searchProduct(keyword);
		
	}
}
