package serveur;

/**
 * 
 * @author Xavier
 *
 */
public class Discussion {

	protected int id;
	protected String titre;
	protected String username;
	protected String nomGroupe;
	
	
	public Discussion(int id, String titre, String username, String nomGroupe) {
		this.id = id;
		this.titre = titre;
		this.username = username;
		this.nomGroupe = nomGroupe;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getTitre() {
		return titre;
	}


	public void setTitre(String titre) {
		this.titre = titre;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getNomGroupe() {
		return nomGroupe;
	}


	public void setNomGroupe(String nomGroupe) {
		this.nomGroupe = nomGroupe;
	}
	
	
	
}
