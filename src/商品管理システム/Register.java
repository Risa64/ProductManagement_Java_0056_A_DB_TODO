package 商品管理システム;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Register {

    public static void register(ProductRepository repository, InputScanner sc) {

        while (true) {
            try {
                System.out.println("------------------------------------");
                System.out.println("商品情報を登録します。");
                System.out.println("商品情報を入力してください。");
                System.out.println("------------------------------------");

                System.out.print("商品ID >");
                String productId = inputProductId(repository, sc);

                System.out.print("商品コード >");
                String productCode = inputProductCode(repository, sc);

                System.out.print("商品名 >");
                String productName = inputProductName(sc);

                System.out.print("商品分類 >");
                String category = inputCategory(sc);

                System.out.print("販売単価 >");
                Integer sellingPrice = inputSellingPrice(sc);

                System.out.print("仕入単価 >");
                Integer purchasePrice = inputPurchasePrice(sc);

                System.out.print("登録日 >");
                LocalDate registrationDate = inputRegistrationDate(sc);

                Product product = new Product(
                        productId,
                        productCode,
                        productName,
                        category,
                        sellingPrice,
                        purchasePrice,
                        registrationDate);

                while (true) {
                    System.out.print("商品情報を登録しますか？ Y/N >");
                    String answer = sc.nextLine();

                    if (answer.equalsIgnoreCase("Y")) {
                        // 入力後、INSERTの直前にも重複を確認する。
                        if (productIdDuplicateCheck(repository, productId)) {
                            System.out.println(
                                    "この商品IDはすでに使用されています。メニューに戻ります。");
                            return;
                        }

                        if (productCodeDuplicateCheck(repository, productCode)) {
                            System.out.println(
                                    "この商品コードはすでに使用されています。メニューに戻ります。");
                            return;
                        }

                        repository.registerProduct(product);

                        System.out.println("商品情報を登録しました。");
                        System.out.println("------------------------------------");
                        System.out.println("商品ID = " + product.getProductId());
                        System.out.println("商品コード = " + product.getProductCode());
                        System.out.println("商品名 = " + product.getProductName());
                        System.out.println("商品分類 = " + product.getCategory());
                        System.out.println("販売単価 = " + product.getSellingPrice());
                        System.out.println("仕入単価 = " + product.getPurchasePrice());
                        System.out.println("登録日 = " + product.getRegistrationDate());
                        System.out.println("------------------------------------");
                        break;

                    } else if (answer.equalsIgnoreCase("N")) {
                        System.out.println("商品登録を終了します。");
                        break;

                    } else {
                        System.out.println("YまたはNを入力してください。");
                    }
                }

                while (true) {
                    System.out.print(
                            "1:続けて登録する 2:メニューへ戻る >");
                    String continueInput = sc.nextLine();

                    if (continueInput.equals("1")) {
                        break;
                    } else if (continueInput.equals("2")) {
                        return;
                    } else {
                        System.out.println("1または2を入力してください。");
                    }
                }

            } catch (SQLException e) {
                System.out.println(
                        "処理を実行できませんでした。システム管理者に連絡してください。");
                e.printStackTrace();
                return;
            }
        }
    }

    private static String inputProductId(
            ProductRepository repository, InputScanner sc)
            throws SQLException {

        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (!input.matches("[a-zA-Z0-9_-]+")) {
                System.out.println(
                        "商品IDは半角英数字・半角アンダースコア・半角ハイフンで入力してください。");
            } else if (input.length() != 10) {
                System.out.println("商品IDは１０桁で入力してください。");
            } else if (productIdDuplicateCheck(repository, input)) {
                System.out.println(
                        "この商品IDはすでに使用されています。新しい商品IDを設定してください。");
            } else {
                return input;
            }
        }
    }

    private static boolean productIdDuplicateCheck(
            ProductRepository repository, String id)
            throws SQLException {

        return repository.existsById(id);
    }

    private static String inputProductCode(
            ProductRepository repository, InputScanner sc)
            throws SQLException {

        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (!input.matches("[0-9]+")) {
                System.out.println("商品コードは半角数字で入力してください。");
            } else if (input.length() != 13) {
                System.out.println("商品コードは１３桁で入力してください。");
            } else if (productCodeDuplicateCheck(repository, input)) {
                System.out.println(
                        "この商品コードはすでに使用されています。新しい商品コードを設定してください。");
            } else {
                return input;
            }
        }
    }

    private static boolean productCodeDuplicateCheck(
            ProductRepository repository, String code)
            throws SQLException {

        // 登録では除外する自分の商品IDがないので、第2引数はnull。
        return repository.existsByCode(code, null);
    }

    private static String inputProductName(InputScanner sc) {
        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (input.isEmpty()) {
                System.out.println("商品名を入力してください。");
            } else if (input.length() > 100) {
                System.out.println("商品名は１００文字以下で入力してください。");
            } else {
                return input;
            }
        }
    }

    private static String inputCategory(InputScanner sc) {
        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (input.isEmpty()) {
                System.out.println("商品分類を入力してください。");
            } else if (input.length() > 100) {
                System.out.println("商品分類は１００文字以下で入力してください。");
            } else {
                return input;
            }
        }
    }

    private static Integer inputSellingPrice(InputScanner sc) {
        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (!input.matches("[0-9]+")) {
                System.out.println("販売単価は半角数字で入力してください。");
            } else if (input.length() > 8) {
                System.out.println("販売単価は８桁以下で入力してください。");
            } else {
                return Integer.valueOf(input);
            }
        }
    }

    private static Integer inputPurchasePrice(InputScanner sc) {
        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (!input.matches("[0-9]+")) {
                System.out.println("仕入単価は半角数字で入力してください。");
            } else if (input.length() > 8) {
                System.out.println("仕入単価は８桁以下で入力してください。");
            } else {
                return Integer.valueOf(input);
            }
        }
    }

    private static LocalDate inputRegistrationDate(InputScanner sc) {
        while (true) {
            String input = sc.nextLine();

            if (isNullInput(input)) {
                System.out.println("登録時にnullは入力できません。");
            } else if (!input.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
                System.out.println(
                        "登録日はyyyy-MM-dd形式で入力してください。");
            } else {
                try {
                    return LocalDate.parse(input);
                } catch (DateTimeParseException e) {
                    System.out.println(
                            "実在する日付を入力してください。");
                }
            }
        }
    }

    private static boolean isNullInput(String input) {
        return input.equalsIgnoreCase("null");
    }
}










// /*
//  * 【このクラスの役割】
//  * 新しい商品の入力、入力チェック、登録確認、登録後の表示を担当する。
//  *
//  * TODO【担当2：登録】
//  * ・ArrayListへのaddとCSV保存をやめ、ProductRepository.insert(product)でDBへ登録する。
//  * ・INSERT直前に商品IDと商品コードをDBでもう一度重複確認し、重複時はメニューへ戻る。
//  * ・重複チェックではdeleted=1の論理削除済み商品を対象外にする。
//  * ・商品名/商品分類は「100バイト」ではなく仕様書どおり「100文字以下」に修正する。
//  * ・登録日は8桁入力ではなくyyyy-MM-dd形式（10文字）＋実在日付チェックに修正する。
//  * ・更新機能以外ではnull入力不可という共通仕様に対応する。
//  */
// package 商品管理システム;

// import java.nio.charset.Charset;
// import java.util.ArrayList;

// public class Register {

// 	public static void register(ArrayList<Product> products, InputScanner sc) {

// 		while (true) {
// 			System.out.println("------------------------------------");
// 			System.out.println("商品情報を登録します。");
// 			System.out.println("商品情報を入力してください。");
// 			System.out.println("------------------------------------");
// 			System.out.print("商品ID >");
// 			String productId = inputProductId(products, sc);
// 			System.out.print("商品コード >");
// 			String productCode = inputProductCode(products, sc);
// 			System.out.print("商品名 >");
// 			String productName = inputProductName(sc);
// 			System.out.print("商品分類 >");
// 			String category = inputCategory(sc);
// 			System.out.print("販売単価 >");
// 			String sellingPrice = inputSellingPrice(sc);
// 			System.out.print("仕入単価 >");
// 			String purchasePrice = inputPurchasePrice(sc);
// 			System.out.print("登録日 >");
// 			String registrationDate = inputRegistrationDate(sc);

// 			// 入力チェックが終わった7項目を、登録する1商品としてまとめる。
// 			Product product = new Product(
// 					productId,
// 					productCode,
// 					productName,
// 					category,
// 					sellingPrice,
// 					purchasePrice,
// 					registrationDate);

// 			while (true) {
// 				System.out.print("商品情報を登録しますか？ Y/N >");
// 				String answer = sc.nextLine();

// 				if (answer.equalsIgnoreCase("Y")) {
// 					// TODO【担当2】ここが登録を確定する場所。
// 					// DB版ではINSERT直前に商品ID・商品コードを再チェックしてからrepository.insert(product)を呼ぶ。
// 					products.add(product);
					
// 					CsvManager.writeCsv(products);
// 					System.out.println("商品情報を登録しました。");
// 					System.out.println("------------------------------------");
// 					System.out.println("商品ID = " + product.getProductId());
// 					System.out.println("商品コード = " + product.getProductCode());
// 					System.out.println("商品名 = " + product.getProductName());
// 					System.out.println("商品分類 = " + product.getCategory());
// 					System.out.println("販売単価 = " + product.getSellingPrice());
// 					System.out.println("仕入単価 = " + product.getPurchasePrice());
// 					System.out.println("登録日 = " + product.getRegistrationDate());
// 					System.out.println("------------------------------------");
// 					break;

// 				} else if (answer.equalsIgnoreCase("N")) {
// 					System.out.println("商品登録を終了します。");
// 					break;

// 				} else {
// 					System.out.println("YまたはNを入力してください。");
// 				}
// 			}

// 			while (true) {
// 				System.out.print("1:続けて登録する 2:メニューへ戻る >");

// 				String continueInput = sc.nextLine();

// 				if (continueInput.equals("1")) {
// 					break;

// 				} else if (continueInput.equals("2")) {
// 					return;

// 				} else {
// 					System.out.println("1または2を入力してください。");
// 				}
// 			}

// 		}
// 	}

	
// 	private static String inputProductId(ArrayList<Product> products, InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
			
// 			if (!input.matches("[a-zA-Z0-9_-]+")) {
// 				System.out.println("商品IDは半角英数字・半角アンダースコア・半角ハイフンで入力してください。");
// 			} else if (input.length() != 10) {
// 				System.out.println("商品IDは１０桁で入力してください。");
// 			} else if (productIdDuplicateCheck(products, input)) {
// 				System.out.println("この商品IDはすでに使用されています。新しい商品IDを設定してください。");
// 			} else {
// 				return input;
// 			}
// 		}

// 	}

	
// 	// 入力中の重複チェック。現在は起動時に読み込んだArrayListだけを確認している。
// 	// TODO【担当2】DB版ではdeleted=0を条件にDBへ問い合わせる。
// 	private static boolean productIdDuplicateCheck(ArrayList<Product> products, String input) {
// 		for (Product product : products) {
// 			if (input.equals(product.getProductId())) {
// 				return true;
// 			}
// 		}
// 		return false;
// 	}

	
// 	private static String inputProductCode(ArrayList<Product> products, InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
// 			if (!input.matches("[0-9]+")) {
// 				System.out.println("商品コードは半角数字で入力してください。");

// 			} else if (input.length() != 13) {
// 				System.out.println("商品コードは１３桁で入力してください。");

// 			} else if (productCodeDuplicateCheck(products, input)) {
// 				System.out.println("この商品コードはすでに使用されています。新しい商品コードを設定してください。");

// 			} else {
// 				return input;
// 			}
// 		}
// 	}

	
// 	// 商品コードも同様に、現在はArrayList内の重複を確認している。
// 	// TODO【担当2】DB版ではdeleted=0を条件にDBへ問い合わせる。
// 	private static boolean productCodeDuplicateCheck(ArrayList<Product> products, String input) {
// 		for (Product product : products) {
// 			if (input.equals(product.getProductCode())) {
// 				return true;
// 			}
// 		}
// 		return false;
// 	}

	
// 	private static String inputProductName(InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
// 			if (input.isEmpty()) {
// 				System.out.println("商品名を入力してください。");

// 			// TODO【担当2】現状はMS932で100バイト。DB版仕様は「100文字以下」なのでlength()基準へ変更する。
// 			} else if (input.getBytes(Charset.forName("MS932")).length > 100) {
// 				System.out.println("商品名は１００バイト（全角５０文字）以下で入力してください。");

// 			} else {
// 				return input;
// 			}
// 		}
// 	}

	
// 	private static String inputCategory(InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
// 			if (input.isEmpty()) {
// 				System.out.println("商品分類を入力してください。");

// 			// TODO【担当2】商品分類も100バイトではなく「100文字以下」に修正する。
// 			} else if (input.getBytes(Charset.forName("MS932")).length > 100) {
// 				System.out.println("商品分類は１００バイト（全角５０文字）以下で入力してください。");

// 			} else {
// 				return input;
// 			}
// 		}
// 	}

	
// 	private static String inputSellingPrice(InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
// 			if (!input.matches("[0-9]+")) {
// 				System.out.println("販売単価は半角数字で入力してください。");

// 			} else if (input.length() > 8) {
// 				System.out.println("販売単価は８桁以下で入力してください。");

// 			} else {
// 				return input;
// 			}
// 		}
// 	}

	
// 	private static String inputPurchasePrice(InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
// 			if (!input.matches("[0-9]+")) {
// 				System.out.println("仕入単価は半角数字で入力してください。");

// 			} else if (input.length() > 8) {
// 				System.out.println("仕入単価は８桁以下で入力してください。");

// 			} else {
// 				return input;
// 			}
// 		}
// 	}

	
// 	// TODO【担当2】現在はyyyyMMddの8桁。仕様書ではyyyy-MM-ddの10文字なので、
// 	// LocalDate.parseなどを使って書式と実在日付の両方を確認する。
// 	private static String inputRegistrationDate(InputScanner sc) {
// 		while (true) {
// 			String input = sc.nextLine();
// 			if (!input.matches("[0-9]{8}")) {
// 				System.out.println("登録日は８桁の日付で入力してくだい。");

// 			} else {
// 				try {

// 					int year = Integer.parseInt(input.substring(0, 4));
// 					int month = Integer.parseInt(input.substring(4, 6));
// 					int day = Integer.parseInt(input.substring(6, 8));

// 					java.time.LocalDate.of(year, month, day);

// 					return input;

// 				} catch (java.time.DateTimeException e) {

// 					System.out.println("登録日は日付ではありません。８桁の日付で入力してくだい。");
// 				}
// 			}
// 		}
// 	}

// }
