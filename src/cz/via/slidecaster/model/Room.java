package cz.via.slidecaster.model;

import java.io.Serializable;

public class Room implements Serializable {

	int id;
	String name;
	boolean password;
	boolean yours;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isPassword() {
		return password;
	}

	public void setPassword(boolean password) {
		this.password = password;
	}

	public boolean isYours() {
		return yours;
	}

	public void setYours(boolean yours) {
		this.yours = yours;
	}

}
