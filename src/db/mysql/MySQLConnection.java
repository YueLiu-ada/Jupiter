package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;

public class MySQLConnection implements DBConnection {

	private Connection conn;
	// try to open
	public MySQLConnection() {
		// create a connection
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		if(conn != null) {
			// if there is connection
			try {
				conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		// operate favorite table
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "INSERT IGNORE INTO history (user_id, item_id) VALUES (?, ?)";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, userId);
				stmt.setString(2, itemId);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		if (conn == null) {
			return;
		}
		
		try {
			String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, userId);
				stmt.setString(2, itemId);
				stmt.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		//  get from history table;
		if(conn == null) {
			return new HashSet<>();
		}
		Set<String> favoriteItemIds = new HashSet<>();
		try {
			// we dont have to focuse on all the items, we should only care about userId
			// use userId to find itemId
			String sql = "SELECT item_id from history where user_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				String itemId = rs.getString("item_id");
				favoriteItemIds.add(itemId);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return favoriteItemIds;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		//return null;
		if(conn == null) {
			return new HashSet<>();
		}
		Set<Item> favoriteItems = new HashSet<>();
		Set<String> itemIds = getFavoriteItemIds(userId);
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(String itemId : itemIds) {
				stmt.setString(1, itemId);
				ResultSet rs = stmt.executeQuery();
				ItemBuilder builder = new ItemBuilder();
				while (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
					
					favoriteItems.add(builder.build());
				}

			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return favoriteItems;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		if (conn == null) {
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				categories.add(rs.getString("category"));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return categories;
	}


	@Override
	public List<Item> searchItems(double lat, double lon, String term) {
		// TODO Auto-generated method stub
		// to find all the thing that we searched on Internet via api.
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> items = tmAPI.search(lat, lon, term);
		for (Item item : items) {
			// important part to process
			saveItem(item);
		}
		return items;
	}

	@Override
	public void saveItem(Item item) {
		// TODO Auto-generated method stub
		if(conn == null) {// if no connection
			return;
		}
		// get item from the result that we searched, and write it into DB
		try {
			// SQL injection
			// more faster
			// sql template: ???????
			String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)"; // when primary key is already in, do not insert
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, item.getItemId());// position, value
			stmt.setString(2, item.getName());
			stmt.setDouble(3, item.getRating());
			stmt.setString(4, item.getAddress());
			stmt.setString(5, item.getImageUrl());
			stmt.setString(6, item.getUrl());
			stmt.setDouble(7, item.getDistance());
			stmt.execute();
			
			// insert into another table
			sql = "INSERT IGNORE INTO categories VALUES (?,?)";// new sql
			stmt = conn.prepareStatement(sql);// new stmt
			for(String category : item.getCategories()) {
				stmt.setString(1, item.getItemId());
				stmt.setString(2, category);
				stmt.execute();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getFullname(String userId) {
//		if (conn == null) {
//			return null;
//		}
//		String name = "";
//		try {
//			String sql = "SELECT first_name, last_name from users WHERE user_id = ?";
//			PreparedStatement statement = conn.prepareStatement(sql);
//			statement.setString(1, userId);
//			ResultSet rs = statement.executeQuery();
//			if (rs.next()) {
//				name = String.join(" ", rs.getString("first_name"), rs.getString("last_name"));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return name;
		return null;
	}


	@Override
	public boolean verifyLogin(String userId, String password) {
//		if (conn == null) {
//			return false;
//		}
//		try {
//			String sql = "SELECT user_id from users WHERE user_id = ? and password = ?";
//			PreparedStatement statement = conn.prepareStatement(sql);
//			statement.setString(1, userId);
//			statement.setString(2, password);
//			ResultSet rs = statement.executeQuery();
//			if (rs.next()) {
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return false;
		return true;
	}

 
}
