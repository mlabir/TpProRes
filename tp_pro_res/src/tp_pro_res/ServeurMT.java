package tp_pro_res;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMT extends Thread {

	private boolean isActive = true;
	private int nombreClient = 0;
	
	public static void main(String[] args) {
		
		new ServeurMT().start();

	}
	
	@Override
	public void run() {
		
		try {
			ServerSocket serverSocket = new ServerSocket(1234);
			
			while(isActive) {
				Socket socket = serverSocket.accept();
				++nombreClient;
				new Conversation(socket, nombreClient).start();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	class Conversation extends Thread {
		
		private Socket socketClient;
		private int numero;
		
		public Conversation(Socket socketCient, int numero) {			
			this.socketClient = socketCient;
			this.numero = numero;
		}
		
		@Override
		public void run() {
			
			try {
				InputStream is = socketClient.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				
				PrintWriter pw = new PrintWriter(socketClient.getOutputStream(), true);
				String ipClient = socketClient.getRemoteSocketAddress().toString();
				pw.println("Bienvenu, vous etes le client numéro "+numero);
				System.out.println("Connexion du client numéro "+numero+", IP = "+ipClient);
				
				while (true) {
					String req  = br.readLine();
					String reponse = "Length = " + req.length();
					pw.println(reponse);
				}
				
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}

}
