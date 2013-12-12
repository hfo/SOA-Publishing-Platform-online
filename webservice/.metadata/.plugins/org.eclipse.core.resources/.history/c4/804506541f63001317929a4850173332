package webservice.resource;

import java.util.ArrayList;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Path;





import webservice.database.DB_Connector;
import webservice.representations.Collection;
import webservice.representations.Post;
//import webservice.database.DB_Connector;
import webservice.representations.User;

@Path("users")
public class UserResource {
	DB_Connector dbcon = DB_Connector.getInstance();
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<User> getUsers(){
		  dbcon.initDBConnection();
		  ArrayList<User> list = dbcon.getUsers();
		  System.out.println("used method getUsers");
		  System.out.println(list);
		  dbcon.closeDBConnection();
		  return list;
	  }
/*	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	//create new user generate ID
	
	@PATH("users/{id}")
	@GET

	
	
	@PUT
	
	@DELETE
	
	@PATH("users/{ID}/posts")
	@GET
	@Produces(MediaType.APPLICATION_JSON)*/
}
