package webservice.security;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import webservice.database.DB_Connector;

public class OAuthProvider {
	DB_Connector dbcon = DB_Connector.getInstance();
	
	public boolean checkAuthorization(
			String resource_owner, 
			String oauth_token
			) throws SQLException {
		boolean authorized = false;
		
		// TODO: check oauth_signature
		
		dbcon.initDBConnection();
		Connection connection = dbcon.getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACCESSTOKENS WHERE oauth_token = ?");
		stmt.setString(1, oauth_token);
		ResultSet rs = stmt.executeQuery();
		     
		while ( rs.next() ) {
			// TODO: check expiration of temporary token via its timestamp
			if (rs.getString("username").equals(resource_owner)) {
				authorized = true;
			}
		}	
		        
		stmt.close();
		rs.close();
		
		return authorized;
	}
	
	public String checkInitializeRequest(
			String oauth_consumer_key,
			String oauth_signature_method,
			String oauth_callback,
			String oauth_signature,
			HttpServletRequest request
			) throws UnsupportedEncodingException {
		
		// we are using the PLAINTEXT method exclusively, so we require HTTPS requests
		if (oauth_signature_method.equals("PLAINTEXT")) {
			// construct the base string uri
			String port = "";
			if (request.getServerPort() != 80) {
				port = ":"+request.getServerPort();
			}
			
			String base_string_uri = request.getScheme()+"://"+request.getServerName()+""+port+""+request.getPathInfo();
		
			// encode parameter
			base_string_uri = java.net.URLEncoder.encode(base_string_uri, "utf-8");
			oauth_callback = java.net.URLEncoder.encode(oauth_callback, "utf-8");
			oauth_consumer_key = java.net.URLEncoder.encode(oauth_consumer_key, "utf-8");
			oauth_signature_method = java.net.URLEncoder.encode(oauth_signature_method, "utf-8");
			
			// build the signature base string to verify with the provided oauth signature
			String signature_base_string = "GET%26"+base_string_uri+"%26oauth_callback%3D"+oauth_callback+"%26oauth_consumer_key%3D"+oauth_consumer_key+"%26oauth_signature_method%3D"+oauth_signature_method;
			oauth_signature = java.net.URLEncoder.encode(oauth_signature, "utf-8");
			
			// verify provided oauth signature and respond with temporary credential or an 401 response
			if (oauth_signature.equals(signature_base_string)) {
				OAuthTokenGenerator tokenGen = new OAuthTokenGenerator();
				String oauth_token = tokenGen.generateToken(10);
				String oauth_token_secret = tokenGen.generateToken(20);
				
				// store temporary credentials
				OAuthTemporaryCredential c = new OAuthTemporaryCredential(-1, oauth_token, oauth_token_secret, oauth_callback);
				dbcon.initDBConnection();
				dbcon.createTemporaryCredential(c);
				
				// build response
				String response = "oauth_token="+oauth_token+"&oauth_token_secret="+oauth_token_secret+"&oauth_callback_confirmed=true";
				return response;
			} else {
				return "";
			}
		}			
					
		return "";
	}
	
	public boolean checkAuthorizeRequest(
			String oauth_token, 
			HttpServletResponse response
			) throws SQLException {
		
		// check oauth_token
		boolean valid = false;
		
		dbcon.initDBConnection();
		Connection connection = dbcon.getConnection();
		PreparedStatement stmt = connection.prepareStatement( "SELECT * FROM TEMPCREDENTIALS WHERE oauth_token = ?;");
		stmt.setString(1, oauth_token);
		ResultSet rs = stmt.executeQuery();
		     
		while ( rs.next() ) {
			// TODO: check expiration of temporary token via its timestamp
			valid = true;
		}	
		        
		stmt.close();
		rs.close();
		
		return valid;
	}
	
	public String checkTokenRequest(
			String oauth_consumer_key,
			String oauth_signature_method,
			String oauth_signature,
			String oauth_verifier,
			String oauth_token,
			HttpServletRequest request
			) throws SQLException, UnsupportedEncodingException {
		// request validation status
		boolean validRequest = true;
		
		// TODO: check temporal credentials for expiration (oauth_token)
	
		// verifiy consumer credentials
		if (oauth_signature_method.equals("PLAINTEXT")) {
			// construct the base string uri
			String port = "";
			if (request.getServerPort() != 80) {
				port = ":"+request.getServerPort();
			}
			
			String base_string_uri = request.getScheme()+"://"+request.getServerName()+""+port+""+request.getPathInfo();
		
			// encode parameter
			base_string_uri = java.net.URLEncoder.encode(base_string_uri, "utf-8");
			oauth_consumer_key = java.net.URLEncoder.encode(oauth_consumer_key, "utf-8");
			oauth_token = java.net.URLEncoder.encode(oauth_token, "utf-8");
			oauth_verifier = java.net.URLEncoder.encode(oauth_verifier, "utf-8");
			oauth_signature_method = java.net.URLEncoder.encode(oauth_signature_method, "utf-8");
			
			// build the signature base string to verify with the provided oauth signature
			String signature_base_string = "GET%26"+base_string_uri+"%26oauth_consumer_key%3D"+oauth_consumer_key+"%26oauth_signature_method%3D"+oauth_signature_method+"%26oauth_token%3D"+oauth_token+"%26oauth_verifier%3D"+oauth_verifier;
			oauth_signature = java.net.URLEncoder.encode(oauth_signature, "utf-8");
			
			// verify provided oauth signature and respond with temporary credential or an 401 response
			if (!oauth_signature.equals(signature_base_string)) {
				validRequest = false;
			}
		} else {
			validRequest = false;
		}
		
		// verify oauth_verifier (check existance in database)
		dbcon.initDBConnection();
		Connection connection = dbcon.getConnection();
		PreparedStatement stmt = connection.prepareStatement( "SELECT * FROM VERIFIERS WHERE oauth_verifier = ?;");
		stmt.setString(1, oauth_verifier);
		ResultSet rs = stmt.executeQuery();
		String username = "";
		
		if (!rs.next()) {
			validRequest = false;
		}
		
		username = rs.getString("username");
		        
		stmt.close();
		rs.close();
		
		// create access token and pass to client
		if (validRequest) {
			OAuthTokenGenerator gen = new OAuthTokenGenerator();
			String oauth_access_token = gen.generateToken(10);
			String oauth_access_token_secret = gen.generateToken(20);
			OAuthAccessToken token = new OAuthAccessToken(-1, oauth_access_token, oauth_access_token_secret, username);
			dbcon.initDBConnection();
			dbcon.createAccessToken(token);
			
			// build response
			String response = "oauth_token="+oauth_access_token+"&oauth_token_secret="+oauth_access_token_secret+"&username="+username;
			return response;
		}
		
		// TODO: delete temporary credentials and verifier from database
		
		return "";
	}

}