package client;

import java.net.Socket;
import java.time.LocalDate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.awt.event.*;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class InterfaceDiscussion extends JFrame {
	
	public JList b;
	Socket sock;
	Utilisateur u;
	Timer timer = new Timer();
	public String[] idFil;
	public String nomFile[];
	JTextField ecrireMessage=new JTextField(20);
	JTextPane ListMessage = new JTextPane();
	Document doc = ListMessage.getStyledDocument(); 
	SimpleAttributeSet attributeSet = new SimpleAttributeSet();
	JButton btn1 = new JButton("OK");  
    JButton btnAjouter = new JButton("+");  
    JButton btnDeco = new JButton("Quit");
    DefaultListModel<String> dlm = new DefaultListModel<String>();
    Request r;
    public boolean notDeco = true;
	
	public void creerInterface() {
		

		
		btnDeco.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
            	notDeco = false;
                ConnectionEnd();
                dispose();
            }
        });
		btnAjouter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				AjouterFil af = new AjouterFil(sock,u,r);
				
			}

		});
		btn1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					if(!b.isSelectionEmpty())
						EnvoyerMessage(ecrireMessage.getText());
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

		});

		JPanel buttonPan = new JPanel();
		String filDisc[];
		
		b = new JList<>(dlm);
		b.setBounds(10,0,100,150);
		timer.schedule(new TimerTask() { // demande faite chaque seconde pour actualiser la liste des files et demander la liste des messages ...
			  @Override
			  public void run() {
				  try {
					if(ecrireMessage.getText().equals(""))
						synchronisation();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  }
		
		}, 0, 1000);
		ecrireMessage.setBackground(Color.cyan);
		ecrireMessage.setBounds(80,270,200,50);
		ListMessage.setBounds(80,0,270,270);
		//Créer le JPanel
	    JPanel panel = new JPanel();
	      //Spécifier la position et la taille du JPanel
	    panel.setBounds(0,0,80,220);
	    buttonPan.setBounds(300,300,50,30);
	    
	      //Spécifier la couleur d'arrière-plan du JPanel
	    panel.setBackground(Color.lightGray);
	      //Créer le bouton 1
	    
	    btnAjouter.setBounds(0,220, 80, 50);
	    btnDeco.setBounds(0,270,80,50);
	    buttonPan.add(btn1);
	    btn1.setBounds(270,270,70,50);
	      //Spécifier la position et la taille du bouton    
	      //Spécifier la couleur d'arrière-plan du bouton
	    btn1.setBackground(Color.WHITE); 
	      //Créer le bouton 2
	    JButton btn2 = new JButton("Bouton 2"); 
	    btn2.setBounds(100,100,80,30);  
	    btn2.setBackground(Color.RED); 
	      //Ajouter les deux boutons au JPanel
	    panel.add(b); 
	      //Ajouter le JPanel au JFrame
	    this.add(panel);
	    this.add(ListMessage);
	    this.add(ecrireMessage);
	    this.add(btn1);
	    this.add(btnDeco);
	    this.setSize(350,350);  
	    this.add(btnAjouter);
	    this.setLayout(null);  
	    this.setVisible(true);  
		 
	}

	public void synchronisation() throws InterruptedException {
		nomFile = demandeListFil(sock);
		updateListeFile(nomFile);
		System.out.println(nomFile[0]+" =  nomFile" );
		idFil = demandeidFil(sock);
		System.out.println(idFil[0]+" =  idFile " );
		
	    if(!(b.isSelectionEmpty())){
	    		ListMessage.setText(" ");
	    	
	    	
	    		try {
					afficherMessage(updateMessage(b.getSelectedValue()));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
		
	}

	private String[] demandeidFil(Socket sock2) {
		r.emission("IdFil "+u.id);
		String idf = r.reception();
		String[] idfl = idf.split(" ");
		return idfl;
		
	}

	protected void afficherMessage(List<Message> updateMessage) throws BadLocationException {
		ListMessage.setText("");
		System.out.println("demande en cours...");
		Collections.reverse(updateMessage);
		for(Message m : updateMessage) {
			
			SimpleAttributeSet attributeSet = checkType(m);
			doc.insertString(doc.getLength(),"-"+m.getPrenom()+" "+m.getNom()+" : "+m.getTexte()+" "+"("+m.getDate()+" "+m.getFile()+")"+"\n",attributeSet);
		}
		
		
	}

	private SimpleAttributeSet checkType(Message m) {
		attributeSet = new SimpleAttributeSet();
		if(m.getTm().equals("ATTENTE")) {
			StyleConstants.setForeground(attributeSet, Color.red);  
			return attributeSet;
		}
		if(m.getTm().equals("RECUGROUPE")) {
			StyleConstants.setForeground(attributeSet, Color.orange);  
			return attributeSet;
		}
		if(m.getTm().equals("LUGROUPE")) {
			StyleConstants.setForeground(attributeSet, Color.green);  
			return attributeSet;
		}
		return attributeSet;
		
	}

	public List<Message> updateMessage(Object selectedValue) {
		List<Message> mList = new ArrayList<>();
		String recept = "";
		r.emission("listeMessage "+trouverIdFile(selectedValue.toString())+" "+u.id); 
		recept = r.reception();//le selectedvalue c'est le fil selectionné
		r.emission("mrecu");
		while(!recept.equals("fn")){
				System.out.println(recept);
				mList.add(traiterMessage(recept));
				recept = r.reception();
				r.emission("mrecu");
			
		}
		System.out.println("finparcours");
		return mList;
	}

	private Message traiterMessage(String reception) {
		String[] infoM = reception.split(" ");
		System.out.println("texte"+infoM[0].toString());
		System.out.println("nom"+infoM[3].toString());
		System.out.println("prenom"+infoM[4].toString());
		System.out.println("tm"+infoM[5].toString());
		System.out.println("date"+infoM[6].toString());
		System.out.println("fil"+infoM[8].toString());
		System.out.println("id"+infoM[9].toString());
		String msg = "";
		int i = 0;
		while (!infoM[i].equals("---")) {
			msg += infoM[i]+" ";
			i++;
			
		}
		
		Message m = new Message(msg,infoM[i+1],infoM[i+2],infoM[i+3],infoM[i+4],infoM[i+6],infoM[i+7]); 
		//Texte+nom+prenom+type+date+file+id
		return m;
	}



	public void updateListeFile(String[] demandeListFil) {
		//String test[] = {"hej","flocka","ikea","ingka"};
		for(String fil : demandeListFil) {
			if(!dlm.contains(fil))
				dlm.addElement(fil);
		}
		
	}

	public String[] demandeListFil(Socket sock2) {
		r.emission("TitreFil "+u.id);
		String listeFil = r.reception();
		String[] listefinale =listeFil.split(" ");
		return listefinale;
		
	}
	

	public void EnvoyerMessage(String text) throws BadLocationException {

		
		LocalDate date = LocalDate.now();
		String id = trouverIdFile(b.getSelectedValue().toString());
		Message m = new Message(text,u.getNom(),u.getPrenom(),"ATTENTE",date.toString(),id.toString(),"");
		SimpleAttributeSet attributeSet = checkType(m);
		doc.insertString(doc.getLength(), "-"+m.getPrenom()+" "+m.getNom()+" : "+m.getTexte()+" "+"("+m.getDate()+" "+m.getFile()+")"+"\n", attributeSet);
		r.emission("Message " + m.getTexte()+" --- "+" "+u.id+" "+m.getFile());
		if(r.reception().equals("messagerecu")) { //Confirmation de reception attendue par le serveur

			m.setTm("RECUSERV");
			checkType(m);
		}
		ecrireMessage.setText("");
	
	}

	public String trouverIdFile(String selectedValue) {
		int i =0;
		for(String mot : nomFile) {
			if(mot.equals(selectedValue)) {
				return idFil[i];
			}
			i++;
		}
		
		return null;
	}

	public InterfaceDiscussion(Socket sock,Utilisateur u ,Request r) {
		this.sock = sock;
		this.u = u;
		this.setTitle("DiscUT3 - Discussion - "+u.getId());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.r= r;
		creerInterface();
	}
	
	public boolean ConnectionEnd() {
		this.setVisible(false);
		if(notDeco) {
			return true;
		}
		return false;
		
	}
	
	public class AjouterFil extends JFrame {
		/**
		 * 
		 */
		
		JTextField TitreFil=new JTextField(20);
		JTextField premierMessage=new JTextField(20);
		JComboBox ChoixGroupe; 
		String[] listeGroupe;
		private static final long serialVersionUID = 1L;
		JLabel sTitre = new JLabel("Saisir un titre");
		JLabel sMess = new JLabel("Saisir un message");
		JLabel sGroupe = new JLabel("Choisir un groupe");
		JButton Valider = new JButton("Valider");
		Socket sock;
		Utilisateur u;
		Request r = new Request(sock);
		JPanel panelFil = new JPanel();
		public AjouterFil(Socket sock, Utilisateur u,Request r) {
			this.sock = sock;
			this.u = u;
			this.r = r;
			this.setTitle("Création Fil");
			creerInterfaceFil();
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		}
		//creation de la petite fenetre pour creer un fil de discussion
		public void creerInterfaceFil(){
            ChoixGroupe = new JComboBox(recevoirListeGroupe());
            panelFil.setBounds(0,0,200,200);
            panelFil.add(sTitre);
            panelFil.add(TitreFil);
            panelFil.add(sMess);
            panelFil.add(premierMessage);
            panelFil.add(sGroupe);
            panelFil.add(ChoixGroupe);
			panelFil.add(Valider);
			this.add(panelFil);
			TitreFil.setBounds(300,100 ,150 , 20);
			premierMessage.setBounds(300,200,150,20);
			ChoixGroupe.setBounds(300,400,150,50);
			this.setBounds(0,0,500,500);
			this.setVisible(true);
            Valider.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent ae) {
                    Message m = new Message(premierMessage.getText(),u.getNom(),u.getPrenom(),"ATTENTE" ,"",TitreFil.getText()," " );
                    if(!(TitreFil.getText().isEmpty()) && !(premierMessage.getText().isEmpty())) {
                        r.emission("CreerFil "+premierMessage.getText()+" --- "+u.id+" "+TitreFil.getText()+" "+ChoixGroupe.getSelectedItem().toString());
                        
                        if(r.reception().equals("messagerecu")) {// requete a recevoir
                            m.tm = "RECUSERV";
                        }
                        //Envoi requête création Fil au serveur....
                        //Envoie requete d'envoie message au serveur...
                        dispose();
                    }
                    else {
                         JOptionPane.showMessageDialog(null, "Veuillez remplir tout les champs", "Erreur remplissage champs", JOptionPane.WARNING_MESSAGE);
                    }
                    
                    
                }

			});
			
		}
		
		
		
		public String[] recevoirListeGroupe() {
			String recu;
			String gest1;
			
			r.emission("lGroupe "+u.id); //Demande de recevoir la liste du groupe
			recu = r.reception();
			System.out.println(recu);//Quand je recois ListeGroupe g1 g2 g3 ... je tronque et prend seulement g1 g2 g3...
			return ajouterLesGroupes(recu);
			
 			//return ajouterLesGroupes("a bb ccc ddddd eeeeeeeee fffffffffffff");
			
		}
		private String[] ajouterLesGroupes(String gest1) {
			return gest1.split(" "); //Je décortique le message recu par le serveur pour le ranger dans une liste
			
		}

	}


}
