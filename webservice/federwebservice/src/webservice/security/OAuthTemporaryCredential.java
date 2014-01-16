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
public class OAuthTemporaryCredential {
	@JsonIgnore private int id;
	@JsonProperty("oauth_token") private String oauth_token;
	@JsonProperty("oauth_token_secret") private String oauth_token_secret;
	@JsonProperty("oauth_callback") private String oauth_callback;
	@JsonProperty("timestamp") private String timestamp;
	
	public OAuthTemporaryCredential(int i, String oauth_token, String oauth_token_secret, String oauth_callback) {
		setId(i);
		setOauth_token(oauth_token);
		setOauth_token_secret(oauth_token_secret);
		setOauth_callback(oauth_callback);
		
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

	public String getOauth_token() {
		return oauth_token;
	}

	public void setOauth_token(String oauth_token) {
		this.oauth_token = oauth_token;
	}

	public String getOauth_token_secret() {
		return oauth_token_secret;
	}

	public void setOauth_token_secret(String oauth_token_secret) {
		this.oauth_token_secret = oauth_token_secret;
	}

	public String getOauth_callback() {
		return oauth_callback;
	}

	public void setOauth_callback(String oauth_callback) {
		this.oauth_callback = oauth_callback;
	}
	
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

}
