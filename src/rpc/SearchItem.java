package rpc;

import java.io.IOException;
import java.io.PrintWriter;
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
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public SearchItem() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	
    	JSONArray array = new JSONArray();
    	DBConnection connection = DBConnectionFactory.getConnection();
    	try {
    		String userId = request.getParameter("user_id");
    		double lat = Double.parseDouble(request.getParameter("lat"));
    		double lon = Double.parseDouble(request.getParameter("lon"));
    		String keyword = request.getParameter("term");
    		List<Item> items = connection.searchItems(lat, lon, keyword);
    		Set<String> favorite = connection.getFavoriteItemIds(userId);
    		for (Item item : items) {
				// Add a thin version of item object
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favorite.contains(item.getItemId()));
				array.put(obj);
			}
    	} catch(Exception e) {
    		e.printStackTrace();
    	} finally {
    		connection.close();
    	}
    	RpcHelper.writeJsonArray(response, array);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

 






