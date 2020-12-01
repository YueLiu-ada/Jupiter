package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		// get some useful infomation from users' url
		// this method is used to get a user's favorite item by his user_id
		// first step should get his id from request:
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		// connect to DB
		DBConnection conn = DBConnectionFactory.getConnection();
		// get items from DB by using method getFavoriteItems:
		Set<Item> items = conn.getFavoriteItems(userId);
		// after we get we should put each item into Json Array
		for(Item item : items) {
			JSONObject obj = item.toJSONObject();
			try {
				// add more into JSON obj this setting is for front-end
				obj.append("favorite", true); 
			} catch(JSONException e) {
				e.printStackTrace();
			}
			array.put(obj);
		}
		// after we have json array, we should return them to client
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	
//	{
//	    user_id = “1111”,
//	    favorite = [
//	        “abcd”,
//	        “efgh”,
//	    ]
//	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// in doPost method, the input type is JSON
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i = 0; i < array.length(); i++) { 
				itemIds.add(array.get(i).toString());
			}
			
			DBConnection conn = DBConnectionFactory.getConnection();
			conn.setFavoriteItems(userId, itemIds);
			conn.close();
			
			RpcHelper.writeJsonObject(response, 
					new JSONObject().put("result", "SUCCESS"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			JSONArray array = input.getJSONArray("favorite");
			List<String> itemIds = new ArrayList<>();
			for(int i = 0; i < array.length(); i++) {
				itemIds.add(array.get(i).toString());
			}
			
			DBConnection conn = DBConnectionFactory.getConnection();
			conn.unsetFavoriteItems(userId, itemIds);
			conn.close();
			
			RpcHelper.writeJsonObject(response, 
					new JSONObject().put("result", "SUCCESS"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}








 








