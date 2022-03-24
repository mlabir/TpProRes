package tp_pro_res;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServeurChat extends Thread {

	private boolean isActive = true;
	private int nombreClient = 0;
	private List<Conversation> clients = new ArrayList<Conversation>();
	
	public static void main(String[] args) {
		
		new ServeurChat().start();

	}
	
	@Override
	public void run() {
		
		try {
			ServerSocket serverSocket = new ServerSocket(1234);
			
			while(isActive) {
				Socket socket = serverSocket.accept();
				++nombreClient;
				Conversation conversation = new Conversation(socket, nombreClient);
				clients.add(conversation);
				conversation.start();
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	class Conversation extends Thread {
		
		protected Socket socketClient;
		protected int numero;
		
		public Conversation(Socket socketCient, int numero) {			
			this.socketClient = socketCient;
			this.numero = numero;
		}
		
		public void broadcastMessage( String msg, Socket socket, int numClient) {
			try {
				for (Conversation client:clients) {
					if(client.socketClient != socket) {						
						if(client.numero == numClient || numClient == -1) {
							PrintWriter printWriter = new PrintWriter(client.socketClient.getOutputStream(), true);
							printWriter.println(msg);							
						}
					}
				}
			}catch (IOException e) {
				e.printStackTrace();
			}
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
					if(req.contains("=>")) {
						String[] reqParam = req.split("=>");
						if(reqParam.length == 2);
						String msg = reqParam[1];
						int numeroClient = Integer.parseInt(reqParam[0]);
						broadcastMessage(msg, socketClient, numeroClient);						
					}else {
						broadcastMessage(req, socketClient, -1);
					}
				}
				
			}catch(IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}

}
