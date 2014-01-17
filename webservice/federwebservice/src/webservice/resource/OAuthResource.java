package webservice.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import webservice.database.DB_Connector;
import webservice.representations.Collection;
import webservice.representations.User;
import webservice.security.OAuthAccessToken;
import webservice.security.OAuthProvider;
import webservice.security.OAuthTemporaryCredential;
import webservice.security.OAuthTokenGenerator;
import webservice.security.OAuthVerifier;

@Path("oauth")
public class OAuthResource {
	DB_Connector dbcon = DB_Connector.getInstance();
	OAuthProvider oauth = new OAuthProvider();
	
	/**
	 * First step.
	 * Verify the client credentials and provide temporary credentials
	 * for the client.
	 * 
	 * @param oauth_consumer_key
	 * @param oauth_signature_method
	 * @param oauth_callback
	 * @param oauth_signature
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 */
	@GET	// TODO: make POST
	@Path("/initiate")
	@Produces(MediaType.APPLICATION_FORM_URLENCODED)
	public Response initiate(
			@QueryParam("oauth_consumer_key") String oauth_consumer_key,
			@QueryParam("oauth_signature_method") String oauth_signature_method,
			@QueryParam("oauth_callback") String oauth_callback,
			@QueryParam("oauth_signature") String oauth_signature,
			@Context HttpServletRequest request
			) throws UnsupportedEncodingException {
		
		String check = oauth.checkInitializeRequest(oauth_consumer_key, oauth_signature_method, oauth_callback, oauth_signature, request);
		
		if (check.equals("")) {
			return Response.status(401).build();
		} else {
			return Response.status(200).entity(check).build();
		}
	}
	
	/**
	 * Second step.
	 * Ask user for authorization and redirect back to client.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws SQLException 
	 */
	@GET
	@Path("/authorize")
	@Produces(MediaType.TEXT_HTML)
	public Response authorize(
			@QueryParam("oauth_token") String oauth_token,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response
			) throws IOException, SQLException {
		System.out.println(oauth_token);
		System.out.println(response);
		
		boolean valid = oauth.checkAuthorizeRequest(oauth_token, response);
		System.out.println(valid);
		// redirect to signin, if oauth_token was valid (in database)
		if (valid) {
			response.sendRedirect("signin?oauth_token="+oauth_token);
			return Response.status(200).build();
		} else {
			return Response.status(401).build();
		}
	}
	
	/**
	 * Third step.
	 * Exchange access token with client.
	 * 
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws SQLException 
	 */
	@GET
	@Path("/token")
	@Produces(MediaType.APPLICATION_JSON)
	public Response token(
			@QueryParam("oauth_consumer_key") String oauth_consumer_key,
			@QueryParam("oauth_signature_method") String oauth_signature_method,
			@QueryParam("oauth_signature") String oauth_signature,
			@QueryParam("oauth_verifier") String oauth_verifier,
			@QueryParam("oauth_token") String oauth_token,
			@Context HttpServletRequest request
			) throws UnsupportedEncodingException, SQLException {
		
		String check = oauth.checkTokenRequest(oauth_consumer_key, oauth_signature_method, oauth_signature, oauth_verifier, oauth_token, request);
		
		if (check.equals("")) {
			return Response.status(401).build();
		} else {
			return Response.status(200).entity(check).build();
		}
	}
	
	@GET
	@Path("/signin")
	@Produces(MediaType.TEXT_HTML)
	public String signin(
			@QueryParam("oauth_token") String oauth_token,
			@Context HttpServletRequest request
			) {
		// TODO: check validity of auth_token
		
		String output = 	 "<html>"
							+"	<head>"
							+"		<title>Feder Webservice Authentication</title>"
							+" 		<style type=\"text/css\">"
							+" 			body { font-family: 'Helvetica Neue', 'Helvetica', sans-serif; line-height: 1.4; }"
							+"			p { width: 400px; margin: 100px auto 0; }"
							+" 			i, strong { color: #4285F4; }"
							+" 			form { width: 400px; margin: 40px auto; }"
							+" 			input { width: 174px; margin-bottom: 10px; padding: 8px; margin-right: 15px; border: 1px solid #aaa; border-radius: 4px; }"
							+" 			input[type=submit] { background: #4285F4; width: auto; padding: 8px 16px; border: none; color: #fff; font-weight: bold; cursor: pointer; border-radius: 4px; }"
							+" 		</style>"
							+"	</head>"
							+"	<body>"
							+"		<p>The <strong>Feder Web App</strong> wants to connect to your user account. The app will have the ability to <i>create, alter and delete your posts</i>. Enter your user credentials to grant access.</p>"
							+" 		<form action=\"/federwebservice/rest/oauth/signin\" method=\"post\">"
							+" 			<input type=\"text\" name=\"username\" placeholder=\"Username...\" />"
							+" 			<input type=\"password\" name=\"password\" placeholder=\"Password...\" />"
							+" 			<input type=\"hidden\" name=\"oauth_token\" value=\""+oauth_token+"\" />"
							+" 			<input type=\"submit\" name=\"submit\" value=\"Grant Access\" />"
							+" 		</form>"
							+"	</body>"
							+"</html>";
		
		return output;
	}
	
	@POST
	@Path("/signin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response signin(
			MultivaluedMap<String, String> form,
			@Context HttpServletRequest request,
			@Context HttpServletResponse response
			) throws SQLException, IOException {
		
			String oauth_token = form.get("oauth_token").get(0);
			String username = form.get("username").get(0);
			String password = form.get("password").get(0);
			String callback = "";
		     // DONE: !!! fetch and check real credentials from database !!!
			if (dbcon.checkLogin(username, password)) {
				// get callback url
				dbcon.initDBConnection();
				Connection connection = dbcon.getConnection();
				PreparedStatement stmt = connection.prepareStatement( "SELECT * FROM TEMPCREDENTIALS WHERE oauth_token = ?;");
				stmt.setString(1, oauth_token);
				ResultSet rs = stmt.executeQuery();
				     
				while ( rs.next() ) {
					callback = rs.getString("oauth_callback");
					callback = java.net.URLDecoder.decode(callback, "utf-8");
				}	
				        
				stmt.close();
				rs.close();
				
				//generate oauth_verifier and pass with redirect to client
				OAuthTokenGenerator gen = new OAuthTokenGenerator();
				String verifier = gen.generateToken(10);
				OAuthVerifier oauth_verifier = new OAuthVerifier(-1, verifier, username);
				dbcon.createVerifier(oauth_verifier);
				
				response.sendRedirect(callback+"?oauth_verifier="+verifier+"&oauth_token="+oauth_token);
				return Response.status(200).build();
			}
			
			return Response.status(401).build();
	}
		
}
