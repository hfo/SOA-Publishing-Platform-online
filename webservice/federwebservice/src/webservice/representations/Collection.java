package webservice.representations;

public class Collection {

	private int ID;
	private String title;
	private String image;
	private int views;
	private int posts;
	
	public Collection(int i, String t, String img, int v, int p){
		setID(i);
		setTitle(t);
		setImage(img);
		setViews(v);
		setPosts(p);		
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
