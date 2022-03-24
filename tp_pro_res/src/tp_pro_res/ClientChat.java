package tp_pro_res;


import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ListView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ClientChat extends Application {
	PrintWriter pw;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage app) throws Exception {
		app.setTitle("Client Chat");
		BorderPane borderPane = new BorderPane();
		
		Label labelHost = new Label("Host : ");
		TextField textFieldHost = new TextField("localhost");
		Label labelPort = new Label("Port : ");
		TextField textFieldPort = new TextField("1234");
		Button buttonConnecter = new Button("Connecter");
		
		HBox hBox = new HBox();
		hBox.setSpacing(10);
		hBox.setPadding(new Insets(10));
		hBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		hBox.getChildren().addAll(labelHost, textFieldHost, labelPort, textFieldPort, buttonConnecter);
		
		borderPane.setTop(hBox);
		
		VBox vBox = new VBox();
		vBox.setSpacing(10);
		vBox.setPadding(new Insets(10));
		
		ObservableList<String> listModel = FXCollections.observableArrayList();
		ListView<String> listView = new ListView<String>(listModel);
		vBox.getChildren().add(listView);
		borderPane.setCenter(vBox);
		
		Label labelMessage = new Label("Message");
		TextField textFieldMessage = new TextField();
		textFieldMessage.setPrefSize(400, 30);
		Button buttonEnvoyer = new Button("Envoyer");
		
		HBox hBox2 = new HBox();
		hBox2.setSpacing(10);
		hBox2.setPadding(new Insets(10));
		hBox2.getChildren().addAll(labelMessage, textFieldMessage, buttonEnvoyer);
		
		borderPane.setBottom(hBox2);
		
		Scene scene = new Scene(borderPane, 800, 600);
		app.setScene(scene);
		app.show();
		
		buttonConnecter.setOnAction((evt)->{
			String host = textFieldHost.getText();
			int port = Integer.parseInt(textFieldPort.getText());
			try {
				Socket socket = new Socket(host, port);
				InputStream is = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				pw = new PrintWriter(socket.getOutputStream(), true);
				new Thread(()->{
					while(true) {
						try {							
							String response = br.readLine();
							Platform.runLater(()->{
								listModel.add(response);
							});
						}catch (IOException e) {							
							e.printStackTrace();
						}
					}
				}).start();
				
			}catch(IOException e) {
				e.printStackTrace();
			}
			
		});
		
		buttonEnvoyer.setOnAction((evt)->{
			String msg = textFieldMessage.getText();
			pw.println(msg);
		});
	}
		
}
