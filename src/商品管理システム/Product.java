/*
 * 【このクラスの役割】
 * 商品1件分の情報をまとめて持つクラス。検索結果や登録・変更する商品を、1つのProductとして扱えるようにする。
 *
 * TODO【担当3：検索・共通】
 * ・DBから取得するversion_noを変更・削除時に使えるよう、Productに持たせる方法を決める。
 * ・必要に応じてsid、deleted、作成日時、更新日時などのDB管理項目を追加する。
 * ・登録日は仕様書のyyyy-MM-ddを扱いやすい型・形式にそろえる。
 */
package 商品管理システム;

import java.time.LocalDate;

public class Product {

	// 商品ID～登録日までを1つにまとめることで、7個の値を別々に渡さずProductとして扱える。
	
	private String productId;

	private String productCode;

	private String productName;

	private String category;

	private Integer sellingPrice;

	private Integer purchasePrice;

	private LocalDate registrationDate;
	
	private int versionNo;

	Product(String productId, String productCode, String productName, String category, Integer sellingPrice,
			Integer purchasePrice, LocalDate registrationDate) {
		this.productId = productId;
		this.productCode = productCode;
		this.productName = productName;
		this.category = category;
		this.sellingPrice = sellingPrice;
		this.purchasePrice = purchasePrice;
		this.registrationDate = registrationDate;
	}

	
	public String getProductId() {
		return productId;
	}

	
	public void setProductId(String productId) {
		this.productId = productId;
	}

	
	public String getProductCode() {
		return productCode;
	}

	
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	
	public String getProductName() {
		return productName;
	}

	
	public void setProductName(String productName) {
		this.productName = productName;
	}

	
	public String getCategory() {
		return category;
	}

	
	public void setCategory(String category) {
		this.category = category;
	}

	
	public Integer getSellingPrice() {
		return sellingPrice;
	}

	
	public void setSellingPrice(Integer sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	
	public Integer getPurchasePrice() {
		return purchasePrice;
	}

	
	public void setPurchasePrice(Integer purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	
	public LocalDate getRegistrationDate() {
		return registrationDate;
	}


	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}
	
	/*
	 * @author マエドンチ
	 */
	public int getVersionNo() {
		return versionNo;
	}
	
	/*
	 * @author マエドンチ
	 */
	public void setVersionNo(int versionNo) {
		this.versionNo = versionNo;
	}

	
	// Productをprintlnしたとき、検索結果と同じ並びで1行表示するための文字列を作る。
	@Override
	public String toString() {
		return productId + ","
				+ productCode + ","
				+ productName + ","
				+ category + ","
				+ sellingPrice + ","
				+ purchasePrice + ","
				+ registrationDate;
	}

}
