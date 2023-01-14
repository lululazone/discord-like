package client;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


import javax.swing.*;
import javax.swing.border.TitledBorder;



public class ConnexionId extends JFrame implements ActionListener {
	JFrame frame = new JFrame("DiscUT3");
	Container container=getContentPane();
	JLabel userLabel=new JLabel("USERNAME");
	JLabel passwordLabel=new JLabel("PASSWORD");
	JTextField userTextField=new JTextField();
	JPasswordField passwordField=new JPasswordField();
	JButton loginButton;
	static Thread synchronisation;
    static Thread Temission;
    public static String addServ = "localhost"; // a modifier
    public static int portServeur = 4999;//a modifier
    boolean isConnect = false;
    Socket sock;
	
	
	
	public ConnexionId() throws UnknownHostException, IOException {
	 loginButton=new JButton("LOGIN");
	 loginButton.addActionListener(this);
	 TitledBorder border = new TitledBorder("DISCUT3");
	 setLayoutManager();
     setLocationAndSize();
	 border.setTitleJustification(TitledBorder.CENTER);
	 border.setTitlePosition(TitledBorder.TOP);
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
     frame.setSize(500, 500);
     JPanel panel = new JPanel();
     panel.setBorder(border);
     frame.add(panel);
     frame.setLayout(null);
     userLabel.setBounds(50,150,100,30);
     passwordLabel.setBounds(50,220,100,30);
     userTextField.setBounds(150,150,150,30);
     passwordField.setBounds(150,220,150,30);
     loginButton.setBounds(50,300,100,30);
     frame.add(userLabel);
     frame.add(passwordLabel);
     frame.add(userTextField);
     frame.add(passwordField);
     frame.add(loginButton);
     frame.setVisible(true);
  
     
     sock = new Socket(addServ, portServeur);
	}
	 public void setLayoutManager()
	   {
	       container.setLayout(null);
	   }
	   public void setLocationAndSize()
	   {
	       //Setting location and Size of each components using setBounds() method.
	       userLabel.setBounds(50,150,100,30);
	       passwordLabel.setBounds(50,220,100,30);
	       userTextField.setBounds(150,150,150,30);
	       passwordField.setBounds(150,220,150,30);
	       loginButton.setBounds(50,300,100,30);
	 
	 
	   }
	   public void actionPerformed(ActionEvent ae) {
		   String user = userTextField.getText();
		   @SuppressWarnings("deprecation")
		   String password = passwordField.getText();
		  
		   
		   if(user.length() == 0 || password.length() == 0) {
              JOptionPane.showMessageDialog(this, "Veuillez remplir tout les champs", "Erreur remplissage champs", JOptionPane.WARNING_MESSAGE);
		   }
		   else {
			   Request r = new Request(sock);
			   r.emission("login"+" "+user+" "+password);
			   if (r.reception().equals("approved")) {  // a recevoir du serveur
				   Utilisateur u = new Utilisateur(getNom(sock, r, user), getPrenom(sock, r, user), user, getType(sock, r, user), getListeGroupe(sock, r, user));
   
			       isConnect = true;
			       JOptionPane.showMessageDialog(null, "Bienvenue"+user+" ... Merci de patienter....", "Connection", JOptionPane.INFORMATION_MESSAGE);
			       InterfaceDiscussion ie = new InterfaceDiscussion(sock,u,r);
			                        
			  
			       
			
			   } else {
			       JOptionPane.showMessageDialog(null, "Connexion impossible : identifiant ou mot de passe incorrect", "Login Error", JOptionPane.ERROR_MESSAGE);
			   }
	   }
}
	   

	   
	   public String getPrenom(Socket sock, Request r,String user) {  //Demande pour recup prénom
		   r.emission("Prenom "+user);
    	   return r.reception();
		   
	   }
	   public String getNom(Socket sock, Request r,String user) { //Demande pour recup nom
    	   r.emission("Nom "+user);
    	   return r.reception();
		   
	   }
	   public String getType(Socket sock, Request r ,String user) { //Demande pour recup type
		   r.emission("Type "+user);
		   return r.reception();
		   
	   }
	   
	   public List<String> getListeGroupe(Socket sock,Request r , String user){ //Demande pour recup listegroupe
		   List<String> lgrp = new ArrayList<>();
		   r.emission("lGroupe "+user);
		   String[] result = r.reception().split(" ");
		   for(String mot : result) {
			   lgrp.add(mot);
		   }
		   return lgrp;
	   }
	
	
	

}