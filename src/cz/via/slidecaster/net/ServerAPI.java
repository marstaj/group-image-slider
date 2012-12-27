package cz.via.slidecaster.net;

import java.util.List;

import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Photo;
import cz.via.slidecaster.model.Room;

public interface ServerAPI {
	public List<Room> getRooms() throws ApplicationException;

	public void setPassword(String password);

	public Photo getActualImage(Integer roomID);

	public Integer getActualImageID(Integer roomID);

	public void addRoom(Room toAdd);

	public void updateRoom(Room toEdit);

	public void deleteRoom(Room toDelete);

	public Photo getActualImage();

	public void addImage(Photo toAdd);

	public void updateImage(Photo toEdit);

	public void deleteImage(Photo toDelete);

}