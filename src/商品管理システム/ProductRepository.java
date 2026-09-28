package 商品管理システム;

import java.sql.SQLException;
import java.util.List;

/*
 * 【このクラスの役割】
 * 各機能からDB操作を呼ぶための共通窓口。
 * Register / Search / Update / DeleteにSQLを直接書かず、DB処理をここで分離するために追加する。
 *
 * TODO【担当1：DB共通】
 * ・各メソッドをPostgreSqlProductRepositoryで実装する。
 * ・4人の機能担当と先にメソッド名・引数・戻り値を共有し、途中で頻繁に変更しない。
 */
public interface ProductRepository {
    // 検索結果は複数件になる可能性があるため、List<Product>で返す。
    public List<Product> searchProduct(String keyword) throws SQLException;

    // 商品IDで1商品を特定して取得するため、Productを1件返す。(更新/削除で使用)
    public Product findById(String id) throws SQLException;

    // 同じ商品IDが存在するか確認し、重複しているかをbooleanで返す。(登録で使用)
    public boolean existsById(String id) throws SQLException;

    // 商品コードの重複を確認する。excludeIdを指定すると、その商品自身は確認対象から除外する。(登録/更新で使用)
    public boolean existsByCode(String code, String excludeId) throws SQLException;

    // 1商品分の情報をProductで受け取り、登録後に返す値はないためvoid。
    public void registerProduct(Product product) throws SQLException;

    // Productを更新し、versionNoで楽観ロックを行う。更新できたかをbooleanで返す。
    public boolean updateProduct(Product product, int versionNo) throws SQLException;

    // 商品IDで対象を特定し、versionNoで楽観ロックを行う。論理削除できたかをbooleanで返す。
    public boolean deleteProduct(String id, int versionNo) throws SQLException;

    /*
     * 【楽観ロック】
     * versionNoをDBのversion_noと照合し、他の処理で先に更新されていないか確認する。
     * 一致した場合だけ更新・削除を行い、先に変更されていた場合は処理しない。
     *
     * 【例外処理】
     * DB操作で発生するSQLExceptionのみを外部へ通知する。
     * app.propertiesの読み込みで発生するIOExceptionは内部処理のため、ここでは通知しない。
     */
}