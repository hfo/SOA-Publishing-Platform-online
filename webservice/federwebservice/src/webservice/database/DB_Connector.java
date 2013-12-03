package webservice.database;

import java.sql.Connection; 
import java.sql.Date; 
import java.sql.DriverManager; 
import java.sql.PreparedStatement; 
import java.sql.ResultSet; 
import java.sql.SQLException; 
import java.sql.Statement; 
import java.util.ArrayList;

import webservice.representations.Collection;
import webservice.representations.Comment;
import webservice.representations.Post;
import webservice.representations.User;

public class DB_Connector { 
     
    private static final DB_Connector dbcon = new DB_Connector(); 
    private static Connection connection; 
    private static final String DB_PATH = "SOAPP.db"; 

    static { 
        try { 
            Class.forName("org.sqlite.JDBC"); 
        } catch (ClassNotFoundException e) { 
            System.err.println("Fehler beim Laden des JDBC-Treibers"); 
            e.printStackTrace(); 
        } 
    } 
     
    private DB_Connector(){ 
    	
    	createTableUser();
    	createTablePost();
    	createTableComment();
    	createTableCollection();
    	initDBConnection();
    } 
     
    public static DB_Connector getInstance(){ 
        return dbcon; 
    } 
     
    private void initDBConnection() { 
        try { 
            if (connection != null) 
                return; 
            System.out.println("Creating Connection to Database..."); 
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); 
            if (!connection.isClosed()) 
                System.out.println("...Connection established"); 
        } catch (SQLException e) { 
            throw new RuntimeException(e); 
        } 

        Runtime.getRuntime().addShutdownHook(new Thread() { 
            public void run() { 
                try { 
                    if (!connection.isClosed() && connection != null) { 
                        connection.close(); 
                        if (connection.isClosed()) 
                            System.out.println("Connection to Database closed"); 
                    } 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                } 
            } 
        }); 
    } 
    
    public void createTableUser(){
    	try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS User("
					+ "id INT PRIMARY KEY AUTOINCREMENT"
					+ "username CHAR(25)"
					+ "password CHAR(25)"
					+ "email CHAR;");
		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 
		}
    }
    public void createTablePost(){
    	try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Post("
					+ "id INT PRIMARY KEY AUTOINCREMENT"
					+ "title CHAR"
					+ "authorID INT"
					+ "subtitle CHAR"
					+ "body TEXT"
					+ "image CHAR"
					+ "creationDate NONE"
					+ "collectionID INT"
					+ "isDraft INT"
					+ "views INT;");
			stmt.execute("INSERT INTO Post VALUES ('my',1,'your','our','http',21212,2,false,2 )");
			System.out.println("I was here");
		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 
		}
    	
    }
    public void createTableComment(){
    	try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Comment("
					+ "id INT PRIMARY KEY AUTOINCREMENT"
					+ "authorID INT"
					+ "postID INT"
					+ "body TEXT"
					+ "creationDate NONE;");
		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 
		}
    	
    }
    public void createTableCollection(){
    	try {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS Comment("
					+ "id INT PRIMARY KEY AUTOINCREMENT"
					+ "title CHAR"
					+ "image CHAR"
					+ "views INT"
					+ "posts INT;");
		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 
		}
    	
    }
    public ArrayList<User> getUsers(){
    	ArrayList<User> UsersInDB = new ArrayList<User>();
    	try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USER;" );
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String  uname = rs.getString("username");
				String email  = rs.getString("email");
				User user = new User(id,uname,email);
				UsersInDB.add(user);
				
			}
			rs.close();
			stmt.close();
			return UsersInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public ArrayList<User> getUsersByName(String name){
    	
    	ArrayList<User> UsersInDB = new ArrayList<User>();
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM USER WHERE username=?;");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String  uname = rs.getString("username");
				String email  = rs.getString("email");
				User user = new User(id,uname,email);
				UsersInDB.add(user);
				
			}
			rs.close();
			stmt.close();
			
			return UsersInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public User getSingleUser(int ID){
    	
    	int counter =0; 
    	User user=null;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM USER WHERE id=?;");
			stmt.setInt(1, ID);
			ResultSet rs = stmt.executeQuery();
			     
			while ( rs.next() ) {
				
			
				int id = rs.getInt("id");
				String  uname = rs.getString("username");
				String email  = rs.getString("email");
				user = new User(id,uname,email);
				counter=counter+1;
			}	
			        
			stmt.close();
			rs.close();
			
			if(counter == 1){
				return user;
			}
			else{
				if(counter<1){
					System.err.println("No User was found using this ID!");
					return null;
				}
				else{
					System.err.println("Multiple Users found by ID! DB failure!");
					return null;
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
        
    	
    }
    public boolean createUser(User user){
    	try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO USER "
					+ "VALUES (?,?,?)");
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getEmail());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    	
    }
    public boolean changeUser(User user){
    	
    	try {
			PreparedStatement stmt = connection.prepareStatement(
					"UPDATE USER SET username = ? WHERE id = ?;"
					+ "UPDATE USER SET email=? WHERE id=?");
			stmt.setString(1, user.getUsername());
			stmt.setInt(2, user.getID());
			stmt.setString(3, user.getEmail());
			stmt.setInt(4, user.getID());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
    public void changeUserPassword(){
    	//TODO
    }
    
    	
    public ArrayList<Post> getPosts(){
    	ArrayList<Post> PostsInDB = new ArrayList<Post>();
    	try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM POST;" );
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String title  = rs.getString("title");
				int authID = rs.getInt("authorID");
				String subtitle = rs.getString("subtitle");
				String body = rs.getString("body");
				String image = rs.getString("image");
				Date creationDate = rs.getDate("creationDate");
				int collectionID = rs.getInt("collectionID");
				boolean isDraft = rs.getBoolean("isDraft");
				int views = rs.getInt("views");
				Post post = new Post(id, title, authID, subtitle, body, image, creationDate, collectionID, isDraft, views);
				PostsInDB.add(post);
				
			}

			rs.close();
			stmt.close();
			return PostsInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public ArrayList<Post> getPostsByCollection(int collID){
    	ArrayList<Post> PostsInDB = new ArrayList<Post>();
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM POST WHERE collectionID=?;");
			stmt.setInt(1, collID);
			ResultSet rs = stmt.executeQuery();
			
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String title  = rs.getString("title");
				int authID = rs.getInt("authorID");
				String subtitle = rs.getString("subtitle");
				String body = rs.getString("body");
				String image = rs.getString("image");
				Date creationDate = rs.getDate("creationDate");
				int collectionID = rs.getInt("collectionID");
				boolean isDraft = rs.getBoolean("isDraft");
				int views = rs.getInt("views");
				Post post = new Post(id, title, authID, subtitle, body, image, creationDate, collectionID, isDraft, views);
				PostsInDB.add(post);
				
			}
			rs.close();
			stmt.close();
			
			return PostsInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public ArrayList<Post> getPostsByUser(int userID){
    	ArrayList<Post> PostsInDB = new ArrayList<Post>();
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM POST WHERE authorID=?;");
			stmt.setInt(1, userID);
			ResultSet rs = stmt.executeQuery();
			
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String title  = rs.getString("title");
				int authID = rs.getInt("authorID");
				String subtitle = rs.getString("subtitle");
				String body = rs.getString("body");
				String image = rs.getString("image");
				Date creationDate = rs.getDate("creationDate");
				int collectionID = rs.getInt("collectionID");
				boolean isDraft = rs.getBoolean("isDraft");
				int views = rs.getInt("views");
				Post post = new Post(id, title, authID, subtitle, body, image, creationDate, collectionID, isDraft, views);
				PostsInDB.add(post);
				
			}
			rs.close();
			stmt.close();
			
			return PostsInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public Post getSinglePost(int ID){
    	Post post = null;
    	int counter=0;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM POST WHERE id=?;");
			stmt.setInt(1, ID);
			ResultSet rs = stmt.executeQuery();
			
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String title  = rs.getString("title");
				int authID = rs.getInt("authorID");
				String subtitle = rs.getString("subtitle");
				String body = rs.getString("body");
				String image = rs.getString("image");
				Date creationDate = rs.getDate("creationDate");
				int collectionID = rs.getInt("collectionID");
				boolean isDraft = rs.getBoolean("isDraft");
				int views = rs.getInt("views");
				post = new Post(id, title, authID, subtitle, body, image, creationDate, collectionID, isDraft, views);
				counter=counter+1;
				
			}
			rs.close();
			stmt.close();
			
			if(counter == 1){
				return post;
			}
			else{
				if(counter<1){
					System.err.println("No Post found using this ID!");
					return null;
				}
				else{
					System.err.println("Multiple Posts found by ID! DB failure!");
					return null;
				}


			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		return null;
		}
    	
    }
    public boolean createPost(Post post){
    	try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO POST "
					+ "VALUES (?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, post.getTitle());
			stmt.setInt(2, post.getAuthorID());
			stmt.setString(3, post.getSubtitle());
			stmt.setString(4, post.getBody());
			stmt.setString(5, post.getImage());
			stmt.setDate(6, post.getCreationDate());
			stmt.setInt(7, post.getCollectionID());
			stmt.setBoolean(8, post.isDraft());
			stmt.setInt(9, post.getViews());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    public boolean changePost(Post post){
    	try {
			PreparedStatement stmt = connection.prepareStatement(
					"UPDATE USER SET title = ? WHERE id = ?;"
					+ "UPDATE USER SET subtitle=? WHERE id=?;"
					+ "UPDATE USER SET body=? WHERE id=?;"
					+ "UPDATE USER SET image=? WHERE id=?;"
					+ "UPDATE USER SET collectionID=? WHERE id=?"
					+ "UPDATE USER SET isDraft=? WHERE id=?");
			stmt.setString(1, post.getTitle());
			stmt.setString(2, post.getSubtitle());
			stmt.setString(3, post.getBody());
			stmt.setString(4, post.getImage());
			stmt.setInt(5, post.getCollectionID());
			stmt.setBoolean(6, post.isDraft());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }

    public ArrayList<Comment> getCommentsByPost(int post_ID){
    	ArrayList<Comment> CommentsInDB = new ArrayList<Comment>();
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM COMMENT WHERE postID=?;");
			stmt.setInt(1, post_ID);
			ResultSet rs = stmt.executeQuery();
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				int authID = rs.getInt("authorID");
				int postID = rs.getInt("postID");        	
				String body = rs.getString("body");
				Date creationDate = rs.getDate("creationDate");
				Comment comment = new Comment(id, authID, postID, body, creationDate);
				CommentsInDB.add(comment);
				
			}

			rs.close();
			stmt.close();
			return CommentsInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public boolean createComment(Comment comment){
    	try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO COMMENT "
					+ "VALUES (?,?,?,?)");
			stmt.setInt(1, comment.getAuthorID());
			stmt.setInt(2, comment.getPostID());
			stmt.setString(3, comment.getBody());
			stmt.setDate(4, comment.getCreationDate());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
    public ArrayList<Collection> getCollections(){
    	ArrayList<Collection> CollectionsInDB = new ArrayList<Collection>();
    	try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM COLLECTION;" );
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String title  = rs.getString("title");
				String image = rs.getString("image");
				int views = rs.getInt("views");
				int posts = rs.getInt("posts");
				Collection collection = new Collection(id, title, image, views, posts);
				CollectionsInDB.add(collection);
				
			}
			rs.close();
			stmt.close();
			return CollectionsInDB;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	
    }
    public ArrayList<Collection> getCollectionsByName(String name){
    	ArrayList<Collection> CollectionsInDB = new ArrayList<Collection>();
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM COLLECTION WHERE title=?;");
			stmt.setString(1, name);
			ResultSet rs = stmt.executeQuery();
			
	        while ( rs.next() ) {

	        	int id = rs.getInt("id");
	        	String title  = rs.getString("title");
	        	String image = rs.getString("image");
	        	int views = rs.getInt("views");
	        	int posts = rs.getInt("posts");
	        	Collection collection = new Collection(id, title, image, views, posts);
	        	CollectionsInDB.add(collection);
	        	
	        }
	        rs.close();
	        stmt.close();
	        return CollectionsInDB;
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
    	 	
    }
    /*
     * what for do we need this ?
     * public Collection getSingleCollection(int ID){
    	
    }*/
    public boolean createCollection(Collection collection){
    	try {
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO COLLECTION "
					+ "VALUES (?,?,?,?)");
			stmt.setString(1, collection.getTitle());
			stmt.setString(2, collection.getImage());
			stmt.setInt(3, collection.getViews());
			stmt.setInt(4, collection.getPosts());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
    public boolean changeCollection(Collection collection){
    	try {
			PreparedStatement stmt = connection.prepareStatement(
					"UPDATE COLLECTION SET title = ? WHERE id = ?;"
					+ "UPDATE COLLECTION SET image=? WHERE id=?;"
					+ "UPDATE COLLECTION SET views=? WHERE id=?;"
					+ "UPDATE COLLECTION SET posts=? WHERE id=?;");
			stmt.setString(1, collection.getTitle());
			stmt.setString(2, collection.getImage());
			stmt.setInt(3, collection.getViews());
			stmt.setInt(4, collection.getPosts());
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    public boolean incCollViews(int collID){
    	int views=0;
    	int counter=0;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT views FROM COLLECTION WHERE id=?;");
			stmt.setInt(1, collID);
			ResultSet rs = stmt.executeQuery();
			
	        while ( rs.next() ) {

	        	views = rs.getInt("views");
	        	counter=counter+1;
	        	
	        }
	        rs.close();
	        stmt.close();
	        if(counter==1){
	        	try{
	        		PreparedStatement stmt1 = connection.prepareStatement(
	    					"UPDATE COLLECTION SET views = ? WHERE id = ?;");
	        		stmt1.setInt(1, views+1);
	        		stmt1.close();
	        		return true;
	        		
	        	}
	        	catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        			return false;
	        	}
	        }
	        else{
	        	System.err.println("Couldn't handle DB-Query");
	        	return false;
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 	
    }
    
    public boolean decCollViews(int collID){
    	int views=0;
    	int counter=0;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT views FROM COLLECTION WHERE id=?;");
			stmt.setInt(1, collID);
			ResultSet rs = stmt.executeQuery();
			
	        while ( rs.next() ) {

	        	views = rs.getInt("views");
	        	counter=counter+1;
	        	
	        }
	        rs.close();
	        stmt.close();
	        if(counter==1){
	        	try{
	        		PreparedStatement stmt1 = connection.prepareStatement(
	    					"UPDATE COLLECTION SET views = ? WHERE id = ?;");
	        		stmt1.setInt(1, views-1);
	        		stmt1.close();
	        		return true;
	        		
	        	}
	        	catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        			return false;
	        	}
	        }
	        else{
	        	System.err.println("Couldn't handle DB-Query");
	        	return false;
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    public boolean incCollPosts(int collID){
    	int posts=0;
    	int counter=0;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT posts FROM COLLECTION WHERE id=?;");
			stmt.setInt(1, collID);
			ResultSet rs = stmt.executeQuery();
			
	        while ( rs.next() ) {

	        	posts = rs.getInt("posts");
	        	counter=counter+1;
	        	
	        }
	        rs.close();
	        stmt.close();
	        if(counter==1){
	        	try{
	        		PreparedStatement stmt1 = connection.prepareStatement(
	    					"UPDATE COLLECTION SET posts = ? WHERE id = ?;");
	        		stmt1.setInt(1, posts+1);
	        		stmt1.close();
	        		return true;
	        		
	        	}
	        	catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        			return false;
	        	}
	        }
	        else{
	        	System.err.println("Couldn't handle DB-Query");
	        	return false;
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}	
    }
    public boolean decCollPosts(int collID){
    	int posts=0;
    	int counter=0;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT posts FROM COLLECTION WHERE id=?;");
			stmt.setInt(1, collID);
			ResultSet rs = stmt.executeQuery();
			
	        while ( rs.next() ) {

	        	posts = rs.getInt("posts");
	        	counter=counter+1;
	        	
	        }
	        rs.close();
	        stmt.close();
	        if(counter==1){
	        	try{
	        		PreparedStatement stmt1 = connection.prepareStatement(
	    					"UPDATE COLLECTION SET posts = ? WHERE id = ?;");
	        		stmt1.setInt(1, posts-1);
	        		stmt1.close();
	        		return true;
	        		
	        	}
	        	catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        			return false;
	        	}
	        }
	        else{
	        	System.err.println("Couldn't handle DB-Query");
	        	return false;
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }
    public boolean incPostViews(int postID){
    	int views=0;
    	int counter=0;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT views FROM POST WHERE id=?;");
			stmt.setInt(1, postID);
			ResultSet rs = stmt.executeQuery();
			
	        while ( rs.next() ) {

	        	views = rs.getInt("views");
	        	counter=counter+1;
	        	
	        }
	        rs.close();
	        stmt.close();
	        if(counter==1){
	        	try{
	        		PreparedStatement stmt1 = connection.prepareStatement(
	    					"UPDATE POST SET views = ? WHERE id = ?;");
	        		stmt1.setInt(1, views+1);
	        		stmt1.close();
	        		return true;
	        		
	        	}
	        	catch (SQLException e) {
	        			// TODO Auto-generated catch block
	        			e.printStackTrace();
	        			return false;
	        	}
	        }
	        else{
	        	System.err.println("Couldn't handle DB-Query");
	        	return false;
	        }
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} 	
    	
    }
    public boolean deletePost(int postID){
    	try {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM POST WHERE id=?");
			stmt.setInt(1, postID);
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    	
    }
    public boolean deleteComment(int commentID){
    	try {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM COMMENT WHERE id=?");
			stmt.setInt(1, commentID);
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    
    }
    public boolean deleteUser(int userID){
    	try {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM USER WHERE id=?");
			stmt.setInt(1, userID);
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
    public boolean deleteCollection(int collID){
    	try {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM COLLECTION WHERE id=?");
			stmt.setInt(1, collID);
			stmt.execute();
			stmt.close();
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	
    }
}