package webservice.representations;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.owlike.genson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Comment {

	@JsonProperty("id") private int ID;
	@JsonProperty("authorId") private int authorID;
	@JsonProperty("postId") private int postID;
	@JsonProperty("body") private String body;
	@JsonProperty("creationDate") private String creationDate;
	
	public Comment(){
		
	}
	public Comment(int i, int ai, int pi, String b, String cd){
		setID(i);
		setAuthorID(ai);
		setPostID(pi);
		setBody(b);
		setCreationDate(cd);
		
	}
	@Override
	public String toString(){
		String commentString= new String();

		commentString="\"Comment\":{"
							+"\"id\":"+"\""+String.valueOf(this.getID())+"\","
							+"\"authorId\":"+"\""+String.valueOf(this.getAuthorID())+"\","
							+"\"postId\":"+"\""+String.valueOf(this.getPostID())+"\","
							+"\"body\":"+"\""+this.getBody()+"\","
							+"\"creationDate\":"+"\""+this.getCreationDate().toString()+"\"}";
		return commentString;
		
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getAuthorID() {
		return authorID;
	}
	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}
	public int getPostID() {
		return postID;
	}
	public void setPostID(int postID) {
		this.postID = postID;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
}
