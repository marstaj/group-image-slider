package cz.via.slidecaster.net;

import java.util.List;

import cz.via.slidecaster.exception.ApplicationException;
import cz.via.slidecaster.model.Image;
import cz.via.slidecaster.model.Room;

public interface ServerAPI {
	public List<Room> getRooms() throws ApplicationException;

	public void setPassword(String password);

	public Image getActualImage(Integer roomID);

	public Integer getActualImageID(Integer roomID);

	public void addRoom(Room toAdd);

	public void updateRoom(Room toEdit);

	public void deleteRoom(Room toDelete);

	public Image getActualImage();

	public void addImage(Image toAdd);

	public void updateImage(Image toEdit);

	public void deleteImage(Image toDelete);

}