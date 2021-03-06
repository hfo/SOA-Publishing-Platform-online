package webservice.resource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import webservice.representations.Comment;
import webservice.representations.Post;

@Path("posts")
public class PostResource {
	DB_Connector dbcon = DB_Connector.getInstance();
	
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public ArrayList<Post> getPosts(){
		  dbcon.initDBConnection();
		  ArrayList<Post> list = dbcon.getPosts();
		  System.out.println("used method getPosts");
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
		 dbcon.incCollPosts(post.getCollectionID());
		 System.out.println("used method createPost");
		 return result;
	 }

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Post getPostById(@PathParam("id") int id){
		dbcon.initDBConnection();
		dbcon.incPostViews(id);
		Post post = dbcon.getSinglePost(id);
		System.out.println("used method getSinglePost");
		return post;	
	}
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean changePost(@PathParam("id") int id, Post post){
		dbcon.initDBConnection();
		post.setID(id);
		System.out.println("used method changePost");
		return dbcon.changePost(post);
	}
	@DELETE
	@Path("/{id}")
	public boolean deletePost(@PathParam("id") int id){
		dbcon.initDBConnection();
		System.out.println("used method deletePost");
		return dbcon.deletePost(id);
	}
	@GET
	@Path("/{id}/comments")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<Comment> getCommentsByPost(@PathParam("id") int id){
		dbcon.initDBConnection();
		ArrayList<Comment> list = dbcon.getCommentsByPost(id);
		System.out.println("used method getCommentByPost");
		return list;
	}
	@POST
	@Path("/{id}/comments")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean createComment(@PathParam("id") int id, Comment comment){
		dbcon.initDBConnection();
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		comment.setCreationDate(now.toString());
		comment.setPostID(id);
		Boolean result = dbcon.createComment(comment);
		System.out.println("used method createComment");
		return result;
	}
/*	
	

	*/
}
