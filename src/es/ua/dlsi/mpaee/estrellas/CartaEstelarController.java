package es.ua.dlsi.mpaee.estrellas;

import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CartaEstelarController {
    //TODO Lo podemos hacer con un slider
    private static final double ESCALA = 0.5;
    private final Pane pane;
    Stage stage;
    HashMap<Estrella, Text> estrellas;

    public CartaEstelarController(Modelo modelo) {
        stage = new Stage();
        stage.setMinWidth(Constantes.MAX_COORDENADA_X*ESCALA);
        stage.setMinHeight(Constantes.MAX_COORDENADA_Y*ESCALA);
        pane = new Pane();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();

        estrellas = new HashMap<>();
        for (Estrella estrella: modelo.estrellasProperty()) {
            addEstrella(estrella);
        }

        modelo.estrellasProperty().addListener(new ListChangeListener<Estrella>() {
            @Override
            public void onChanged(Change<? extends Estrella> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        /*for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutar
                        }*/
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (Estrella remitem : c.getRemoved()) {
                            pane.getChildren().remove(estrellas.get(remitem));
                        }
                        for (Estrella additem : c.getAddedSubList()) {
                            addEstrella(additem);
                        }
                    }
                }
            }
        });
    }

    private void addEstrella(Estrella estrella) {
        Text text = new Text();
        text.textProperty().bind(estrella.nombreProperty());
        text.xProperty().bind(estrella.xProperty().multiply(ESCALA));
        text.yProperty().bind(estrella.yProperty().multiply(ESCALA));
        estrellas.put(estrella, text);
        pane.getChildren().add(text);
    }

    public void show() {
        stage.show();
    }
}
