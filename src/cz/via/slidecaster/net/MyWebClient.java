package cz.via.slidecaster.net;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Image;
import cz.via.slidecaster.model.Room;

public class MyWebClient extends WebClient implements ServerAPI {

	private static MyWebClient instance;

	final private String URL_BASE = "http://if.sh.cvut.cz:9915/";

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

	public List<Room> getRooms() throws ApplicationException {
		String json = sendRequest(URL_BASE + "rooms.json", "UTF-8");
		// Room room = gson.fromJson(t, Room.class); Example how to only get one object out of json

		Gson gson = new Gson();
		Type collectionType = new TypeToken<List<Room>>() {
		}.getType();

		// Convert json to List
		return gson.fromJson(json, collectionType);
	}

	@Override
	public void setPassword(String password) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getActualImage(Integer roomID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getActualImageID(Integer roomID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRoom(Room toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateRoom(Room toEdit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteRoom(Room toDelete) {
		// TODO Auto-generated method stub

	}

	@Override
	public Image getActualImage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addImage(Image toAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateImage(Image toEdit) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteImage(Image toDelete) {
		// TODO Auto-generated method stub

	}

}