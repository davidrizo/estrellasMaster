package es.ua.dlsi.mpaee.estrellas;

import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

public class CartaEstelarController {
    //TODO Lo podemos hacer con un slider
    private static final double ESCALA = 0.5;
    private final Pane pane;
    Stage stage;
    List<Text> estrellas;

    public CartaEstelarController(Modelo modelo) {
        stage = new Stage();
        stage.setMinWidth(Constantes.MAX_COORDENADA_X*ESCALA);
        stage.setMinHeight(Constantes.MAX_COORDENADA_Y*ESCALA);
        pane = new Pane();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();

        estrellas = new LinkedList<>();
        for (Estrella estrella: modelo.estrellasProperty()) {
            addEstrella(estrella);
        }

        // Sesión 2: añadiremos la adición y borrado usando los cambios modelo.estrellasProperty()
    }

    private void addEstrella(Estrella estrella) {
        Text text = new Text();
        text.textProperty().bind(estrella.nombreProperty());
        text.xProperty().bind(estrella.xProperty().multiply(ESCALA));
        text.yProperty().bind(estrella.yProperty().multiply(ESCALA));
        estrellas.add(text);
        pane.getChildren().add(text);
    }

    public void show() {
        stage.show();
    }
}
