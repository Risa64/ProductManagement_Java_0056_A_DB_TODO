/*
 * 【このクラスの役割】
 * exit / \qが入力されたことを、通常の入力値とは別の形で呼び出し元へ伝えるための独自例外。
 *
 * TODO【担当3：検索・共通】
 * ・各機能中はメニューへ戻る、メニュー中は終了する、という仕様に合わせて使い方を整理する。
 */
package 商品管理システム;

public class CancelException extends RuntimeException {
}
