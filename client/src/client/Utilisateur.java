package client;

import java.util.ArrayList;
import java.util.List;

public class Utilisateur {
	String Nom;
	String Prenom;
	String id;
	String Tu;
	List<String> Groupe = new ArrayList<>();
	public Utilisateur(String nom, String prenom, String id, String tu, List<String> groupe) {
		this.Nom = nom;
		this.Prenom = prenom;
		this.id = id;
		this.Tu = tu;
		this.Groupe = groupe;
	}
	public String getNom() {
		return Nom;
	}
	public String getPrenom() {
		return Prenom;
	}
	public String getId() {
		return id;
	}
	public String getTu() {
		return Tu;
	}
	public List<String> getGroupe() {
		return Groupe;
	}
	

}
