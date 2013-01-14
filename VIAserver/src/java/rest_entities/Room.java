package rest_entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Room {
  private Integer id;
  private String name;
  private boolean password;
  private boolean yours = false;

  public static List<Room> list(List<entities.Room> list, String device) {
    List<rest_entities.Room> rest_list = new ArrayList<Room>();
    Iterator<entities.Room> i = list.iterator();
    while(i.hasNext()) rest_list.add(new Room(i.next(), device));
    return rest_list;
  }
  
  
  public Room() {
  }

  public Room(entities.Room room, String device) {
    id = room.getId();
    name = room.getName();
    password = room.getPassword() != null;
    yours = room.getDevice().equals(device);
  }
  
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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
