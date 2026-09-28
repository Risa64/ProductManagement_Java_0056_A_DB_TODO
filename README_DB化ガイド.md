# A班 Javaプロジェクト｜DB化注釈・TODO版

このプロジェクトは、A班のCSV版コードを**そのまま理解しやすくしながら、PostgreSQL版へ直す場所を明確にするための作業用コピー**です。

- 元々入っていたコメント・Javadoc・`@author`は削除しました。
- `println`、getter/setterなど、見れば分かる処理には説明を増やしていません。
- DB版仕様書と差がある場所には、担当者が分かる形で `TODO【担当○：...】` を入れています。
- 新規追加したDB用クラスは**ひな形のみ**で、まだ実装していません。
- 現在のCSV版コードの処理そのものは基本的に残しています。

---

## おすすめの5人分担

| 担当 | 主担当 | 主に触るクラス | やること |
|---|---|---|---|
| **担当1：DB共通** | DB・Repository | `ProductRepository` / `PostgreSqlProductRepository` / `DatabaseConnectionManager` / `DatabaseErrorHandler` / `CsvManager` | DB接続、SQL、論理削除、楽観ロック用のDB処理、CSV撤去 |
| **担当2：登録** | 登録機能 | `Register` | 入力仕様修正、重複チェック、INSERT直前の再確認、登録処理 |
| **担当3：検索・共通** | 検索＋全体のつなぎ | `Search` / `Main` / `InputScanner` / `CancelException` / `Product` | SELECT検索、MainのDB切替、共通入力、Product調整、結合確認補助 |
| **担当4：変更** | 変更機能 | `Update` | UPDATE、更新直前重複確認、空欄/null、楽観ロック |
| **担当5：削除** | 削除機能 | `Delete` | 論理削除、楽観ロック、削除後表示、結合テスト補助 |

### この分け方にした理由

A班は、`Register / Search / Update / Delete` がそれぞれ独立しているため、4人が各機能を持つと分かりやすいです。

一方、**DB接続やRepositoryを複数人が別々に作ると、SQLやConnectionの扱いがバラバラになりやすい**ため、DB共通部分は担当1に集約します。

検索は他機能より修正量が少なめなので、担当3には `Main / InputScanner / Product` などの共通部分と結合確認も持ってもらう想定です。

---

# DB版の完成イメージ

CSV版では、

`Main → ArrayList<Product> → Register / Search / Update / Delete → CsvManager`

という流れです。

DB版では、

`Main → Register / Search / Update / Delete → ProductRepository → PostgreSqlProductRepository → PostgreSQL`

へ変えていきます。

各機能クラスへSQLを直接書くのではなく、**SQLはPostgreSqlProductRepositoryへ集める**のがポイントです。

---

# クラスごとの役割と修正点

## Main.java
**今の役割**  
起動時にCSVを読み込み、メニュー番号によって4機能へ処理を渡します。

**DB化で必要なこと**
- `CsvManager.readCsv()` と `ArrayList<Product>`を外す。
- `PostgreSqlProductRepository`を作り、各機能へ渡す。
- メニュー中の `exit / \q` はプログラム終了にする。
- DB接続失敗時は仕様書どおり終了する。

**担当**：担当3（担当1と連携）

---

## Product.java
**今の役割**  
商品ID～登録日まで、1商品分の情報をまとめています。

**DB化で必要なこと**
- 変更・削除の楽観ロックに必要な `version_no` を扱えるようにする。
- 必要に応じて `sid / deleted / 作成日時 / 更新日時` も扱う。
- 登録日は仕様書の `yyyy-MM-dd` に合わせる。

**担当**：担当3

---

## Register.java
**今の役割**  
登録する7項目を入力・チェックし、`ArrayList`へ追加してCSVへ保存します。

**DB化で必要なこと**
- `products.add()` / `CsvManager.writeCsv()` → `repository.insert()`。
- INSERT直前に商品ID・商品コードをDBで再度重複確認。
- 論理削除済みの商品は重複対象外。
- 商品名・分類：100バイト → **100文字以下**。
- 登録日：8桁 → **yyyy-MM-dd**。
- `null`は更新以外では使用不可。

**担当**：担当2

---

## Search.java
**今の役割**  
ArrayListを1件ずつ見て、商品ID・商品コード・商品名にキーワードを含む商品を探しています。

**DB化で必要なこと**
- Javaのfor文検索 → SQLのSELECT。
- `deleted=0`のみ検索。
- 空文字なら全件。
- 商品ID昇順。

**担当**：担当3

---

## Update.java
**今の役割**  
商品IDで対象を探し、空欄なら変更なし、入力された項目だけ書き換えてCSV保存します。

**DB化で必要なこと**
- 商品取得をSELECTへ変更。
- 保存をUPDATEへ変更。
- UPDATE直前に商品コード重複を再確認。
- `version_no`で楽観ロック。
- 空欄＝変更なし。
- `null`＝DBのNULLとして既存値を削除。
- 100文字・yyyy-MM-dd仕様へ修正。

**担当**：担当4

---

## Delete.java
**今の役割**  
商品IDで商品を探し、確認後にArrayListからremoveしてCSV保存します。

**DB化で必要なこと**
- `remove()`禁止。
- `deleted=1`へUPDATEする論理削除。
- `version_no`による楽観ロック。
- 削除後に削除した商品の情報を表示。

**担当**：担当5

---

## InputScanner.java
**今の役割**  
入力を共通化し、exit / \q とカンマ入力を処理しています。

**DB化で必要なこと**
- カンマ禁止はそのまま。
- 更新機能以外で`null`を禁止する方法を追加。
- メニュー中のexit / \qだけはプログラム終了になるよう調整。

**担当**：担当3

---

## CancelException.java
**今の役割**  
入力中断を通常の戻り値と分けてMainへ伝えます。

**DB化で必要なこと**
- 基本的には再利用可能。
- 「機能中断」と「メニューでの終了」を区別できるよう使い方を整理。

**担当**：担当3

---

## CsvManager.java
**今の役割**  
CSVの読み込み・一括保存を行います。

**DB化で必要なこと**
- DB版完成後は使用しないため削除対象。

**担当**：担当1

---

# 今回追加したクラス

## ProductRepository.java
各機能からDB操作を呼ぶための**共通窓口**です。

機能担当は「どんなSQLを書くか」を考えず、

- `search()`
- `findById()`
- `insert()`
- `update()`
- `logicalDelete()`

などを呼ぶ想定です。

**担当**：担当1

---

## PostgreSqlProductRepository.java
実際にSQLを書く場所です。

- SELECT
- INSERT
- UPDATE
- 論理削除
- 重複確認
- version_noを使った楽観ロック

をここへまとめます。

**担当**：担当1

---

## DatabaseConnectionManager.java
`app.properties`を読み、PostgreSQLへのConnectionを返すクラスです。

DB接続処理を各機能に重複して書かないために追加しています。

**担当**：担当1

---

## DatabaseErrorHandler.java
DB・SQLエラー時の共通メッセージをまとめるクラスです。

**担当**：担当1

---

## app.properties
DB接続情報をソースコードの外で管理します。

`X`は講師から指定された値へ変更してください。

**担当**：担当1

---

# おすすめの作業順

1. **5人でRepositoryのメソッドを確認する**
   - 名前
   - 引数
   - 戻り値
2. **担当1がDB接続とRepositoryの土台を作る**
3. 担当2～5が、自分の機能をRepository利用へ変更する
4. 担当3がMainをDB版へつなぐ
5. 5人で結合テスト
6. CSV依存がなくなったことを確認して`CsvManager`を削除

特に重要なのは、**担当1が途中でRepositoryのメソッド仕様を何度も変えないこと**です。  
最初に5人で共通窓口を決めておくと、それぞれが並行して作業しやすくなります。
