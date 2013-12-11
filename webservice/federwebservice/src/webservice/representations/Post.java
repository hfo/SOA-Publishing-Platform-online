package webservice.representations;

import java.sql.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Post {
	@JsonIgnore private int ID;
	@JsonProperty("title")private String title;
	@JsonProperty("authorID")private int authorID;
	@JsonProperty("subtitle")private String subtitle;
	@JsonProperty("body")private String body;
	@JsonProperty("image")private String image;
	@JsonProperty ("creationDate")private String creationDate;
	@JsonProperty("collectionId")private int collectionID;
	@JsonProperty("isDraft") private int isDraft;
	@JsonProperty("views")private int views;
	
	public Post(){
		
	}
	public Post(int i,String t, int ai, String s, String b, String img, String d, int ci, int isD, int v){
		setID(i);
		setTitle(t);
		setAuthorID(ai);
		setSubtitle(s);
		setBody(b);
		setImage(img);
		setCreationDate(d);
		setCollectionID(ci);
		setIsDraft(isD);
		setViews(v);
		
	}
	@Override
	public String toString(){
		String postString= new String();

		postString="\"Post\":{"
							+"\"id\":"+"\""+String.valueOf(this.getID())+"\","
							+"\"title\":"+"\""+this.getTitle()+"\","
							+"\"authorId\":"+"\""+String.valueOf(this.getAuthorID())+"\","
							+"\"subtitle\":"+"\""+this.getSubtitle()+"\","
							+"\"body\":"+"\""+this.getBody()+"\","
							+"\"image\":"+"\""+this.getImage()+"\","
							+"\"creationDate\":"+"\""+this.getCreationDate()+"\","
							+"\"collectionId\":"+"\""+String.valueOf(this.getCollectionID())+"\","
							+"\"isDraft\":"+"\""+String.valueOf(this.getIsDraft())+"\","
							+"\"views\":"+"\""+String.valueOf(this.getViews())+"\"}";
		return postString;
		
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
	public String getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public int getCollectionID() {
		return collectionID;
	}
	public void setCollectionID(int collectionID) {
		this.collectionID = collectionID;
	}
	public int getIsDraft() {
		return isDraft;
	}
	public void setIsDraft(int isDraft) {
		this.isDraft = isDraft;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}

	
}
