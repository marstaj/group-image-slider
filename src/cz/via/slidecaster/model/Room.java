package cz.via.slidecaster.model;

import java.io.Serializable;

public class Room implements Serializable {

	int id;
	String name;
	boolean has_passwd;

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

	public boolean isHas_passwd() {
		return has_passwd;
	}

	public void setHas_passwd(boolean has_passwd) {
		this.has_passwd = has_passwd;
	}

	public boolean isPasswordProtected() {
		return has_passwd;
	}

}
