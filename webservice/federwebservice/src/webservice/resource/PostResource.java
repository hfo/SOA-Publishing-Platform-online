package webservice.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import webservice.database.DB_Connector;
import webservice.representations.Post;

@Path("posts")
public class PostResource {
	DB_Connector dbcon = DB_Connector.getInstance();
	
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public ArrayList<Post> getPosts(){
		  dbcon.initDBConnection();
		  ArrayList<Post> list = dbcon.getPosts();
		  System.out.println(list);
		  dbcon.closeDBConnection();
		  return list;

	 }
	  
	 @POST
	 @Consumes(MediaType.APPLICATION_JSON)
	 public boolean createPost(Post post){
		 dbcon.initDBConnection();
		 Calendar cal = Calendar.getInstance();
		 Date now = cal.getTime();
		 post.setCreationDate(now.toString());
		 Boolean result = dbcon.createPost(post);
		 dbcon.closeDBConnection();
		 return result;
	 }

	@GET
	@Path("posts/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Post getPostById(@PathParam("id") int id){
		dbcon.initDBConnection();
		Post post = dbcon.getSinglePost(id);
		return post;
		
	}
/*	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)*/
}
