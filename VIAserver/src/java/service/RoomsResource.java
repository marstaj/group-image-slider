package service;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import entities.Room;
import entities.Photo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import oracle.toplink.essentials.ejb.cmp3.EntityManager;
import oracle.toplink.essentials.expressions.Expression;
import oracle.toplink.essentials.expressions.ExpressionBuilder;

/**
 *
 * @author Dominik
 */
@Stateless
@Path("rooms")
public class RoomsResource {
  //@PersistenceContext(unitName = "VIAserverPU")
  //private EntityManager em;
  EntityManagerFactory emf = Persistence.createEntityManagerFactory("VIAserverPU");
  EntityManager em = (EntityManager)emf.createEntityManager();

  public RoomsResource() {
  }

  @GET
  @Path("{device}")
  @Produces({"application/xml", "application/json"})
  public List<rest_entities.Room> findAll(@PathParam("device") String device) {
    //javax.persistence.criteria.CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
    //cq.select(cq.from(Room.class));
    //return rest_entities.Room.list(em.createQuery(cq).getResultList(), device);
    ExpressionBuilder emp = new ExpressionBuilder();
    Expression exp = emp;
    return rest_entities.Room.list(em.createQuery(exp, Room.class).getResultList(), device);
  }

  @POST
  @Path("{device}")
  @Consumes({"application/json"})
  public void create(@PathParam("device") String device, Room entity) {
    em.getTransaction().begin();
    entity.setDevice(device);
    em.persist(entity);
    em.getTransaction().commit();
  }

  @PUT
  @Path("{device}")
  @Consumes({"application/json"})
  public void edit(@PathParam("device") String device, Room entity) {
    Room r = em.find(Room.class, entity.getId());
    if(r.getDevice().equals(device)) {
      em.getTransaction().begin();
      entity.setDevice(device);
      em.merge(entity);
      em.getTransaction().commit();
    }
  }
  
  @DELETE
  @Path("{device}/{id}")
  public void remove(@PathParam("device") String device, @PathParam("id") Integer id) {
    Room r = em.find(Room.class, id);
    if(r.getDevice().equals(device)) {
      em.getTransaction().begin();
      em.remove(em.merge(r));
      em.getTransaction().commit();
    }
  }

  @GET
  @Path("{device}/{id}")
  @Produces({"application/xml", "application/json"})
  public rest_entities.Room find(@PathParam("device") String device, @PathParam("id") Integer id) {
    Room r = em.find(Room.class, id);
    if(r.getPassword() != null) return null;
    return new rest_entities.Room(r, device);
  }

  @GET
  @Path("{device}/{id}/{password}")
  @Produces({"application/xml", "application/json"})
  public rest_entities.Room find(@PathParam("device") String device, @PathParam("id") Integer id, @PathParam("password") String password) {
    Room r = em.find(Room.class, id);
    if(!r.matchPassword(password)) return null;
    return new rest_entities.Room(r, device);
  }

  @GET
  @Path("{device}/{id}/photos")
  @Produces({"application/xml", "application/json"})
  public List<rest_entities.Photo> findAllPhotos(@PathParam("device") String device, @PathParam("id") Integer id) {
    Room r = em.find(Room.class, id);
    if(r == null) return null;
    if(r.getPassword() != null) return null;
    return rest_entities.Photo.list(r.getPhotos());
  }
  
  @GET
  @Path("{device}/{id}/{password}/photos")
  @Produces({"application/xml", "application/json"})
  public List<rest_entities.Photo> findAllPhotos(@PathParam("device") String device, @PathParam("id") Integer id, @PathParam("password") String password) {
    Room r = em.find(Room.class, id);
    if(r == null) return null;
    if(!r.matchPassword(password)) return null;
    return rest_entities.Photo.list(r.getPhotos());
  }
  
  @GET
  @Path("{device}/{id}/photo")
  @Produces({"application/xml", "application/json"})
  public rest_entities.Photo findPhoto(@PathParam("device") String device, @PathParam("id") Integer id) {
    Room r = em.find(Room.class, id);
    if(r == null) return null;
    if(r.getPassword() != null) return null;
    Iterator<Photo> i = r.getPhotos().iterator();
    while(i.hasNext()) {
      Photo p = i.next();
      if(p.isActive()) return new rest_entities.Photo(p);
    }
    return null;
  }

  @GET
  @Path("{device}/{id}/{password}/photo")
  @Produces({"application/xml", "application/json"})
  public rest_entities.Photo findPhoto(@PathParam("device") String device, @PathParam("id") Integer id, @PathParam("password") String password) {
    Room r = em.find(Room.class, id);
    if(r == null) return null;
    if(!r.matchPassword(password)) return null;
    Iterator<Photo> i = r.getPhotos().iterator();
    while(i.hasNext()) {
      Photo p = i.next();
      if(p.isActive()) return new rest_entities.Photo(p);
    }
    return null;
  }
  
  @POST
  @Path("{device}/{id}/photo")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public void createPhoto(@PathParam("device") String device, @PathParam("id") Integer id, 
                                @FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) { 
    Room r = em.find(Room.class, id);
    if(!r.getDevice().equals(device)) return;
    
    String filename = r.getId()+"-"+(new Date().getTime())+"-"+Math.round(Math.random()*10000)+".jpg"; //fileDetail.getFileName();
    String uploadedFileLocation = "/home/via/upload/" + filename;
    try {
	 		OutputStream out = null;
 			int read = 0; 
			byte[] bytes = new byte[1024];

			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    
    em.getTransaction().begin();
    Photo entity = new Photo();
    entity.setActive(r.getPhotos().isEmpty());
    entity.setFilename("/via_upload/"+filename);
    entity.setRoom(r);
    em.persist(entity);
    em.getTransaction().commit();
    //r.getPhotos().add(entity);
    em.refresh(r);
  }
  
  
  @PUT
  @Path("{device}/{id}/photo/{photo_id}")
  public void activatePhoto(@PathParam("device") String device, @PathParam("id") Integer id, @PathParam("photo_id") Integer photo_id) {
    Room r = em.find(Room.class, id);
    if(!r.getDevice().equals(device)) return;
    
    em.getTransaction().begin();
    Iterator<Photo> i = r.getPhotos().iterator();
    while(i.hasNext()) {
      Photo p = i.next();
      if(p.getId().equals(photo_id)) {
        p.setActive(true);
        em.merge(p);
      } else if(p.isActive()) {
        p.setActive(false);
        em.merge(p);
      }
    }
    em.getTransaction().commit();
    em.refresh(r);
  }
}
