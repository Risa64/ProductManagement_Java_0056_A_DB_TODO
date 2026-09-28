package 商品管理システム;

import java.io.IOException;
import java.sql.SQLException;

public class ConnectionTest {
	public static void main(String[] args) throws SQLException, IOException {	
		PostgreSqlProductRepository repository = new PostgreSqlProductRepository();
//		Product product = new Product("A000000002", "0000000000002", "Tシャツ", "衣服", 1400, 1000, null);
//		repository.registerProduct(product);
//		System.out.println(product);
		
//		Product product = repository.findById("A000000002");
//		System.out.println(product.getVersionNo());
//		Product updatedProduct = new Product(
//			    "A000000002",
//			    "0000000000002",
//			    "Yシャツ",
//			    "衣服",
//			    1400,
//			    1000,
//			    null
//			);
//		repository.updateProduct(updatedProduct, 1);
//		
//		System.out.println(updatedProduct);
		
		Product deleteTarget = repository.findById("A000000002");

		System.out.println("削除前：" + deleteTarget);
		System.out.println("削除前versionNo：" + deleteTarget.getVersionNo());

		boolean deleteResult = repository.deleteProduct(
		        deleteTarget.getProductId(),
		        deleteTarget.getVersionNo()
		);

		System.out.println("削除結果：" + deleteResult);

		Product afterDelete = repository.findById("A000000002");
		System.out.println("削除後：" + afterDelete);
	}
}
