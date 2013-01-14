package entities;

import entities.Room;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.2.0.v20110202-r8913", date="2013-01-14T11:00:54")
@StaticMetamodel(Photo.class)
public class Photo_ { 

    public static volatile SingularAttribute<Photo, Integer> id;
    public static volatile SingularAttribute<Photo, Boolean> active;
    public static volatile SingularAttribute<Photo, String> filename;
    public static volatile SingularAttribute<Photo, Room> room;

}