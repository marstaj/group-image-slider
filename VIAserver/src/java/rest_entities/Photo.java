package rest_entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Photo {
  private Integer id;
  private String filename;
  private boolean active;

  public static List<Photo> list(List<entities.Photo> list) {
    List<rest_entities.Photo> rest_list = new ArrayList<Photo>();
    Iterator<entities.Photo> i = list.iterator();
    while(i.hasNext()) rest_list.add(new Photo(i.next()));
    return rest_list;
  }
  
  public Photo() {
  }

  public Photo(entities.Photo photo) {
    id = photo.getId();
    filename = photo.getFilename();
    active = photo.isActive();
  }
  
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }
}
