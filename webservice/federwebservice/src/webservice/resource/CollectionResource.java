package webservice.resource;

import java.util.ArrayList;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import webservice.database.DB_Connector;
import webservice.representations.Collection;
import webservice.representations.Post;
import webservice.security.Encryptor;


@Path("collections")
public class CollectionResource {
	DB_Connector dbcon = DB_Connector.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Collection> getCollections(){
		dbcon.initDBConnection();
		  ArrayList<Collection> list = dbcon.getCollections();
		  Encryptor.get_SHA_1_SecurePassword("mypassword");
		  System.out.println("used method getCollections");
		  return list;
		  
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean createCollection(Collection collection){
		dbcon.initDBConnection();
		System.out.println("used method createCollection");
		return dbcon.createCollection(collection);

	}

	
	@GET
	@Path("/filter={name}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Collection> getCollectionByName(@PathParam("name") String name){
		dbcon.initDBConnection();
		ArrayList<Collection> list = dbcon.getCollectionsByName(name);
		System.out.println("used method getCollectionsByName");
		return list;
	  }
		
	
	@GET
	@Path("/{id}/posts")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Post> getPostsByCollection(@PathParam("id") int id){
		dbcon.initDBConnection();
		ArrayList<Post> list = dbcon.getPostsByCollection(id);
		System.out.println("used method getPostsByCollection");
		return list;
	  }
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Collection getCollectionByID(@PathParam("id") int id){
		dbcon.initDBConnection();
		dbcon.incCollViews(id);
		Collection collection = dbcon.getSingleCollection(id);
		System.out.println("used method getSingleCollection");
		return collection;
	}
	
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean changeCollection(@PathParam("id") int id, Collection collection){
		dbcon.initDBConnection();
		System.out.println("used method changeCollection");
		collection.setID(id);
		return dbcon.changeCollection(collection);
	}
	
	@DELETE
	@Path("/{id}")
	public boolean deleteCollection(@PathParam("id") int id){
		dbcon.initDBConnection();
		System.out.println("used method deleteCollection");
		return dbcon.deleteCollection(id);
	}
}

	

