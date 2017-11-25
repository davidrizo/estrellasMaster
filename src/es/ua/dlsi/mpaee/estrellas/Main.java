package es.ua.dlsi.mpaee.estrellas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("crud.fxml"));
        primaryStage.setTitle("Estrellas");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        CartaEstelarController cartaEstelarController = new CartaEstelarController(Modelo.getInstance());
        cartaEstelarController.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
