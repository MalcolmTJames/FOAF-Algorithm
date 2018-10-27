package csi403;

// Import required java libraries
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;

// Extend HttpServlet class
public class ReverseList extends HttpServlet {

	// Standard servlet method
	public void init() throws ServletException {
		// Do any required initialization here - likely none
	}

	// Standard servlet method - we will handle a POST operation
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			doService(request, response);
		} catch (Exception e) {
			// Set response content type and return an error message
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.println("{ 'message' : 'Malformed JSON'}");
		}
	}

	// Standard servlet method - we will not respond to GET
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Set response content type and return an error message
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.println("{ 'message' : 'Use POST!'}");
	}

	// Our main worker method
	// Parses messages e.g. {"inList" : [5, 32, 3, 12]}
	// Returns the list reversed.
	private void doService(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get received JSON data from HTTP request
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonStr = "";
		if (br != null) {
			jsonStr = br.readLine();
		}

		// Create JsonReader object
		StringReader strReader = new StringReader(jsonStr);
		JsonReader reader = Json.createReader(strReader);

		// Get the singular JSON object (name:value pair) in this message.
		JsonObject obj = reader.readObject();
		// From the object get the array named "inList"
		JsonArray inArray = obj.getJsonArray("inList");

		// Creates outList array
		JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();

		// declaring ArrayList with Friend Suggestions
		ArrayList<ArrayList<String>> friendSug = new ArrayList<ArrayList<String>>();

		// loops though inList array to get ASCII Values
		for (int i = 0; i < inArray.size(); i++) {

			JsonObject Friends = inArray.getJsonObject(i);
			JsonArray friendVal = Friends.getJsonArray("friends");

			String person = friendVal.getString(0);
			String friend = friendVal.getString(1);

			for (int k = i + 1; k < inArray.size(); k++) {
				JsonObject nxtFriends = inArray.getJsonObject(k);
				JsonArray nxtfriendVal = nxtFriends.getJsonArray("friends");

				String nxtPerson = nxtfriendVal.getString(0);
				String nxtFriend = nxtfriendVal.getString(1);

				if ((friend.equals(nxtPerson))) {
					ArrayList<String> friendSuggest = new ArrayList<String>();
					// JsonArrayBuilder newFriends = Json.createArrayBuilder();
					friendSuggest.add(person);
					friendSuggest.add(nxtFriend);

					friendSug.add(friendSuggest);
				}
			}
		}

		for (int j = 0; j < friendSug.size(); j++) {
			String person = friendSug.get(j).get(0);
			String friend = friendSug.get(j).get(1);

			for (int l = j + 1; l < friendSug.size(); l++) {
				String newPerson = friendSug.get(l).get(0);
				String newFriend = friendSug.get(l).get(1);

				if (((person.equals(newPerson)) && (friend.equals(newFriend)))
						|| ((person.equals(newFriend)) && (friend.equals(newPerson)))) {
					friendSug.remove(l);
				}

			}
		}

		for (int m = 0; m < friendSug.size(); m++) {
			JsonArrayBuilder newFriends = Json.createArrayBuilder();
			newFriends.add(friendSug.get(m).get(0));
			newFriends.add(friendSug.get(m).get(1));

			outArrayBuilder.add(newFriends);
		}

		// Set response content type to be JSON
		response.setContentType("application/json");
		// Send back the response JSON message
		PrintWriter out = response.getWriter();
		out.println("{ \"outList\" : " + outArrayBuilder.build().toString() + "}");
	}

	// Standard Servlet method
	public void destroy() {
		// Do any required tear-down here, likely nothing.
	}
}
