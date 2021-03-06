package webservice.security;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
		
		// TODO: check oauth_signature QUESTION when done like in checkTokenRequest there are missing some handovers
		
		dbcon.initDBConnection();
		Connection connection = dbcon.getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM ACCESSTOKENS WHERE oauth_token = ?");
		stmt.setString(1, oauth_token);
		ResultSet rs = stmt.executeQuery();
		
		// DONE: check expiration of temporary token via its timestamp===> QUESTION: check accesstokens for expiration or was something else ment to be done?     if yes remove comment in IF
		while ( rs.next() ) {
			int expTime = 15;
			String stamp = rs.getString("timestamp");
			DateFormat format = new SimpleDateFormat("EEE MMM d H:m:s z yyyy",Locale.UK);
			Date date = new Date();
			
			try {
				date = format.parse(stamp);	
				System.out.println("Timestamp: "+date.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Calendar cal = Calendar.getInstance();
			Date now = cal.getTime();
			System.out.println("NOW: "+now.toString());
			cal.setTime(date);
			cal.add(cal.MINUTE, expTime);
			Date expDate = cal.getTime();	
			System.out.println("expDate: "+expDate.toString());	
			

			
			if (rs.getString("username").equals(resource_owner)/*&& now.before(expDate)*/) {
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
		// DONE: check expiration of temporary token via its timestamp
		//check if expiring date expDate=timestamp+expTime is before the current time 

		while ( rs.next() ) {
				int expTime = 15;
				String stamp = rs.getString("timestamp");
				DateFormat format = new SimpleDateFormat("EEE MMM d H:m:s z yyyy",Locale.UK);
				Date date = new Date();
				
				try {
					date = format.parse(stamp);	
					System.out.println("Timestamp: "+date.toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				
				Calendar cal = Calendar.getInstance();
				Date now = cal.getTime();
				System.out.println("NOW: "+now.toString());
				cal.setTime(date);
				cal.add(cal.MINUTE, expTime);
				Date expDate = cal.getTime();	
				System.out.println("expDate: "+expDate.toString());	
				
				if(now.before(expDate)){
					valid = true;	
				}
	
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
		
		dbcon.initDBConnection();
		Connection connection = dbcon.getConnection();
		
		// DONE: check temporal credentials for expiration (oauth_token)
		PreparedStatement stmt1 = connection.prepareStatement( "SELECT * FROM TEMPCREDENTIALS WHERE oauth_token = ?;");
		stmt1.setString(1, oauth_token);
		ResultSet rs1 = stmt1.executeQuery();
		boolean enterRS1=false;
		
		while ( rs1.next() ) {
			enterRS1=true;
			int expTime = 15;
			String stamp = rs1.getString("timestamp");
			DateFormat format = new SimpleDateFormat("EEE MMM d H:m:s z yyyy",Locale.UK);
			Date date = new Date();
			
			try {
				date = format.parse(stamp);	
				System.out.println("Timestamp: "+date.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Calendar cal = Calendar.getInstance();
			Date now = cal.getTime();
			System.out.println("NOW: "+now.toString());
			cal.setTime(date);
			cal.add(cal.MINUTE, expTime);
			Date expDate = cal.getTime();	
			System.out.println("expDate: "+expDate.toString());	
			
			if(!now.before(expDate)){
				validRequest = false;	
			}
		}
		stmt1.close();
		rs1.close();
		
		//false validRequest when no oauth_token was found in DB
		if(!enterRS1){
			validRequest=false;
		}
	
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
			
			// DONE: delete temporary credentials and verifier from database
			PreparedStatement stmt2 = connection.prepareStatement( "DELETE FROM TEMPCREDENTIALS WHERE oauth_token = ?;");
			stmt2.setString(1, oauth_token);
			stmt2.execute();
			PreparedStatement stmt3 = connection.prepareStatement( "DELETE FROM VERIFIERS WHERE oauth_verifier = ?;");
			stmt3.setString(1, oauth_verifier);
			stmt3.execute();
			
			return response;
		}
		
		
		
		return "";
	}

}
