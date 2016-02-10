package bg.uni_sofia.fmi.corejava.stuff;

public class Message {
	User author;
	String content;
	
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Message(User author, String content) {
		this.author = author;
		this.content = content;
	}
	
}
