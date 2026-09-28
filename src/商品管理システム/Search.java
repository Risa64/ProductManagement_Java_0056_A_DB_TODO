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

import java.util.ArrayList;
import java.util.List;

public class Search {

	public static void search(ArrayList<Product> products, InputScanner sc) {
		while (true) {
			System.out.println("'------------------------------------");
			System.out.println("商品情報を検索します。");
			System.out.println("検索キーワードを入力してください。");
			System.out.print("キーワード >");
			String keyword = sc.nextLine();
			// 入力されたキーワードに合う商品だけを集める。
			// TODO【担当3】DB版ではこの呼び出しをrepository.search(keyword)へ変更する。
			List<Product> result = searchProducts(products, keyword);
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
	private static List<Product> searchProducts(ArrayList<Product> products, String keyword) {
		List<Product> result = new ArrayList<>();

		for (Product product : products) {

			if (keyword.equals("")
					|| product.getProductId().contains(keyword)
					|| product.getProductCode().contains(keyword)
					|| product.getProductName().contains(keyword)) {

				result.add(product);
			}
		}

		// 仕様どおり商品IDの昇順に並べる。
		// TODO【担当3】DB版ではORDER BY product_idでDB側に並べてもらう。
		result.sort((a, b) -> a.getProductId().compareTo(b.getProductId()));

		return result;
	}
}
