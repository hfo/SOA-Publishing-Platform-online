package webservice.security;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OAuthVerifier {
	@JsonIgnore private int id;
	@JsonProperty("oauth_verifier") private String oauth_verifier;
	@JsonProperty("username") private String username;
	@JsonProperty("timestamp") private String timestamp;
	
	public OAuthVerifier(int i, String oauth_verifier, String username) {
		setId(i);
		setOauth_verifier(oauth_verifier);
		setUsername(username);
		
		Calendar cal = Calendar.getInstance();
		Date now = cal.getTime();
		setTimestamp(now.toString());
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getOauth_verifier() {
		return oauth_verifier;
	}

	public void setOauth_verifier(String oauth_verifier) {
		this.oauth_verifier = oauth_verifier;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
