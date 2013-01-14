package entities;

import entities.Photo;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110202-r8913", date="2013-01-14T11:00:54")
@StaticMetamodel(Room.class)
public class Room_ { 

    public static volatile SingularAttribute<Room, Integer> id;
    public static volatile ListAttribute<Room, Photo> photos;
    public static volatile SingularAttribute<Room, String> name;
    public static volatile SingularAttribute<Room, String> device;
    public static volatile SingularAttribute<Room, String> password;

}