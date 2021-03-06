package webservice.representations;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlRootElement;

import com.owlike.genson.annotation.JsonIgnore;
import com.owlike.genson.annotation.JsonProperty;



@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Collection {

	@JsonIgnore private int ID;
	@JsonProperty("title") public String title;
	@JsonProperty("image") public String image;
	@JsonProperty("views") public int views;
	@JsonProperty("posts") public int posts;
	
	public Collection(){
		
	}
	public Collection(int i, String t, String img, int v, int p){
		this.setID(i);
		this.setTitle(t);
		this.setImage(img);
		this.setViews(v);
		this.setPosts(p);		
	}
	
	@Override
	public String toString(){
		String collectionString= new String();
		collectionString="\"Collection\":{"
							+"\"id\":"+"\""+String.valueOf(this.getID())+"\","
							+"\"title\":"+"\""+this.getTitle()+"\","
							+"\"image\":"+"\""+this.getImage()+"\","
							+"\"views\":"+"\""+String.valueOf(this.getViews())+"\","
							+"\"posts\":"+"\""+String.valueOf(this.getPosts())+"\"}";
		return collectionString;
		
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public int getViews() {
		return views;
	}
	public void setViews(int views) {
		this.views = views;
	}
	public int getPosts() {
		return posts;
	}
	public void setPosts(int posts) {
		this.posts = posts;
	}

}
