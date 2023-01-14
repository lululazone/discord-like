package serveur;

/**
 * 
 * @author Xavier
 *
 */
public class Utilisateur {
	public String username;
	public String name;
	public String firstName;
	public Categorie categorie;
	
	public Utilisateur(String username, String name, String firstName, Categorie categorie) {
		this.username = username;
		this.name = name;
		this.firstName = firstName;
		this.categorie = categorie;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public String getFirstName() {
		return firstName;
	}
	
	
	
	
}
