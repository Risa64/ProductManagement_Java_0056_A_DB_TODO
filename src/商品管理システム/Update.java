/*
 * 【このクラスの役割】
 * 商品IDで変更対象を探し、変更したい項目だけ入力して商品情報を更新する。
 *
 * TODO【担当4：変更】
 * ・ArrayListから探す処理をProductRepository.findById()へ変更する。
 * ・CSV保存をやめ、ProductRepository.update()でUPDATEする。
 * ・UPDATE直前に商品コードの重複をDBで再確認する。deleted=1は重複対象外。
 * ・version_noを使った楽観ロックを実装し、他ユーザーが先に変更していたらメニューへ戻る。
 * ・空欄は変更なし、文字列「null」はDBのNULLとして保存する形にする。
 * ・商品名/商品分類は100文字以下、登録日はyyyy-MM-dd＋実在日付チェックに修正する。
 */
package 商品管理システム;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class Update {

	public static void update(ArrayList<Product> products, InputScanner sc) {
		while (true) {
			System.out.println("------------------------------------");
			System.out.println("商品情報を変更します。");
			System.out.println("変更する商品IDを入力してください");
			System.out.print("商品ID >");
			// まず現在の商品を取得する。空欄入力時に「現在値をそのまま使う」ためにも必要。
			// TODO【担当4】DB版ではrepository.findById()で取得し、version_noも保持する。
			Product target = inputProductId(products, sc);
			System.out.println("------------------------------------");
			System.out.println("変更する項目のみ入力してください");
			System.out.println("------------------------------------");
			System.out.print("商品コード[" + target.getProductCode() + "] >");
			String productCode = inputProductCode(products, sc, target);
			System.out.print("商品名[" + target.getProductName() + "] >");
			String productName = inputProductName(sc, target);
			System.out.print("商品分類[" + target.getCategory() + "] >");
			String category = inputCategory(sc, target);
			System.out.print("販売単価[" + target.getSellingPrice() + "] >");
			String sellingPrice = inputSellingPrice(sc, target);
			System.out.print("仕入単価[" + target.getPurchasePrice() + "] >");
			String purchasePrice = inputPurchasePrice(sc, target);
			System.out.print("登録日[" + target.getRegistrationDate() + "] >");
			String registrationDate = inputRegistrationDate(sc, target);

			System.out.println("------------------------------------");
			System.out.println("商品ID = " + target.getProductId());
			System.out.println("商品コード = " + productCode);
			System.out.println("商品名 = " + productName);
			System.out.println("商品分類 = " + category);
			System.out.println("販売価格 = " + sellingPrice);
			System.out.println("仕入価格 = " + purchasePrice);
			System.out.println("登録日 = " + registrationDate);
			System.out.println("------------------------------------");

			while (true) {
				System.out.print("この内容で変更しますか？ Y/N >");
				String answer = sc.nextLine();
				if (answer.equalsIgnoreCase("y")) {
					// TODO【担当4】ここが更新確定地点。
					// DB版では商品コード重複の再確認 → version_no確認付きUPDATEの順に行う。
					target.setProductCode(productCode);
					target.setProductName(productName);
					target.setCategory(category);
					target.setSellingPrice(sellingPrice);
					target.setPurchasePrice(purchasePrice);
					target.setRegistrationDate(registrationDate);
					
					CsvManager.writeCsv(products);
					System.out.println("商品情報を変更しました。");
					break;
				} else if (answer.equalsIgnoreCase("n")) {
					System.out.println("変更をキャンセルしました");
					break;
				} else {
					System.out.println("YまたはNを入力してください。");
				}
			}

			while (true) {
				System.out.print("1:続けて変更しますか？ 2:メニューに戻りますか？ >");
				String continueInput = sc.nextLine();
				
				if (continueInput.equals("1")) {
					break;
				} else if (continueInput.equals("2")) {
					return;
				} else {
					System.out.println("1または2を入力してください。");
				}

			}
		}

	}

	
	// 変更対象の商品IDを探し、見つかったProductを返す。
	// TODO【担当4】DB版ではSELECTでdeleted=0の商品だけ取得する。
	private static Product inputProductId(ArrayList<Product> products, InputScanner sc) {
		while (true) {
			String input = sc.nextLine();
			
			for (Product product : products) {
				if (product.getProductId().equals(input)) {
					return product;
				}
			}
			System.out.println("存在しない商品IDです。商品IDを確認してください。");
		}
	}

	
	private static String inputProductCode(ArrayList<Product> products, InputScanner sc, Product target) {
		while (true) {
			String input = sc.nextLine();
			// 更新では空欄なら変更しない。「null」なら現在値を削除する、という仕様。
			if (input.isEmpty()) {
				return target.getProductCode();
			} else if (input.equalsIgnoreCase("null")) {
				// TODO【担当4】DB版では空文字ではなくSQLのNULLとして保存できる値にする。
				return "";
			
			} else if (!input.matches("[0-9]+")) {
				System.out.println("商品コードは半角数字で入力してください。");
			} else if (input.length() != 13) {
				System.out.println("商品コードは１３桁で入力してください。");
			} else if (productCodeDuplicateCheck(products, input, target)) {
				System.out.println("この商品コードはすでに使用されています。新しい商品コードを設定してください。");
			} else {
				return input;
			}

		}
	}

	
	// 自分自身以外で同じ商品コードが使われていないか確認する。
	// TODO【担当4】DB版ではdeleted=0かつ自分の商品IDを除外してDB検索する。
	private static boolean productCodeDuplicateCheck(ArrayList<Product> products, String input, Product target) {
		
		for (Product product : products) {
			if (product != target && input.equals(product.getProductCode())) {
				return true;
			}
		}
		return false;
	}

	
	private static String inputProductName(InputScanner sc, Product target) {
		while (true) {
			String input = sc.nextLine();
			if (input.isEmpty()) {
				return target.getProductName();
			} else if (input.equalsIgnoreCase("null")) {
				return "";
			// TODO【担当4】商品名は100バイトではなく「100文字以下」に修正する。
			} else if (input.getBytes(Charset.forName("MS932")).length > 100) {
				System.out.println("商品名は１００バイト（全角５０文字）以下で入力してください。");
			} else {
				return input;
			}
		}
	}

	
	private static String inputCategory(InputScanner sc, Product target) {
		while (true) {
			String input = sc.nextLine();
			if (input.isEmpty()) {
				return target.getCategory();
			} else if (input.equalsIgnoreCase("null")) {
				return "";
			// TODO【担当4】商品分類も100バイトではなく「100文字以下」に修正する。
			} else if (input.getBytes(Charset.forName("MS932")).length > 100) {
				System.out.println("商品分類は１００バイト（全角５０文字）以下で入力してください。");
			} else {
				return input;
			}
		}
	}

	
	private static String inputSellingPrice(InputScanner sc, Product target) {
		while (true) {
			String input = sc.nextLine();
			if (input.isEmpty()) {
				return target.getSellingPrice();
			} else if (input.equalsIgnoreCase("null")) {
				return "";
			} else if (!input.matches("[0-9]+")) {
				System.out.println("販売単価は半角数字で入力してください");
			} else if (input.length() > 8) {
				System.out.println("販売単価は8桁以下で入力してください");
			} else {
				return input;
			}
		}
	}

	
	private static String inputPurchasePrice(InputScanner sc, Product target) {
		while (true) {
			String input = sc.nextLine();
			if (input.isEmpty()) {
				return target.getPurchasePrice();
			} else if (input.equalsIgnoreCase("null")) {
				return "";
			} else if (!input.matches("[0-9]+")) {
				System.out.println("仕入単価は半角数字で入力してください");
			} else if (input.length() > 8) {
				System.out.println("仕入単価は8桁以下で入力してください");
			} else {
				return input;
			}
		}
	}

	
	// TODO【担当4】登録日は8桁ではなくyyyy-MM-dd＋実在日付チェックに修正する。
	// 空欄は現在値、nullはDBのNULLという更新ルールは残す。
	private static String inputRegistrationDate(InputScanner sc, Product target) {
		while (true) {
			String input = sc.nextLine();
			if (input.isEmpty()) {
				return target.getRegistrationDate();
			} else if (input.equalsIgnoreCase("null")) {
				return "";
			} else if (!input.matches("[0-9]+")) {
				System.out.println("登録日付は半角数字で入力してください");
			} else if (input.length() != 8) {
				System.out.println("登録日付は8桁で入力してください");
			} else {
				return input;
			}
		}
	}
}
