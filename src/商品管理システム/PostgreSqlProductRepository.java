package 商品管理システム;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/*
 * 【このクラスの役割】
 * ProductRepositoryのPostgreSQL版。
 * SELECT / INSERT / UPDATEを実際に実行する、DB化の中心となるクラス。
 *
 * TODO【担当1：DB共通】
 * ・PreparedStatementを使ってSQLを実装する。
 * ・検索はdeleted=0、商品ID昇順。
 * ・登録/変更の直前重複チェック用メソッドを実装する。
 * ・変更/削除はWHERE句にversion_noを入れて楽観ロックを行う。
 * ・削除はDELETE文ではなくdeleted=1へのUPDATEにする。
 * ・SQL実行エラーは共通エラー処理へ渡す。
 */
public class PostgreSqlProductRepository implements ProductRepository {

	@Override
	public List<Product> searchProduct(String keyword) throws SQLException {
		List<Product> products = new ArrayList<Product>();
		
		String sql = """
				SELECT id, code, name, category, sale_price, purchase_price, registration_date
				FROM product
				WHERE deleted = '0'
				AND (id LIKE ?
				     OR code LIKE ?
				     OR name LIKE ?)
				ORDER BY ID ASC
			     """;

		try (Connection connection = DatabaseConnectionManager.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)) {
			
			String searchKeyword = "%" + keyword + "%";
			statement.setString(1, searchKeyword);
			statement.setString(2, searchKeyword);
			statement.setString(3, searchKeyword);
			
			try (ResultSet resultSet = statement.executeQuery()) { 
				while (resultSet.next()) { 
					String id = resultSet.getString("id");  
					String code = resultSet.getString("code");  
					String name = resultSet.getString("name");  
					String category = resultSet.getString("category");  
					Integer salePrice = resultSet.getInt("sale_price");
					if(resultSet.wasNull()) {
						salePrice = null;
					}
					
					Integer purchasePrice = resultSet.getInt("purchase_price"); 
					if(resultSet.wasNull()) {
						purchasePrice = null;
					}
					
					Date sqlDate = resultSet.getDate("registration_date");
					LocalDate registrationDate;
					if(resultSet.wasNull()) {
						registrationDate = null;
					} else {
						registrationDate = sqlDate.toLocalDate();
					}
					
			        Product product = new Product(id, code, name, category, salePrice, purchasePrice, registrationDate);
				    products.add(product);
				}
			} 
		} catch (IOException e) {
			throw new SQLException(e);
		}
		
		return products;
	}

	@Override
	public Product findById(String targetId) throws SQLException {
		String sql = """
				SELECT id, code, name, category, sale_price, purchase_price, registration_date, version_no
				FROM product
				WHERE id = ?
				AND deleted = '0'
				""";
		
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, targetId);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				
				if(resultSet.next()) {
					String id = resultSet.getString("id");
					String code = resultSet.getString("code");
					String name = resultSet.getString("name");
					String category = resultSet.getString("category");
					Integer salePrice = resultSet.getInt("sale_price");
					if(resultSet.wasNull()) {
						salePrice = null;
					}
					
					Integer purchasePrice = resultSet.getInt("purchase_price"); 
					if(resultSet.wasNull()) {
						purchasePrice = null;
					}
					
					Date sqlDate = resultSet.getDate("registration_date");
					LocalDate registrationDate;
					if(resultSet.wasNull()) {
						registrationDate = null;
					} else {
						registrationDate = sqlDate.toLocalDate();
					}
					
					int productVersionNo = resultSet.getInt("version_no");
					Product product = new Product(id, code, name, category,
							salePrice, purchasePrice, registrationDate);
					product.setVersionNo(productVersionNo);
					
					return product;
					
				}
			}
		} catch (IOException e) {
			throw new SQLException(e);
		}
		
		return null;
	}

	@Override
	public boolean existsById(String id) throws SQLException {
		String sql = """
				SELECT COUNT(*)
				FROM product
				WHERE id = ?
				AND deleted = '0'
				""";
		
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, id);
			
			try (ResultSet resultSet = statement.executeQuery()) {
				
				if(resultSet.next()) {
					int productCount = resultSet.getInt(1);
					return productCount > 0;
				}
			}
			
		} catch (IOException e) {
			throw new SQLException(e);
		}
		
		return false;
	}

	@Override
	public boolean existsByCode(String code, String excludeId) throws SQLException {
		String sql = """
				SELECT COUNT(*)
				FROM product
				WHERE code = ?
				AND deleted = '0'
				""";
		
		if (excludeId != null) {
		    sql += " AND id <> ?";
		}
		
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, code);
			if (excludeId != null) {
				statement.setString(2, excludeId);
			}
			
			try (ResultSet resultSet = statement.executeQuery()) {
				
				if(resultSet.next()) {
					int productCount = resultSet.getInt(1);
					return productCount > 0;
				}
			}
			
		} catch (IOException e) {
			throw new SQLException(e);
		}
		
		return false;
	}

	@Override
	public void registerProduct(Product product) throws SQLException {
		String sql = """
				INSERT INTO product(
				id, code, name, category, sale_price, purchase_price, registration_date,
				deleted, version_no, record_creation_timestamp, record_update_timestamp)
				VALUES(?, ?, ?, ?, ?, ?, ?,
				'0', 1, current_timestamp, current_timestamp)
				""";
		
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, product.getProductId());
			statement.setString(2, product.getProductCode());
			statement.setString(3, product.getProductName());
			statement.setString(4, product.getCategory());
			
			if(product.getSellingPrice() == null) {
				statement.setNull(5, Types.INTEGER);
			} else {
				statement.setInt(5, product.getSellingPrice());
			}
			
			if(product.getPurchasePrice() == null) {
				statement.setNull(6, Types.INTEGER);
			} else {
				statement.setInt(6, product.getPurchasePrice());
			}
			
			if(product.getRegistrationDate() == null) {
				statement.setNull(7, Types.DATE);
			} else {
				statement.setDate(7, Date.valueOf(product.getRegistrationDate()));
			}
			
			statement.executeUpdate();
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean updateProduct(Product product, int versionNo) throws SQLException {
		String sql = """
				UPDATE product
				SET code = ?, name = ?, category = ?, sale_price = ?, purchase_price = ?, registration_date = ?,
				version_no = version_no + 1, record_update_timestamp = current_timestamp
				WHERE id = ?
				AND deleted = '0'
				AND version_no = ?
				""";
		
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, product.getProductCode());
			statement.setString(2, product.getProductName());
			statement.setString(3, product.getCategory());
			
			if(product.getSellingPrice() == null) {
				statement.setNull(4, Types.INTEGER);
			} else {
				statement.setInt(4, product.getSellingPrice());
			}
			
			if(product.getPurchasePrice() == null) {
				statement.setNull(5, Types.INTEGER);
			} else {
				statement.setInt(5, product.getPurchasePrice());
			}
			
			if(product.getRegistrationDate() == null) {
				statement.setNull(6, Types.DATE);
			} else {
				statement.setDate(6, Date.valueOf(product.getRegistrationDate()));
			}
			
			statement.setString(7, product.getProductId());
			statement.setInt(8, versionNo);
			
			int updateCount = statement.executeUpdate();
			
			return updateCount == 1;
			
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}

	@Override
	public boolean deleteProduct(String targetId, int versionNo) throws SQLException {
		String sql = """
				UPDATE product
				SET deleted = '1', version_no = version_no + 1, record_update_timestamp = current_timestamp
				WHERE id = ?
				AND deleted = '0'
				AND version_no = ?
				""";
		
		try (Connection connection = DatabaseConnectionManager.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			
			statement.setString(1, targetId);
			statement.setInt(2, versionNo);
			
			int deleteCount = statement.executeUpdate();
			
			return deleteCount == 1;
			
		} catch (IOException e) {
			throw new SQLException(e);
		}
	}
}
