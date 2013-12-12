package webservice.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;








import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.resource.Singleton;

import webservice.database.DB_Connector;
//import webservice.database.DB_Connector;
import webservice.representations.Collection;
import webservice.representations.Post;


@Path("collections")
public class CollectionResource {
	DB_Connector dbcon = DB_Connector.getInstance();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Collection> getCollections(){
		  dbcon.initDBConnection();
		  ArrayList<Collection> list = dbcon.getCollections();
		  System.out.println("used method getcollections");
		  dbcon.closeDBConnection();
		  return list;
		  
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void createCollection(Collection collection){
		dbcon.initDBConnection();
		System.out.println(collection.toString());
		dbcon.createCollection(collection);
		dbcon.closeDBConnection();
	}
	
	@GET
	@Path("?filter={name}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Collection> getCollectionByName(@PathParam("name") String name){
		if(name.length()==0){System.out.println("noName handed over");}
		else{System.out.println(name);}
		
		dbcon.initDBConnection();
		ArrayList<Collection> list = dbcon.getCollectionsByName(name);
		System.out.println("used method getcollectionsbyname");
		dbcon.closeDBConnection();
		return list;
	  }
	

/*	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	//Delete Collection
*/	

	
	
	@GET
	@Path("collections/{ID}/posts")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Post> getPostsByCollection(@PathParam("id") int id){
		  dbcon.initDBConnection();
		  ArrayList<Post> list = dbcon.getPostsByCollection(id);
		return list;
	  }
}

	

