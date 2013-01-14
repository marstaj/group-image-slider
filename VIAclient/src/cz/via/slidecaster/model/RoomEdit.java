package cz.via.slidecaster.model;

import java.io.Serializable;

public class RoomEdit implements Serializable {

	int id;
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
