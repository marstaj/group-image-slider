package cz.via.slidecaster.net;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.message.BasicHeader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cz.via.slidecaster.MyApp;
import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Photo;
import cz.via.slidecaster.model.Room;
import cz.via.slidecaster.model.RoomAdd;
import cz.via.slidecaster.model.RoomAddWithPass;
import cz.via.slidecaster.model.RoomEdit;
import cz.via.slidecaster.model.RoomEditWithPass;

public class MyWebClient extends WebClient {

	private int port = 8080;
	private String address = "http://malis.sh.cvut.cz";

	private static MyWebClient instance;

	final private String URL_BASE = address + ":" + port + "/VIAserver/resources/";

	/**
	 * Method returns instance of MyWebClient.
	 * 
	 * @param context
	 *            Application context
	 * @return Instance of MyWebClient
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

	/**
	 * Download list of rooms.
	 * 
	 * @return List of rooms or NULL if a problem with request or response occurred.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public List<Room> getRooms() throws ApplicationException {
		String json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId(), "UTF-8", getHeaders());
		if (json == null) {
			return null;
		}
		// Room room = gson.fromJson(t, Room.class); Example how to only get one object out of json
		Gson gson = new Gson();
		Type collectionType = new TypeToken<List<Room>>() {
		}.getType();
		// Convert json to List
		return gson.fromJson(json, collectionType);
	}

	/**
	 * Create new room
	 * 
	 * @param name
	 *            Name for the room
	 * @param pass
	 *            Password for the room (use null for room without password). Empty string means no password.
	 * @return TRUE string if everything went OK or FALSE if a problem with request or response occurred.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public boolean createRoom(String name, String pass) throws ApplicationException {
		Gson gson = new Gson();
		String response = null;
		if (pass != null && !pass.equals("")) {
			RoomAddWithPass r = new RoomAddWithPass();
			r.setName(name);
			r.setPassword(MyApp.getsaltedHash(pass));
			response = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId(), "UTF-8", gson.toJson(r), getHeaders());
		} else {
			RoomAdd r = new RoomAdd();
			r.setName(name);
			response = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId(), "UTF-8", gson.toJson(r), getHeaders());
		}
		return response != null;
	}

	/**
	 * Delete room.
	 * 
	 * @param room
	 *            Room which we want to delete
	 * @return TRUE if everything went OK or FALSE if a problem with request or response occurred.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public boolean deleteRoom(Room room) throws ApplicationException {
		String html = sendDeleteRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId(), "UTF-8", getHeaders());
		return html != null;
	}

	/**
	 * Edit room.
	 * 
	 * @param room
	 *            Room which we want to edit
	 * @param name
	 *            New name
	 * @param pass
	 *            New password (use null for room without password). Empty string means no password
	 * @return TRUE if everything went OK or FALSE if a problem with request or response occurred.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public boolean editRoom(Room room, String name, String pass) throws ApplicationException {
		Gson gson = new Gson();
		String response = null;
		if (pass != null && !pass.trim().equals("")) {
			RoomEditWithPass r = new RoomEditWithPass();
			r.setId(room.getId());
			r.setName(name);
			r.setPassword(MyApp.getsaltedHash(pass));
			response = sendPutRequest(URL_BASE + "rooms/" + MyApp.getDeviceId(), "UTF-8", gson.toJson(r), getHeaders());
		} else {
			RoomEdit r = new RoomEdit();
			r.setId(room.getId());
			r.setName(name);
			response = sendPutRequest(URL_BASE + "rooms/" + MyApp.getDeviceId(), "UTF-8", gson.toJson(r), getHeaders());
		}
		return response != null;
	}

	/**
	 * Get room for password confirmation
	 * 
	 * @param room
	 *            Room which we want to confirm.
	 * @param pass
	 *            Password for the room (use null for room without password)
	 * @return Room if everything went OK or NULL if a problem with request or response occurred or password is wrong.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public Room getRoom(Room room, String pass) throws ApplicationException {
		String json;
		if (pass != null) {
			json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/" + MyApp.getsaltedHash(pass), "UTF-8", getHeaders());
		} else {
			json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId(), "UTF-8", getHeaders());
		}
		if (json == null || json.isEmpty()) {
			return null;
		}

		Gson gson = new Gson();
		Type collectionType = new TypeToken<Room>() {
		}.getType();
		return gson.fromJson(json, collectionType);
	}

	/**
	 * Get actual active photo of a room.
	 * 
	 * @param room
	 *            Room from which we wand to get active photo.
	 * @param pass
	 *            Password for the room. (use null for room without password)
	 * @return Room if everything went OK or NULL if a problem with request or response occurred or password is wrong or there is no active photo.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public Photo getActivePhotoInRoom(Room room, String pass) throws ApplicationException {
		String json;
		if (pass != null) {
			json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/" + MyApp.getsaltedHash(pass) + "/photo", "UTF-8", getHeaders());
		} else {
			json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/photo", "UTF-8", getHeaders());
		}

		if (json == null || json.isEmpty()) {
			return null;
		}

		Gson gson = new Gson();
		Type collectionType = new TypeToken<Photo>() {
		}.getType();

		Photo p = gson.fromJson(json, collectionType);
		p.setFilename(address + ":80" + p.getFilename());
		return p;
	}

	/**
	 * Get list of photos in a room.
	 * 
	 * @param room
	 *            Room from which we want list of photos.
	 * @param pass
	 *            Password for the room (use null for room without password)
	 * @return List of photos if everything went OK or NULL if a problem with request or response occurred or password is wrong.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public List<Photo> getPhotosInRoom(Room room, String pass) throws ApplicationException {
		String json;
		if (pass != null) {
			json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/" + MyApp.getsaltedHash(pass) + "/photos", "UTF-8", getHeaders());
		} else {
			json = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/photos", "UTF-8", getHeaders());
		}
		Gson gson = new Gson();

		if (json == null) {
			return null;
		}

		List<Photo> list;
		try {
			Type collectionType = new TypeToken<List<Photo>>() {
			}.getType();
			list = gson.fromJson(json, collectionType);
		} catch (Exception e) {
			Type collectionType = new TypeToken<Photo>() {
			}.getType();
			Photo p = gson.fromJson(json, collectionType);
			list = new ArrayList<Photo>();
			list.add(p);
		}
		return list;
	}

	// public void postPhoto(String id, Room room, Photo photo) throws ApplicationException {
	// Gson gson = new Gson();
	// PhotoAdd p = new PhotoAdd();
	// p.setFilename(photo.getFilename());
	// sendRequest(URL_BASE + "rooms/" + id + "/" + room.getId() + "/photo", "UTF-8", gson.toJson(p), getHeaders());
	// }

	/**
	 * Upload new picture.
	 * 
	 * @param room
	 *            Room in which we want to upload new picture to.
	 * @param file
	 *            File with photo to upload.
	 * @return TRUE if everything went OK or FALSE if a problem with request or response occurred.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public boolean postPhoto(Room room, File file) throws ApplicationException {
		String response = sendRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/photo", "UTF-8", file, getHeaders());
		return response != null;
	}

	/**
	 * Set photo as active
	 * 
	 * @param room
	 *            Room in which we want to set active photo.
	 * @param photo
	 *            Photo that we want to set as active.
	 * @return TRUE if everything went OK or FALSE if a problem with request or response occurred.
	 * @throws ApplicationException
	 *             Exception is thrown when error with communication occurred.
	 */
	public boolean setPhotoAsActive(Room room, Photo photo) throws ApplicationException {
		String response = sendPutRequest(URL_BASE + "rooms/" + MyApp.getDeviceId() + "/" + room.getId() + "/photo/" + photo.getId(), "UTF-8", getHeaders());
		return response != null;
	}
}