package webservice.representations;

import java.sql.Date;

public class Comment {

	private int ID;
	private int authorID;
	private int postID;
	private String body;
	private Date creationDate;
	
	public Comment(int i, int ai, int pi, String b, Date cd){
		setID(i);
		setAuthorID(ai);
		setPostID(pi);
		setBody(b);
		setCreationDate(cd);
		
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
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}