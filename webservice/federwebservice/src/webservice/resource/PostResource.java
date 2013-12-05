package webservice.resource;

import java.util.ArrayList;
import java.util.Iterator;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import webservice.database.DB_Connector;
import webservice.representations.Post;

@Path("/posts")
public class PostResource {
	DB_Connector dbcon = DB_Connector.getInstance();
	
	  @GET
	  @Produces(MediaType.APPLICATION_JSON)
	  public String getPosts(){
		  String posts = new String();
		  posts="{";
		  dbcon.initDBConnection();
		  ArrayList<Post> list = dbcon.getPosts();
		  int size = list.size()-1;
		  if(size<=0){
			    posts=posts+"}";	
			}
		  for ( Iterator<Post> i = list.iterator(); i.hasNext(); )
		  {
			if(size==0){
			    posts=posts+i.next().toString()+"}";	
			}
			else{
				posts=posts+i.next().toString()+",";
			}
		    size=size-1;
		  }
		  return posts;
	  }
	  @POST
	  @Consumes(MediaType.APPLICATION_JSON)
	  public boolean createPost(Post post){
		  return dbcon.createPost(post);
	  }

//	  @GET
//	  @Produces(MediaType.TEXT_HTML)
//	  public String sayHtmlHello() {
//	    return "<html> " + "<title>" + "Hello Jersey" + "</title>"
//	        + "<body><h1>" + "Hello Jersey1" + "</body></h1>" + "</html> ";
//	  }
/*	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	//create new post generate ID

	@PATH("posts/{ID}")
	@GET
	@Produces(MediaType.Application_JSON)
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)*/
}
