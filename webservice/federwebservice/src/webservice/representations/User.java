package webservice.representations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class User {
	
	@JsonProperty("id") private int ID;
	@JsonProperty private String username;
	@JsonProperty private String password;
	@JsonProperty private String email;
	
	public User(){
		
	}
	public User(int i, String u, String e){
		setID(i);
		setUsername(u);
		setEmail(e);
	}
	@Override
	public String toString(){
		String userString= new String();

		userString="\"Post\":{"
							+"\"id\":"+"\""+String.valueOf(this.getID())+"\","
							+"\"username\":"+"\""+this.getUsername()+"\","
							+"\"email\":"+"\""+this.getEmail()+"\"}";
		return userString;
		
	}
	public User(int i, String u, String p, String e){
		
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

}
