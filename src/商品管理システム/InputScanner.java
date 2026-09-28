/*
 * 【このクラスの役割】
 * キーボード入力を1か所にまとめ、exit / \qによる中断や、共通の入力チェックを行う。
 *
 * TODO【担当3：検索・共通】
 * ・仕様書では更新機能以外で「null」は使用不可。Updateだけnullを許可できる仕組みにする。
 * ・カンマ禁止はDB版でも共通仕様なので残す。
 * ・メニュー入力中のexit / \qは「機能中断」ではなく「プログラム終了」になるようMainと調整する。
 */
package 商品管理システム;

import java.util.Scanner;

public class InputScanner {
	
	// Mainで作ったScannerを使い回す。Scannerを各機能で何個も作らないためのフィールド。
	private final Scanner scanner;

	public InputScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	public String nextLine() {
		while (true) {
			String input = scanner.nextLine();

			
			
			// 中断コマンドは普通の文字列として返さず、CancelExceptionで一気に呼び出し元へ知らせる。
			if (input.equals("exit")
					|| input.equals("\\q")
					|| input.equals("¥q")) {
				
				throw new CancelException();
			}

			
			// カンマ禁止はCSVの都合だけではなく、DB版の共通入力仕様にも残っている。
			if (input.contains(",")) {
				System.out.println("カンマは使用できません。");
				System.out.print("再入力 >");
				
				continue;
			}

			return input;
		}
	}
}
