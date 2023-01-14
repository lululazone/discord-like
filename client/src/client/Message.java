package client;
import java.time.LocalDate;

public class Message {
	public String texte;
	public String nom;
	public String prenom;
	public String tm;
	public String date;
	public String file;
	public String id;
	public String getFile() {
		return file;
	}
	public Message(String texte, String nom,String prenom, String tm,String date,String file,String id) {
		super();
		this.texte = texte;
		this.nom = nom;
		this.prenom = prenom;
		this.tm = tm;
		this.date = date;
		this.file = file;
		this.id = id;
	}
	public String getPrenom() {
		return prenom;
	}
	public String getTexte() {
		return texte;
	}
	public void setTm(String tm) {
		this.tm = tm;
	}
	public String getId() {
		return id;
	}
	public String getNom() {
		return nom;
	}

	public String getTm() {
		return tm;
	}
	public String getDate() {
		return date;
	}
}
	

