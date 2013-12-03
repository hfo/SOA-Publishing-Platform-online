package webservice.representations;

import java.sql.Date;

public class Post {
	private int ID;
	private String title;
	private int authorID;
	private String subtitle;
	private String body;
	private String image;
	private Date creationDate;
	private int collectionID;
	private boolean isDraft;
	private int views;
	
	public Post(int i,String t, int ai, String s, String b, String img, Date d, int ci, boolean isD, int v){
		setID(i);
		setTitle(t);
		setAuthorID(ai);
		setSubtitle(s);
		setBody(b);
		setImage(img);
		setCreationDate(d);
		setCollectionID(ci);
		setDraft(isD);
		setViews(v);
		
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getAuthorID() {
		return authorID;
	}
	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public int getCollectionID() {
		return collectionID;
	}
	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}
	public boolean isDraft() {
		return isDraft;
	}
	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}

	
}