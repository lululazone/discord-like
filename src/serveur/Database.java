package serveur;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;



/**
 * 
 * @author Xavier
 *
 */
public class Database {
    private final String url;
    private final String username;
    private final String pswd;
    private Connection con;
    private Statement state;


    /**
     * Se connecter à  la base de donnée
     * @throws ClassNotFoundException 
     */
    public Database() {
    	try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			System.exit(-1);
		}
        this.url = "jdbc:mysql://localhost:3306/neocampus?zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=UTC";
        this.username = "root";
        this.pswd = "";
        try {
			this.con = DriverManager.getConnection(url, username, pswd);
			this.state = con.createStatement();
		} catch (SQLException e2) {
			e2.printStackTrace();
			System.exit(-1);
		}
    }
    
    //Gestion des utilisateurs
    

    /**
     * Ajout d'un utilisateur à la bdd.
     * 
     * @param user
     * @return 1 si l'ajout d'un utilisateur c'est bien passe, sinon 0;
     */
    public int addUser(Utilisateur user, String pswd) {
    	String userGrp = "";
    	switch (user.getCategorie()) {
	    	case ELEVE:
	    		userGrp = "'Eleve'";
	    		break;
	    	case ENSEIGNANT:
	    		userGrp = "'Enseignant'";
	    		break;
	    	case ADMINISTRATION:
	    		userGrp = "'Administration'";
	    		break;
	    	case ENTRETIENT:
	    		userGrp = "'Entretient'";
	    		break;
	    	case SECURITE:
	    		userGrp = "'Securite'";
    	}
    	
    	String valuesUser = "'"+ user.getUsername() +"', '"+ pswd+"', '"+user.getName()+"', '"+user.getFirstName()+"', '"+user.getCategorie()+"'";
    	String valuesAppartenir = "'"+user.getUsername()+"', "+userGrp+"";
    	
    	String requestUser = "INSERT INTO utilisateur(Id_Utilisateur, MotDePasse, Nom, Prenom, Type)  VALUES ("+valuesUser+");";
    	String requestAppartenir = "INSERT INTO appartenir(Id_Utilisateur, Nom_Groupe) VALUES ("+valuesAppartenir+");";
        int result = 0;
        try {
            state.executeUpdate(requestUser);
            result = state.executeUpdate(requestAppartenir);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        addStatusWithoutUpdate(user.getUsername());
        return result;
    }
    
    
    /**
     * S
     * 
     * @param username
     * @param pswd
     * @return true si le mdp et username sont correct, false sinon.
     */
    public boolean verifyUserPswd(String username, String pswd) {
    	String requestUser = "SELECT Id_Utilisateur, Nom, Prenom, Type FROM UTILISATEUR WHERE Id_Utilisateur = '"+username+"' AND MotDePasse = '"+pswd+"'";
    	try {
			ResultSet result = state.executeQuery(requestUser);
			if (result.next()) {
				return true;
			}
		} catch (java.sql.SQLException e) {
			return false;
		}
    	return false;
    }
    
    
    
    /**
     * Supprime un utilisateur de la bdd.
     * 
     * @param user Utilisateur a supprimer.
     * @return 1 si succès, 0 sinon.
     */
    public int removeUser(Utilisateur user) {
        String requestUser = "DELETE FROM UTILISATEUR WHERE Id_Utilisateur = '"+user.getUsername()+"'";
        int result;
        try {
            result = state.executeUpdate(requestUser);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            result = 0;
        }
        removeAppartenir(user);
        return result;
    }
    
    /**
     * Supprime un utilisateur de la bdd.
     * 
     * @param username Utilisateur a supprimer.
     * @return 1 si succes, 0 sinon.
     */
    public int removeUser(String username) {
        String requestUser = "DELETE FROM UTILISATEUR WHERE Id_Utilisateur = '"+username+"'";
        int result;
        try {
            result = state.executeUpdate(requestUser);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            result = 0;
        }
        removeAppartenir(new Utilisateur(username, null, null, null));
        return result;
    }
    
    
    /**
     * Supprime un utilisateur de tout ses groupes
     * 
     * @param user
     * @return (nombre de groupe) >0 si succès,0 sinon.
     */
    public int removeAppartenir(Utilisateur user) {
    	String requestAppartenir = "DELETE FROM APPARTENIR WHERE Id_Utilisateur = '"+user.getName()+"'";
    	int result;
        try {
            result = state.executeUpdate(requestAppartenir);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }
    
    /**
     *  Modifie le mot de passe d'un utilisateur.
     * 
     * @param username
     * @param pswd
     * @return 1 si succes, 0 sinon.
     */
    public int modifyPswd(String username, String pswd) {
    	String requestUser = "UPDATE UTILISATEUR  SET MotDePasse = '"+pswd+"' WHERE Id_Utilisateur = '"+username+"'";
    	int result;
    	try {
            result = state.executeUpdate(requestUser);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }
    
    /**
     *  Renvoie la liste de tout les utilisateurs sur la bdd.
     * 
     * @return liste d'user si succes, null sinon.
     */
    public List<Utilisateur> getAllUser() {
    	List<Utilisateur> allUser = new ArrayList<>();
    	String requestUser = "SELECT Id_Utilisateur, Nom, Prenom, Type FROM UTILISATEUR";
    	try {
    		ResultSet user = state.executeQuery(requestUser);
    		while (user.next()) {
    			allUser.add(new Utilisateur(user.getString(1), user.getString(2), user.getString(3), Categorie.valueOf(user.getString(4))));
    		}
    	} catch (java.sql.SQLException e) {
    		e.printStackTrace();
    		allUser = null;
    	}
    	return allUser;
    }
    
    
    /**
     * Renvoie un utilisateur de la bdd par rapport à son username.
     * 
     * @param username
     * @return Utilisateur si succes, null sinon.
     */
    public Utilisateur getUser(String username) {
    	Utilisateur user = null;
    	String requestUser = "SELECT Id_Utilisateur, Nom, Prenom, Type FROM UTILISATEUR WHERE Id_Utilisateur = '"+username+"'";
    	try {
			ResultSet result = state.executeQuery(requestUser);
			if (result.next()) {
				user = new Utilisateur (result.getString(1), result.getString(2), result.getString(3), Categorie.valueOf(result.getString(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return user;
    }
    
    
    
    
    
    //**************************************************************************************************
    //Gestion des groupes
    
    /**
     * Ajoute un groupe a la bdd.
     * 
     * @param nomGroupe
     * @return 1 si succes, 0 sinon.
     */
    public int addGroupe(String nomGroupe) {
    	String requestGroupe = "INSERT INTO GROUPE VALUES ('"+nomGroupe+"')";
    	int result=0;
    	try {
			result = state.executeUpdate(requestGroupe);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return result;
    }
    
    /**
     * Supprime un groupe de la bdd.
     * 
     * @param nomGroupe
     * @return 1 si succès, 0 sinon.
     */
    public int removeGroupe(String nomGroupe) {
        String requestUser = "DELETE FROM GROUPE WHERE Nom_Groupe = '"+nomGroupe+"'";
        int result;
        try {
            result = state.executeUpdate(requestUser);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            result = 0;
        }
        removeAppartenir(nomGroupe);
        return result;
    }
    
    
    /**
     * Supprime le lien entre le groupe et un utilisateur supprimé.
     * 
     * @param nomGroupe
     * @return (nombre d'utilisateur lie au groupe) >0 si succes, 0 sinon.
     */
    public int removeAppartenir(String nomGroupe) {
    	String requestAppartenir = "DELETE FROM APPARTENIR WHERE Nom_Groupe = '"+nomGroupe+"'";
    	int result;
        try {
            result = state.executeUpdate(requestAppartenir);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            result = 0;
        }
        return result;
    }
    
    
    /**
     * Permet de récupérer la liste des utilisateurs d'un groupe.
     * 
     * @param nomGroupe
     * @return liste d'utilisateur si succès, null si echec.
     */
    public List<Utilisateur> getUserFromGroupe(String nomGroupe) {
    	List<Utilisateur> userList = new ArrayList<>();
    	List<String> usernameList = new ArrayList<>();
    	String requestAppartenir = "SELECT Id_Utilisateur FROM APPARTENIR WHERE Nom_Groupe = '"+nomGroupe+"'";
    	try {
			ResultSet result = state.executeQuery(requestAppartenir);
			while (result.next()) {
				usernameList.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	for (String username : usernameList) {
    		userList.add(getUser(username));
    	}
    	return userList;
    }
    
    
    
    /**
     * Permet de récupérer la liste des groupes dont un utilisateur fait partie.
     * 
     * @param username
     * @return la liste des groupes si succès, null sinon.
     */
    public List<String> getGroupeFromUser(String username) {
    	List<String> nomGroupeList = new ArrayList<>();
    	String requestAppartenir = "SELECT Nom_Groupe FROM APPARTENIR WHERE Id_Utilisateur = '"+username+"'";
    	try {
			ResultSet result = state.executeQuery(requestAppartenir);
			while (result.next()) {
				nomGroupeList.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	return nomGroupeList;
    }
    
    /**
     * Permet d'ajouter un user à un groupe.
     * 
     * @param username
     * @param nomGroupe
     * @return 1 si succès, 0 sinon.
     */
    public int addAppartenir(String username, String nomGroupe) {
    	String requestAppartenir = "INSERT INTO Appartenir(Id_Utilisateur, Nom_Groupe) VALUES ('"+username+"', '"+nomGroupe+"');";
    	int result;
    	try {
			result = state.executeUpdate(requestAppartenir);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
    	addStatusWithoutUpdate(username);
    	return result;
    }
    
    
    /**
     * Permet de récupérer la liste de tout les groupes de la bdd.
     * 
     * @return la liste des noms de groupes, null sinon.
     */
    public List<String> getAllGroupe() {
    	List<String> allGroupe = new ArrayList<>();
    	String requestGroupe = "SELECT Nom_Groupe FROM Groupe";
    	try {
    		ResultSet result = state.executeQuery(requestGroupe);
    		while (result.next()) {
    			allGroupe.add(result.getString(1));
    		}
    	} catch (java.sql.SQLException e) {
    		e.printStackTrace();
    		allGroupe = null;
    	}
    	return allGroupe;
    }
    
    
    //**************************************************************************************************
    //Gestion des fils de discussion
    
    
    /**
     * Permet de récupérer le numéro du dernier fil de discussion créé.
     * 
     * @return le numéro.
     */
    public int numDiscussion() {
    	int num = 0;
    	String requestFil = "SELECT Id_Fil_de_discussion FROM Fil_de_discussion ORDER BY Id_Fil_de_discussion DESC";
    	try {
			ResultSet result = state.executeQuery(requestFil);
			if (result.next()) {
				num = result.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return num;
    }
    
    /**
     * Permet d'ajouter un fil de discussion.
     * 
     * @param titre
     * @param username
     * @param nom_groupe
     * @param premierMess
     * @return 1 si succès, 0 sinon.
     */
    public int addFilDiscussion(String titre, String username, String nom_groupe, String premierMess) {
    	String requestFil = "INSERT INTO Fil_de_discussion(Titre, Id_Utilisateur, Nom_Groupe) VALUES ('"+titre+"', '"+username+"', '"+nom_groupe+"')";
    	try {
			state.executeUpdate(requestFil);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
    	int idFil = numDiscussion();
    	System.out.println(idFil + "numero discussion");
    	int result = addMessage(premierMess, username, idFil);
    	return result;
    }
    
    
    /**
     * Permet de récupérer tout les fils de discussion d'un utilisateur
     * 
     * @param username Nom de l'utilisateur
     * @return la liste des discussion si succès, null si erreur.
     */
    public List<Discussion> getDiscussionFromUser(String username) {
    	List<String> listGroupe = getGroupeFromUser(username);
    	List<Discussion> listDiscussion = new ArrayList<>();
    	// Récupération des fils créés par l'utilisateur.
    	String requestFil = "SELECT Id_Fil_de_discussion, Titre, Nom_Groupe FROM Fil_de_discussion WHERE Id_Utilisateur = '"+username+"'";
    	try {
			ResultSet result = state.executeQuery(requestFil);
			while (result.next()) {
				listDiscussion.add(new Discussion(result.getInt(1), result.getString(2), username, result.getString(3)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	for (String nomGroupe: listGroupe) {
    		// Récupération des fils du groupe d'un utilisateur.
    		requestFil = "SELECT Id_Fil_de_discussion, Titre FROM Fil_de_discussion WHERE Id_Utilisateur != '"+username+"' AND Nom_Groupe = '"+nomGroupe+"'";
    		try {
				ResultSet result = state.executeQuery(requestFil);
				while (result.next()) {
					listDiscussion.add(new Discussion(result.getInt(1), result.getString(2), username, nomGroupe));
				}
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
    	}
    	return listDiscussion;
    }
    
    
    
    /**
     * Permet de récupérer le nom du groupe associé à un fil de discussion.
     * 
     * @param id_fil
     * @return le nom du groupe si succès, null sinon.
     */
    public String getGroupeFromDiscussion(int id_fil) {
    	String nomGroupe = null;
    	String requestFil = "SELECT Nom_Groupe FROM Fil_de_discussion WHERE Id_Fil_de_discussion = '"+id_fil+"'";
    	try {
			ResultSet result = state.executeQuery(requestFil);
			while (result.next()) {
				nomGroupe = result.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return nomGroupe;
    }
    
    
    /**
     * Permet de récupérer la liste des usernames d'un fil de disucssion (créateur du fil et groupe associé).
     * 
     * @param id_fil
     * @return la liste des usernames d'une discussion si succès, 0 sinon.
     */
    public List<String> getUsernameFromDiscussion(int id_fil) {
    	List<String> usernameList = new ArrayList<>();
    	String username=null;
    	String requestFil = "SELECT Id_Utilisateur FROM Fil_de_discussion WHERE Id_Fil_de_discussion = '"+id_fil+"'";
    	try {
			ResultSet result = state.executeQuery(requestFil);
			while (result.next()) {
				username = result.getString(1);
				usernameList.add(username);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	String nomGroupe = getGroupeFromDiscussion(id_fil);
    	String requestAppartenir = "SELECT Id_Utilisateur FROM APPARTENIR WHERE Nom_Groupe = '"+nomGroupe+"' AND Id_Utilisateur !='"+username+"'";
    	try {
			ResultSet result = state.executeQuery(requestAppartenir);
			while (result.next()) {
				usernameList.add(result.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	return usernameList;
    }
    
    
    
    //**************************************************************************************************
    //Gestion des messages
    
    /**
     * Permet de récupérer le numéro du dernier message.
     * 
     * @return le numéro du dernier message ou 0.
     */
    public int numMessage() {
    	int num = 0;
    	String requestMess = "SELECT Id_Message FROM Message ORDER BY Id_Message DESC";
    	try {
			ResultSet result = state.executeQuery(requestMess);
			if (result.next()) {
				num = result.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return num;
    }
    
    
    /**
     * Permet d'ajouter un message à un fil de discussion.
     * 
     * @param contenuMessage
     * @param username
     * @param id_fil
     * @return 1 si succès, 0 sinon.
     */
    public int addMessage(String contenuMessage, String username, int id_fil) {
    	String requestMess = "INSERT INTO Message(Contenu, Date, Id_Fil_de_discussion, Id_Utilisateur) VALUES('"+contenuMessage+"', NOW(), '"+id_fil+"', '"+username+"')";
    	int result;
    	try {
			result = state.executeUpdate(requestMess);
		} catch (SQLException e) {
			e.printStackTrace();
			return result = 0;
		}
    	int idMess = numMessage();
    	addStatus(idMess, id_fil, username);
    	return result;
    }
    
    
    /**
     * Permet de récupérer la liste des messages triées par leur ordre d'apparition.
     * @param id_fil
     * @return la liste des messages si succès, 0 sinon.
     */
    public List<Message> getMessageFromDiscussion(int id_fil) {
    	List<Message> listMess = new ArrayList<>();
    	String requestMess = "SELECT Id_Message, Contenu, Date, Id_Utilisateur FROM Message WHERE Id_Fil_de_discussion = '"+id_fil+"' ORDER BY Id_Message DESC";
    	try {
			ResultSet result = state.executeQuery(requestMess);
			while (result.next()) {
				listMess.add(new Message(result.getInt(1), result.getString(2), result.getString(3), result.getString(4)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
    	return listMess;
    }
    
    
    /**
     * Permet de récupérer le numéro de discussion d'un message;
     * 
     * @param idMess
     * @return num discussion si succès, 0 sinon.
     */
    public int getDiscussionFromMessage(int idMess) {
    	String requestMess = "SELECT Id_Fil_de_discussion FROM Message WHERE Id_Message = '"+idMess+"'";
    	int idFil=0;
    	try {
			ResultSet result = state.executeQuery(requestMess);
			while (result.next()) {
				idFil = result.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			idFil = 0;
		}
    	return idFil;
    }
    
    
    //**************************************************************************************************
    //Gestion des status
    
    /**
     * Permet d'ajouter le status d'un message pour tout les utilisateurs et update le status du createur du message.
     * 
     * @param idMess
     * @param id_fil
     * @param Username
     * @return n>0 si succès, 0 sinon.
     */
    public int addStatus(int idMess, int id_fil, String usernameCreator) {
    	List<String> usernameList = getUsernameFromDiscussion(id_fil);
    	int result=0;
    	for (String username: usernameList) {
			String requestStatus = "INSERT IGNORE INTO Status_message(Id_Message, Id_Utilisateur, Status) VALUES('"+idMess+"', '"+username+"', '"+0+"') ";
    		try {
				result = state.executeUpdate(requestStatus);
			} catch (SQLException e) {
				e.printStackTrace();
				return 0;
			}
    	}
    	updateStatus(idMess, usernameCreator);
    	return result;
    }
    
    
    /**
     * Permet d'ajouter le status d'un message pour un Utilisateur
     * 
     * @param idMess
     * @param id_fil
     * @return le nombre de status rajouter, 99 si erreure.
     */
    public int addStatusWithoutUpdate(String username) {
    	List<Discussion> listDiscussion = getDiscussionFromUser(username);
    	int result=0;
    	for (Discussion discussion: listDiscussion) {
    		List<Message> listMess = getMessageFromDiscussion(discussion.getId());
    		for (Message message: listMess) {
    			String requestStatus = "INSERT IGNORE INTO Status_message(Id_Message, Id_Utilisateur, Status) VALUES('"+message.getId()+"', '"+username+"', '"+0+"')";
    			try {
					result = state.executeUpdate(requestStatus);
				} catch (SQLException e) {
					e.printStackTrace();
					return 99;
				}
    		}
    	}
    	return result;
    }
    
    
    /**
     * Permet de mettre a jour le status d'un message pour un utilisateur.
     * 
     * @param idMess
     * @param username
     * @return 1 si succès, 0 sinon.
     */
    public int updateStatus(int idMess, String username) {
    	String requestStatus = "UPDATE Status_message  SET Status = '"+1+"' WHERE Id_Utilisateur = '"+username+"'";
    	int result;
    	try {
			result = state.executeUpdate(requestStatus);
		} catch (SQLException e) {
			result = 0;
			e.printStackTrace();
		}
    	return result;
    }
    
    
    /**
     * Permet de récupérer le status global (de tout les utilisateurs) d'un message.
     * 
     * @param idMess
     * @return 0 ou 1 en fonction du resultat (succes).
     */
    public int getStatus(int idMess) {
    	String requestStatus = "SELECT Status FROM Status_message WHERE Id_Message = '"+idMess+"'";
    	try {
			ResultSet result = state.executeQuery(requestStatus);
			while (result.next()) {
				if (result.getInt(1) == 0) {
					return 0;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return 1;
    }
    
    
    //**************************************************************************************************
    public void deconnexion() {
    	try {
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
