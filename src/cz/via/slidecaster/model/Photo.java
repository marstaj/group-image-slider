package cz.via.slidecaster.model;

public class Photo {

	/**
	 * Photo ID
	 */
	private int id;
	/**
	 * Photo link for download
	 */
	private String filename;
	/**
	 * Whether is photo set as active or not
	 */
	private boolean active;

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}