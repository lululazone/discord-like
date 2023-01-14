package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Request {

	public Socket socket;
	public Request(Socket socket) {
		this.socket = socket;
	}
	public void emission(String mess) {
		try {
            //On initialise notre sortie
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            //On envoie le message encodé
            out.println(mess);
            out.flush();

        } catch (IOException e) {
            System.err.println("Le serveur ne répond pas, erreur :"+e);
        }
		
	}
	
	public String reception() {
		try {
			InputStreamReader streamreader = new InputStreamReader(socket.getInputStream());
	        BufferedReader in = new BufferedReader(streamreader);
	        return in.readLine();
	        
		 } catch (IOException e) {
	            System.err.println("Le serveur ne répond pas, erreur:"+e);
	        }
		return null;
        
	}


}
