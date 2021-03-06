package webservice.database;

import java.sql.Connection; 
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
import webservice.security.Encryptor;
import webservice.security.OAuthAccessToken;
import webservice.security.OAuthTemporaryCredential;
import webservice.security.OAuthVerifier;

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

    } 
     
    public static DB_Connector getInstance(){ 
        return dbcon; 
    } 
     
    public void initDBConnection() { 
        try { 
            if (connection!=null)//connection != null 
                return; 
            System.out.println("Creating Connection to Database..."); 
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH); 
            if (!connection.isClosed()) 
                System.out.println("...Connection established"); 
        	createTableUser();
        	createTablePost();
        	createTableComment();
        	createTableCollection();
        	createTableTempCredential();
        	createTableAccessToken();
        	createTableVerifier();
        } catch (SQLException e) {
        	System.out.println("Fehler bei initDB");
            throw new RuntimeException(e); 
        }
    }
     public void closeDBConnection(){
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
    
    public Connection getConnection() {
    	return connection;
    }
     
    public void createTableUser(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS USER("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "username CHAR(25),"
					+ "password CHAR(25),"
					+ "email CHAR);");
			System.out.println("enterd user table");

		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 

		}
    }
    public void createTablePost(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS POST("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "title CHAR,"
					+ "authorID INTEGER,"
					+ "subtitle CHAR,"
					+ "body TEXT,"
					+ "image CHAR,"
					+ "creationDate NONE,"
					+ "collectionID INTEGER,"
					+ "isDraft INTEGER,"
					+ "views INTEGER);");
			System.out.println("enterd post table");

		} catch (SQLException e) {
            System.err.println("Couldn't create table Post"); 
            e.printStackTrace(); 

		}
    	
    }
    public void createTableComment(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS COMMENT("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "authorID INTEGER,"
					+ "postID INTEGER,"
					+ "body TEXT,"
					+ "creationDate NONE);");
			System.out.println("enterd user comment");

		} catch (SQLException e) {
            System.err.println("Couldn't create table Comment"); 
            e.printStackTrace(); 

		}
    	
    }
    public void createTableCollection(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS COLLECTION("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "title CHAR,"
					+ "image CHAR,"
					+ "views INTEGER,"
					+ "posts INTEGER);");
			System.out.println("enterd collection table");

		} catch (SQLException e) {
            System.err.println("Couldn't create table Collection"); 
            e.printStackTrace(); 

		}
    	
    }
    public void createTableTempCredential(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS TEMPCREDENTIALS("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "oauth_token VARCHAR(255),"
					+ "oauth_token_secret VARCHAR(255),"
					+ "oauth_callback VARCHAR(255),"
					+ "timestamp VARCHAR(255));");
			System.out.println("entered temporary credentials table");

		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 

		}
    }
    public void createTableAccessToken(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ACCESSTOKENS("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "oauth_token VARCHAR(255),"
					+ "oauth_token_secret VARCHAR(255),"
					+ "username VARCHAR(255),"
					+ "timestamp VARCHAR(255));");
			System.out.println("entered access token table");

		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 

		}
    }
    public void createTableVerifier(){
    	try {

			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS VERIFIERS("
					+ "id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "oauth_verifier VARCHAR(255),"
					+ "username VARCHAR(255),"
					+ "timestamp VARCHAR(255));");
			System.out.println("entered verifier table");

		} catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query"); 
            e.printStackTrace(); 

		}
    }
    public ArrayList<User> getUsers(){
    	ArrayList<User> UsersInDB = new ArrayList<User>();
    	try {
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
    		if(checkUsernameTaken(user.getUsername())==false){
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO USER (username,password,email)"
					+ "VALUES (?,?,?)");
			stmt.setString(1, user.getUsername());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getEmail());
			stmt.execute();
			stmt.close();
			
			return true;
    		}
    		else{return false;}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
    	
    	
    }
    public boolean changeUser(User user){
    	
    	try {
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("UPDATE USER SET username = ? WHERE id = ?;");
			stmt.setString(1, user.getUsername());
			stmt.setInt(2, user.getID());
			stmt.execute();
			stmt = connection.prepareStatement("UPDATE USER SET email=? WHERE id=?");
			stmt.setString(1, user.getEmail());
			stmt.setInt(2, user.getID());
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
    
    public boolean checkLogin(String name, String password){
    	PreparedStatement stmt;
    	String encrypted = Encryptor.get_SHA_1_SecurePassword(password);
    	try {
    		initDBConnection();
			stmt= connection.prepareStatement( "SELECT * FROM USER ;");
			ResultSet rs = stmt.executeQuery();
			     
			while ( rs.next() ) {
				if( name.equals(rs.getString("username")) ){
					if( encrypted.equals(rs.getString("password")) ){
						System.out.println("LoginData correct");
						return true;
					}
					else{System.out.println("LoginData not correct");
						return false;}
				}
				
			}
			System.out.println("No such Username");
			return false;
			        
		}
    	catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
    }
    
    public boolean checkUsernameTaken(String username){
    	PreparedStatement stmt;
    	
    	try {
    		initDBConnection();
			stmt= connection.prepareStatement( "SELECT * FROM USER ;");
			ResultSet rs = stmt.executeQuery();
			System.out.println("JSON:"+ username);
			String curUsername;
			while ( rs.next() ) {
				curUsername=rs.getString("username");
				System.out.println("DB:"+ curUsername);
				if( curUsername.equals(username)){
					System.out.println("UserName is taken");
					return true;
				}
			}
			System.out.println("UserName is not taken");
			return false;
			        
		}
    	catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
    }
    public ArrayList<Post> getPosts(){
    	ArrayList<Post> PostsInDB = new ArrayList<Post>();
    	try {
    		initDBConnection();
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM POST;" );
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				String title  = rs.getString("title");
				int authID = rs.getInt("authorID");
				String subtitle = rs.getString("subtitle");
				String body = rs.getString("body");
				String image = rs.getString("image");
				String creationDate = rs.getString("creationDate");
				int collectionID = rs.getInt("collectionID");
				int isDraftInt  = rs.getInt("isDraft");
				int views = rs.getInt("views");
				Post post = new Post(id, title, authID, subtitle, body, image, creationDate, collectionID, isDraftInt, views);
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
    		initDBConnection();
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
				String creationDate = rs.getString("creationDate");
				int collectionID = rs.getInt("collectionID");
				int isDraft = rs.getInt("isDraft");
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
    		initDBConnection();
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
				String creationDate = rs.getString("creationDate");
				int collectionID = rs.getInt("collectionID");
				int isDraft = rs.getInt("isDraft");
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
    		initDBConnection();
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
				String creationDate = rs.getString("creationDate");
				int collectionID = rs.getInt("collectionID");
				int isDraft = rs.getInt("isDraft");
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
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO POST (title,authorID,subtitle,body,image,creationDate,collectionID,isDraft,views)"
					+ "VALUES (?,?,?,?,?,?,?,?,?)");
			stmt.setString(1, post.getTitle());
			stmt.setInt(2, post.getAuthorID());
			stmt.setString(3, post.getSubtitle());
			stmt.setString(4, post.getBody());
			stmt.setString(5, post.getImage());
			stmt.setString(6, post.getCreationDate());
			stmt.setInt(7, post.getCollectionID());
			stmt.setInt(8, post.getIsDraft());
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
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("UPDATE POST SET title = ? WHERE id = ?;");
			stmt.setString(1, post.getTitle());
			stmt.setInt(2, post.getID());
			stmt.execute();
			stmt = connection.prepareStatement("UPDATE POST SET subtitle=? WHERE id=?;");
			stmt.setString(1, post.getSubtitle());
			stmt.setInt(2, post.getID());
			stmt.execute();
			stmt = connection.prepareStatement("UPDATE POST SET body=? WHERE id=?;");
			stmt.setString(1, post.getBody());
			stmt.setInt(2, post.getID());
			stmt.execute();
			stmt = connection.prepareStatement("UPDATE POST SET image=? WHERE id=?;");
			stmt.setString(1, post.getImage());
			stmt.setInt(2, post.getID());
			stmt.execute();
			stmt = connection.prepareStatement("UPDATE POST SET collectionID=? WHERE id=?");
			stmt.setInt(1, post.getCollectionID());
			stmt.setInt(2, post.getID());
			stmt.execute();
			stmt = connection.prepareStatement("UPDATE POST SET isDraft=? WHERE id=?");
			stmt.setInt(1, post.getIsDraft());
			stmt.setInt(2, post.getID());
		
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
    		initDBConnection();
			stmt= connection.prepareStatement( "SELECT * FROM COMMENT WHERE postID=?;");
			stmt.setInt(1, post_ID);
			ResultSet rs = stmt.executeQuery();
			
			while ( rs.next() ) {

				int id = rs.getInt("id");
				int authID = rs.getInt("authorID");
				int postID = rs.getInt("postID");        	
				String body = rs.getString("body");
				String creationDate = rs.getString("creationDate");
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
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO COMMENT (authorID,postID,body,creationDate)"
					+ "VALUES (?,?,?,?)");
			stmt.setInt(1, comment.getAuthorID());
			stmt.setInt(2, comment.getPostID());
			stmt.setString(3, comment.getBody());
			stmt.setString(4, comment.getCreationDate());
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
    		initDBConnection();
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


    public Collection getSingleCollection(int ID){
    	int counter =0; 
    	Collection collection=null;
    	PreparedStatement stmt;
    	try {
			stmt= connection.prepareStatement( "SELECT * FROM COLLECTION WHERE id=?;");
			stmt.setInt(1, ID);
			ResultSet rs = stmt.executeQuery();
			     
			while ( rs.next() ) {
				
			
				int id = rs.getInt("id");
	        	String title  = rs.getString("title");
	        	String image = rs.getString("image");
	        	int views = rs.getInt("views");
	        	int posts = rs.getInt("posts");
	        	collection = new Collection(id, title, image, views, posts);
				counter=counter+1;
			}	
			        
			stmt.close();
			rs.close();
			
			if(counter == 1){
				
				return collection;
			}
			else{
				if(counter<1){
					System.err.println("No Collection was found using this ID!");
					
					return null;
				}
				else{
					System.err.println("Multiple Collections found by ID! DB failure!");
					
					return null;
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}
    }
    public boolean createCollection(Collection collection){
    	try {

    		PreparedStatement stmt = connection.prepareStatement("INSERT INTO COLLECTION (title,image,views,posts) "
					+ "VALUES (?,?,?,?)");
			System.out.println("vor setzen der varaiblen");
			stmt.setString(1, collection.getTitle());
			stmt.setString(2, collection.getImage());
			stmt.setInt(3, collection.getViews());
			stmt.setInt(4, collection.getPosts());
			System.out.println("nach setzen der varaiblen");
			stmt.execute();
			stmt.close();
			System.out.println("stmt ende");
			System.out.println("collection created title"+collection.getTitle()+" Image"+collection.getImage()+" Views:"+collection.getViews()+" Posts:"+collection.getPosts());

			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			return false;
		}
    	
    }
    public boolean changeCollection(Collection collection){
    	try {
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("UPDATE COLLECTION SET title = ? WHERE id=?;");
			stmt.setString(1, collection.getTitle());
			stmt.setInt(2,collection.getID());
			stmt.execute();
			stmt=connection.prepareStatement("UPDATE COLLECTION SET image=? WHERE id=?;");
			stmt.setString(1, collection.getImage());
			stmt.setInt(2,collection.getID());
			stmt.execute();
			stmt=connection.prepareStatement("UPDATE COLLECTION SET views=? WHERE id=?;");
			stmt.setInt(1, collection.getViews());
			stmt.setInt(2,collection.getID());
			stmt.execute();
			stmt=connection.prepareStatement("UPDATE COLLECTION SET posts=? WHERE id=?;");
			stmt.setInt(1, collection.getPosts());
			stmt.setInt(2,collection.getID());
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
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
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
	        
		} 
    	catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		} 	
    	
    }
    public boolean deletePost(int postID){
    	try {
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
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
    		initDBConnection();
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
    public boolean createTemporaryCredential(OAuthTemporaryCredential tempCredential){
    	try {
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO TEMPCREDENTIALS (oauth_token, oauth_token_secret, oauth_callback, timestamp)"
					+ "VALUES (?,?,?,?)");
			stmt.setString(1, tempCredential.getOauth_token());
			stmt.setString(2, tempCredential.getOauth_token_secret());
			stmt.setString(3, tempCredential.getOauth_callback());
			stmt.setString(4, tempCredential.getTimestamp());
			stmt.execute();
			stmt.close();
			
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
    }
    public boolean createAccessToken(OAuthAccessToken token){
    	try {
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO ACCESSTOKENS (oauth_token, oauth_token_secret, username, timestamp)"
					+ "VALUES (?,?,?,?)");
			stmt.setString(1, token.getOauth_token());
			stmt.setString(2, token.getOauth_token_secret());
			stmt.setString(3, token.getUsername());
			stmt.setString(4, token.getTimestamp());
			stmt.execute();
			stmt.close();
			
			return true;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return false;
		}
    }
    public boolean createVerifier(OAuthVerifier verifier){
    	try {
    		initDBConnection();
			PreparedStatement stmt = connection.prepareStatement("INSERT INTO VERIFIERS (oauth_verifier, username, timestamp)"
					+ "VALUES (?,?,?)");
			stmt.setString(1, verifier.getOauth_verifier());
			stmt.setString(2, verifier.getUsername());
			stmt.setString(3, verifier.getTimestamp());
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