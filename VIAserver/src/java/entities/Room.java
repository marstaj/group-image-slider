/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Dominik
 */
@Entity
@Table(name = "rooms")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Room.findAll", query = "SELECT r.id, r.name FROM Room r"),
  @NamedQuery(name = "Room.findById", query = "SELECT r FROM Room r WHERE r.id = :id")
})
public class Room implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "id", nullable = false, updatable = false)
  private Integer id;
  @Basic(optional = false)
  @Size(min = 1, max = 255)
  @Column(name = "name")
  private String name;
  @Size(max = 255)
  @Column(name = "password")
  private String password;
  @Basic(optional = false)
  @Size(min = 1, max = 255)
  @Column(name = "device")
  private String device;
  
  @OneToMany(mappedBy="room", cascade={CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE})
  @OrderBy("id ASC")
  private List<Photo> photos;

  public Room() {
  }

  public Room(Integer id) {
    this.id = id;
  }

  public Room(Integer id, String name) {
    this.id = id;
    this.name = name;
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

  public String getPassword() {
    return password;
  }

  public List<Photo> getPhotos() {
    return photos;
  }

  public void setPhotos(List<Photo> photos) {
    this.photos = photos;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  
  public boolean matchPassword(String password) {
    return this.password != null && this.password.equals(password);
  }

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;
  }
  
  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Room)) {
      return false;
    }
    Room other = (Room) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "entities.Room[ id=" + id + " ]";
  }
  
}
