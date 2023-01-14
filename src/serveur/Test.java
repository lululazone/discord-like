package serveur;



public class Test {

	public static void main(String[] args) {
		Utilisateur u = new Utilisateur("Grof", "Xavielo", "Bruno", Categorie.ELEVE);
		Database d = new Database();
		
		System.out.println(d.addUser(u, "1234"));
		System.out.println(d.removeUser("ludb"));
		System.out.println(d.modifyPswd(u.getUsername(), "yoyo"));
		System.out.println(d.getAllUser());
		System.out.println(d.getUser("Pierro").getName());
		System.out.println((d.verifyUserPswd("Grof", "yoyo")));
		System.out.println(d.addGroupe("FanClub Lokoise"));
		//System.out.println(d.removeGroupe("Eleve"));
		System.out.println(d.addAppartenir("Grof", "FanClub Lokoise"));
		System.out.println("test");
		System.out.println(d.getUserFromGroupe("Eleve"));
		System.out.println(d.addFilDiscussion("test", "Grof", "Eleve", "YOYOYO"));
		System.out.println(d.addFilDiscussion("test2", "Grof", "Eleve", "le gros test là"));
		System.out.println(d.addFilDiscussion("test3", "Pierro", "Eleve", "mdr"));
		System.out.println(d.getDiscussionFromUser("Grof"));
		d.deconnexion();
	}

}

