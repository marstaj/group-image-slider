package cz.via.slidecaster.model;

import java.io.Serializable;

public class Room implements Serializable {

    String created_at;
    String id;
    String name;
    String password;
    String photo_id;
    String updated_at;
    private int imageNumber;

    public Room() {
        imageNumber = 0;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPasswordProtected() {
        return !this.getPassword().equals("");
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void nextImage() {
        imageNumber++;
    }

    public void prevImage() {
        imageNumber--;
    }

    public int getImageNumber() {
        return imageNumber;
    }
}
