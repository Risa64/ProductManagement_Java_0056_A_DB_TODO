package 商品管理システム;

import java.sql.SQLException;

public class Delete {

    public static void delete(ProductRepository repository, InputScanner sc) {
        while (true) {
            System.out.println("商品情報を削除します。");
            System.out.println("削除する商品IDを入力してください");
            System.out.print("削除する商品ID > ");
            String productId = sc.nextLine();

            Product target;
            try {
                target = findProductById(repository, productId);
            } catch (SQLException e) {
                System.out.println("処理を実行できませんでした。システム管理者に連絡してください。");
                e.printStackTrace();
                return;
            }

            if (target == null) {
                System.out.println("存在しない商品IDです。");
                continue;
            }

            System.out.println("商品ID = " + target.getProductId());
            System.out.println("商品コード = " + target.getProductCode());
            System.out.println("商品名 = " + target.getProductName());
            System.out.println("商品分類 = " + target.getCategory());
            System.out.println("販売単価 = " + target.getSellingPrice());
            System.out.println("仕入単価 = " + target.getPurchasePrice());
            System.out.println("登録日 = " + target.getRegistrationDate());

            while (true) {
                System.out.print("この商品を削除しますか？ Y/N >");
                String answer = sc.nextLine();

                if (answer.equalsIgnoreCase("Y")) {
                    boolean deleted;
                    try {
                        deleted = repository.deleteProduct(
                                target.getProductId(),
                                target.getVersionNo());
                    } catch (SQLException e) {
                        System.out.println("処理を実行できませんでした。システム管理者に連絡してください。");
                        e.printStackTrace();
                        return;
                    }

                    if (!deleted) {
                        System.out.println("他の処理で商品情報が変更されたため、削除できませんでした。");
                        return;
                    }

                    System.out.println("商品情報を削除しました。");
                    System.out.println(target);
                    break;

                } else if (answer.equalsIgnoreCase("N")) {
                    System.out.println("削除をキャンセルしました。");
                    break;

                } else {
                    System.out.println("YまたはNを入力してください。");
                }
            }

            while (true) {
                System.out.println("続けて商品情報を削除しますか？");
                System.out.print("1:続けて削除する 2:メニューに戻る > ");
                String continueInput = sc.nextLine();

                if (continueInput.equals("1")) {
                    break;
                } else if (continueInput.equals("2")) {
                    return;
                } else {
                    System.out.println("正しい番号を入力してください。");
                }
            }
        }
    }

    private static Product findProductById(
            ProductRepository repository, String productId) throws SQLException {

        return repository.findById(productId);
    }
}














// /*
//  * 【このクラスの役割】
//  * 商品IDで削除対象を探し、確認後に商品を削除する処理を担当する。
//  *
//  * TODO【担当5：削除】
//  * ・ArrayListのremoveは使わず、ProductRepository.logicalDelete()でdeletedを更新する論理削除へ変更する。
//  * ・削除対象はDBから商品IDで取得し、deleted=0の商品のみ対象にする。
//  * ・version_noを使った楽観ロックを実装し、他ユーザーが先に変更していたらメニューへ戻る。
//  * ・削除成功後は仕様書どおり、削除した商品の情報を表示する。
//  * ・DB/SQLエラー時は仕様書どおり機能を中断してメニューへ戻る。担当1と連携する。
//  */
// package 商品管理システム;

// import java.util.ArrayList;
// //DB
// import java.sql.SQLException;

// public class Delete {

	
// 	public static void delete(ProductRepository repository, InputScanner sc) {
// 	// public static void delete(ArrayList<Product> products, InputScanner sc) {
// 		while (true) {
// 			System.out.println("商品情報を削除します。");
// 			System.out.println("削除する商品IDを入力してください");
// 			System.out.print("削除する商品ID > ");
// 			String productId = sc.nextLine();

// 			// 削除前に対象商品を取得して、存在確認と削除内容の表示に使う。
// 			// TODO【担当5】DB版ではrepository.findById()でdeleted=0の商品を取得し、version_noも保持する。

// 			Product target;

// 			try {
// 				target = findProductById(repository, productId); // Need to change Method name
// 			// Product target = findProductById(products, productId);
// 			} catch (SQLException e) {
// 				System.out.println("処理を実行できませんでした。システム管理者に連絡してください。");
//                 e.printStackTrace();
//                 return;
// 			}


// 			if (target == null) {
// 				System.out.println("存在しない商品IDです。");
				
// 				continue;
// 			}

// 			System.out.println("商品ID = " + target.getProductId());
// 			System.out.println("商品コード = " + target.getProductCode());
// 			System.out.println("商品名 = " + target.getProductName());
// 			System.out.println("商品分類 = " + target.getCategory());
// 			System.out.println("販売単価 = " + target.getSellingPrice());
// 			System.out.println("仕入単価 = " + target.getPurchasePrice());
// 			System.out.println("登録日 = " + target.getRegistrationDate());

// 			while (true) {
// 				System.out.print("この商品を削除しますか？ Y/N >");
// 				String answer = sc.nextLine();
				
// 				if (answer.equalsIgnoreCase("Y")) {
// 					// TODO【担当5】DB版ではremoveやDELETEを使わない。
// 					// productId＋version_noを条件にdeleted=1へUPDATEし、更新件数0なら同時更新エラーとする。
// 					// products.remove(target); // 不要
// 					// CsvManager.writeCsv(products); // 不要
					
// 					// DB
// 					boolean deleted;
// 					try {
// 						deleted = repository.deleteProduct(target.getProductId(), target.getVersionNo());
// 					} catch (SQLException e) {
// 						System.out.println("処理を実行できませんでした。システム管理者に連絡してください。");
//                         e.printStackTrace();
//                         return;
// 					}

// 					// DB
// 					if (!deleted) {
// 						System.out.println("他の処理で商品情報が変更されたため、削除できませんでした。");
//                         return;
// 					}

// 					System.out.println("商品情報を削除しました。");
	
// 					// DB
// 					System.out.println(target);
// 					break;

// 				} else if (answer.equalsIgnoreCase("N")) {
// 					System.out.println("削除をキャンセルしました。");
// 					break;

// 				} else {
// 					System.out.println("YまたはNを入力してください。");
// 				}
// 			}

// 			while (true) {
// 				System.out.println("続けて商品情報を削除しますか？");
// 				System.out.print("1:続けて削除する 2:メニューに戻る > ");
// 				String continueInput = sc.nextLine();
				
// 				if (continueInput.equals("1")) {
// 					break;
// 				} else if (continueInput.equals("2")) {
// 					return;
// 				} else {
// 					System.out.println("正しい番号を入力してください。");
// 				}
// 			}
// 		}
// 	}

	
// 	// 現在は起動時に読み込んだ全商品から、商品IDが同じものを順番に探している。
// 	// TODO【担当5】DB版ではSELECTで1件取得する。

// 	// DB
// 	private static Product findProductById(ProductRepository repository, String productId) throws SQLException {
// 	// private static Product findProductById(ArrayList<Product> products, String productId) {

// 		return repository.findById(productId);
		
// 		// for (Product product : products) {
// 		// 	if (productId.equals(product.getProductId())) {
// 		// 		return product;
// 		// 	}
// 		// }
// 		// return null;
// 	}
// }
