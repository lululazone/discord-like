package serveur;

import java.awt.event.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.*;
import javax.swing.*;

public class Serveur extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private Database database;
    
    private JButton boutonAddUser = new JButton("Creer un utilisateur");
    private JButton boutonSupUser = new JButton("Supprimer un utilisateur");
    private JButton boutonAddGroup = new JButton("Creer un groupe");
    private JButton boutonSupGroup = new JButton("Supprimer un groupe");
    private JButton boutonAddUserGroup = new JButton("Ajouter un utilisateur � un groupe");
    private JButton boutonStop = new JButton("Eteindre le serveur");
    private JPanel container = new JPanel();

    public Serveur() throws ClassNotFoundException, SQLException {
    	database = new Database();
        initInterfaceMenu();
    }
    
    public void initInterfaceMenu() {
    	this.setTitle("Serveur DiscUT3");
        this.setSize(250, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        boutonAddUser.addActionListener(new alAddUser() );
        container.add(boutonAddUser);
        
        boutonSupUser.addActionListener(new alSupUser() );
        container.add(boutonSupUser);
        
        boutonAddGroup.addActionListener(new alAddGroup() );
        container.add(boutonAddGroup);
        
        boutonSupGroup.addActionListener(new alSupGroup() );
        container.add(boutonSupGroup);
        
        boutonAddUserGroup.addActionListener(new alAddUserGroup() );
        container.add(boutonAddUserGroup);
        
        boutonStop.addActionListener(new alStop() );
        container.add(boutonStop);
        
        this.setContentPane(container);
        this.setVisible(true);  
    }
    
    public Categorie strToCategorie (String str) {
    	switch (str) {
    	case "Administration":
    		return Categorie.ADMINISTRATION;
    	case "El�ve":
    		return Categorie.ELEVE;
    	case "Enseignant":
    		return Categorie.ENSEIGNANT;
    	case "Entretient":
    		return Categorie.ENTRETIENT;
    	case "S�curit�":
    		return Categorie.SECURITE;
    	default:
    		return Categorie.ELEVE;
    	}
    }
    
    class alAddUser implements ActionListener { public void actionPerformed(ActionEvent ae) {
    	String username = JOptionPane.showInputDialog(container, "Nom d'utilisateur : ");
		String name = JOptionPane.showInputDialog(container, "Nom de famille : ");
		String firstName = JOptionPane.showInputDialog(container, "Pr�nom : ");

		String choices[] = {"Administration","El�ve","Enseignant","Entretient","S�curit�"};
		String categorie = (String) JOptionPane.showInputDialog(container, "Cat�gorie : ",
				"Input", JOptionPane.CLOSED_OPTION, null, choices, choices[1]);
		
		Utilisateur newUser = new Utilisateur(username, name, firstName, strToCategorie(categorie));
		String password = JOptionPane.showInputDialog(container, "Mot de passe : ");
		
		int result = database.addUser(newUser, password);
		
		if (result == 1) {
			JOptionPane.showMessageDialog(container, "Nouvel utilisateur cr�� !");
		} else {
			JOptionPane.showMessageDialog(container, "Erreur lors de la cr�ation de l'utilisateur\nCode de sortie : "+result);
		}
    }} 
    
    class alSupUser implements ActionListener { public void actionPerformed(ActionEvent ae) {
    	String username = JOptionPane.showInputDialog(container, "Nom d'utilisateur : ");

		int result1 = database.removeAppartenir(database.getUser(username));
		int result2 = database.removeUser(username);
		
		if (result1 != 0 && result2 == 1) {
			JOptionPane.showMessageDialog(container, "Utilisateur supprim� !");
		} else {
			JOptionPane.showMessageDialog(container, "Erreur lors de la suppression de l'utilisateur\nCode de sortie : "+result2);
		}
    }} 
    
    class alAddGroup implements ActionListener { public void actionPerformed(ActionEvent ae) {
    	String groupName = JOptionPane.showInputDialog(container, "Nom du groupe : ");

		int result = database.addGroupe(groupName);
		
		if (result == 1) {
			JOptionPane.showMessageDialog(container, "Groupe cr�� !");
		} else {
			JOptionPane.showMessageDialog(container, "Erreur lors de la cr�ation du groupe\nCode de sortie : "+result);
		}
    }} 
    
    class alSupGroup implements ActionListener { public void actionPerformed(ActionEvent ae) {
    	String groupName = JOptionPane.showInputDialog(container, "Nom du groupe : ");

		int result = database.removeGroupe(groupName);
		
		if (result == 1) {
			JOptionPane.showMessageDialog(container, "Groupe supprim� !");
		} else {
			JOptionPane.showMessageDialog(container, "Erreur lors de la suppression du groupe\nCode de sortie : "+result);
		}
    }} 
    
    class alAddUserGroup implements ActionListener { public void actionPerformed(ActionEvent ae) {
    	String username = JOptionPane.showInputDialog(container, "Nom d'utilisateur : ");
    	String groupName = JOptionPane.showInputDialog(container, "Nom du groupe : ");

		int result = database.addAppartenir(username, groupName);
		
		if (result == 1) {
			JOptionPane.showMessageDialog(container, "Utilisateur ajout� au groupe !");
		} else {
			JOptionPane.showMessageDialog(container, "Erreur lors de l'ajout de l'utilisateur au groupe\nCode de sortie : "+result);
		}
    }} 
    
    class alStop implements ActionListener { public void actionPerformed(ActionEvent ae) {
    	System.exit(0);;
    }} 
    
    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
    	
    	ServerSocket ss = new ServerSocket(4999);
    	Serveur serv = new Serveur();
    	Client client = new Client(ss,serv.database);
    	client.start();
    	
		
	}
}
