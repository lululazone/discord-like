package serveur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Client extends Thread {
	
	private ServerSocket ss;
	private Database database;
	PrintWriter pr;
	InputStreamReader in;
	BufferedReader bf;
	
	public Client(ServerSocket ss, Database database) {
		this.ss = ss;
		this.database = database;
	}
	
	public void lectureLogin (String[] commande) {
		if (database.verifyUserPswd(commande[1],commande[2])) {
			pr.println("approved");
			pr.flush();
		} else {
			pr.println("pas approved");
			pr.flush();
		}
	}
	
	public void lecturePrenom (String[] commande) {
		pr.println(database.getUser(commande[1]).getFirstName());
		pr.flush();
	}
	
	public void lectureNom (String[] commande) {
		pr.println(database.getUser(commande[1]).getName());
		pr.flush();
	}
	
	public void lectureType (String[] commande) {
		pr.println(database.getUser(commande[1]).getCategorie().toString());
		pr.flush();
	}
	
	public void lectureListeGroupeUser (String[] commande) {
		String msg = "";
		List<String> lGroupe = database.getGroupeFromUser(commande[1]);
		System.out.println(lGroupe);
		for (String groupe : lGroupe) {
			msg += groupe+" ";
		}
		System.out.println(msg);
		pr.println(msg);
		pr.flush();
	}
	
	public void lectureListeMessages (String[] commande) throws IOException {
		List<Message> lMessage = database.getMessageFromDiscussion(Integer.parseInt(commande[1]));
		for (Message msg: lMessage) {
			Utilisateur user = database.getUser(msg.getUsername());
			
			database.updateStatus(msg.getId(), commande[2]);
			String etat;
			if (database.getStatus(msg.getId()) == 0) {
				etat = "RECUGROUPE";
			} else if (database.getStatus(msg.getId()) == 1)  {
				etat = "LUGROUPE";
			} else {
				etat = "ATTENTE";
			}
			System.out.println("demande en cours");
			System.out.println(msg.getContenu().toString());
			pr.println(msg.getContenu()+" --- "+user.getName()+" "+user.getFirstName()+" "+etat+" "+msg.getDate()+" "+commande[1]+" "+msg.getId());
			pr.flush();
			bf.readLine();
		}
		pr.println("fn");
		pr.flush();
		bf.readLine();
		System.out.println("eheheheheheheheheheheheheheheheheheheheeheheheh");
		
	}
	
	public void ecritureMessage (String[] commande) {
		String msg = "";
		
		int i = 1;
		while (!commande[i].equals("---")) {
			msg += commande[i]+" ";
			i++;
			
		}
		i++;
		System.out.println("hola");
		pr.println("messagerecu");
		database.addMessage(msg,commande[i+1], Integer.parseInt(commande[i+2]));
		
		
		pr.flush();
	}
	
	public void lectureIdFils (String[] commande) {
		String msg = "";
		List<Discussion> lFils = database.getDiscussionFromUser(commande[1]);
		for (Discussion d : lFils) {
			msg += d.getId() +" ";
		}
		System.out.println(msg);
		pr.println(msg);
		pr.flush();
	}
	
	public void lectureTitreFils (String[] commande) {
		String msg = "";
		List<Discussion> lFils = database.getDiscussionFromUser(commande[1]);
		System.out.println(lFils);
		for (Discussion d : lFils) {
			msg += d.getTitre() + " ";
		}
		System.out.println(msg);
		pr.println(msg);
		pr.flush();
	}
	
	public void ecritureNouveauFil (String[] commande) {
		String msg = "";
		
		int i = 1;
		while (!commande[i].equals("---")) {
			msg += commande[i]+" ";
			i++;
			
		}
		i++;
		pr.println("messagerecu");
		database.addFilDiscussion(commande[i+1], commande[i], commande[i+2], msg);
		
		pr.flush();
	}
	
	
	
	@Override
	public void run() {
		try {
			Socket s = ss.accept();
			
			Client client = new Client(ss,database);
	    	client.start();
	    	
	    	pr = new PrintWriter(s.getOutputStream());
			in = new InputStreamReader(s.getInputStream());
			bf = new BufferedReader(in);

			String commandeBrut = bf.readLine();
			String commande[] = commandeBrut.split(" ");

			while (!commande[0].equals("deconnection")) {
				System.out.println(commandeBrut);
				
				if (commande[0].equals("login")) {
					lectureLogin(commande);
				}
				if (commande[0].equals("Prenom")) {
					lecturePrenom(commande);
				}
				if (commande[0].equals("Nom")) {
					lectureNom(commande);
				}
				if (commande[0].equals("Type")) {
					lectureType(commande);
				}
				if (commande[0].equals("lGroupe")) {
					lectureListeGroupeUser(commande);
				}
				if (commande[0].equals("listeMessage")) {
					lectureListeMessages(commande);
				}
				if (commande[0].equals("Message")) {
					ecritureMessage(commande);
				}
				if (commande[0].equals("IdFil")) {
					lectureIdFils(commande);
				}
				if (commande[0].equals("TitreFil")) {
					lectureTitreFils(commande);
				}
				if (commande[0].equals("CreerFil")) {
					ecritureNouveauFil(commande);
				}
				
				commandeBrut = bf.readLine();
				commande = commandeBrut.split(" ");
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	
	}
}
