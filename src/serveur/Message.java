package serveur;

import java.util.Date;

/**
 * 
 * @author Xavier
 *
 */
public class Message {

	protected int id;
	protected String contenu;
	protected String date;
	protected String username;

	
	public Message(int id, String contenu, String date, String username) {
		this.id = id;
		this.contenu = contenu;
		this.date = date;
		this.username = username;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
}
