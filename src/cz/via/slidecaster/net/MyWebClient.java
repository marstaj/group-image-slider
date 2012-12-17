package cz.via.slidecaster.net;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.model.RoomAdd;
import cz.via.slidecaster.model.RoomAddWithPass;
import cz.via.slidecaster.model.RoomEdit;
import cz.via.slidecaster.model.RoomEditWithPass;

public class MyWebClient extends WebClient {

	private static MyWebClient instance;

	final private String URL_BASE = "http://malis.sh.cvut.cz:8080/VIAserver/resources/";

	/**
	 * Method returns instance of MyWebClient.
	 * 
	 * @param context
	 *            Application context
	 * @return Instance of LocationHandler
	 */
	public static MyWebClient getInstance() {
		if (instance == null) {
			instance = new MyWebClient();
		}
		return instance;
	}

	/**
	 * Private constructor
	 */
	private MyWebClient() {
	}

	// Set Json header
	private Header[] getHeaders() {
		CookieStore store = client.getCookieStore();
		client.setCookieStore(store);
		List<Cookie> s = store.getCookies();
		List<BasicHeader> headerList = new ArrayList<BasicHeader>();

		headerList.add(new BasicHeader("Accept", "application/json"));

		String sstr = "";
		for (Cookie var : s) {
			sstr += var.getName() + "=" + var.getValue() + "; ";
		}
		headerList.add(new BasicHeader("Cookie", sstr));

		return headerList.toArray(new Header[headerList.size()]);
	}

	// ------------------------------------------ REQUESTS --------------------------------------

	public List<Room> getRooms(String id) throws ApplicationException {
		String json = sendRequest(URL_BASE + "rooms/id", "UTF-8", getHeaders());
		// Room room = gson.fromJson(t, Room.class); Example how to only get one object out of json
		Gson gson = new Gson();
		Type collectionType = new TypeToken<List<Room>>() {
		}.getType();
		// Convert json to List
		return gson.fromJson(json, collectionType);
	}

	public void createRoom(String id, String name, String pass) throws ApplicationException {
		Gson gson = new Gson();
		if (pass != null && !pass.equals("")) {
			RoomAddWithPass r = new RoomAddWithPass();
			r.setName(name);
			r.setPassword(pass);
			sendRequest(URL_BASE + "rooms/" + id, "UTF-8", gson.toJson(r), getHeaders());
		} else {
			RoomAdd r = new RoomAdd();
			r.setName(name);
			sendRequest(URL_BASE + "rooms/" + id, "UTF-8", gson.toJson(r), getHeaders());
		}

	}

	public void deleteRoom(String id, Room room) throws ApplicationException {
		Gson gson = new Gson();
		String html = sendDeleteRequest(URL_BASE + "rooms/" + id + "/" + room.getId(), "UTF-8", getHeaders());
		if (html != null)
			System.out.print(html);
		else
			System.out.print("null null null null");
	}

	public void editRoom(String id, Room room, String name, String pass) throws ApplicationException {
		Gson gson = new Gson();
		String html = null;
		if (pass != null && !pass.equals("")) {
			RoomEditWithPass r = new RoomEditWithPass();
			r.setId(room.getId());
			r.setName(name);
			r.setPassword(pass);
			html = sendPutRequest(URL_BASE + "rooms/" + id, "UTF-8", gson.toJson(r), getHeaders());
		} else {
			RoomEdit r = new RoomEdit();
			r.setId(room.getId());
			r.setName(name);
			html = sendPutRequest(URL_BASE + "rooms/" + id, "UTF-8", gson.toJson(r), getHeaders());
		}
		if (html != null)
			System.out.print(html);
		else
			System.out.print("null null null null");
	}

	public Room getRoom(String id, Room room, String pass) throws ApplicationException {
		String json;
		if (pass != null) {
			json = sendRequest(URL_BASE + "rooms/" + id + "/" + room.getId() + "/" + pass, "UTF-8", getHeaders());
		} else {
			json = sendRequest(URL_BASE + "rooms/" + id + "/" + room.getId(), "UTF-8", getHeaders());
		}
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Room>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}
}