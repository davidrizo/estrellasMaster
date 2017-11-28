package es.ua.dlsi.mpaee.estrellas;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class EstrellaView extends CuerpoCelesteView<Estrella> {
    private static final double RADIO = 20;
    Text text;

    public EstrellaView(Estrella estrella) {
        super(estrella);
        this.text = new Text();
        this.text.textProperty().bind(estrella.nombreProperty());
        this.text.xProperty().bind(estrella.xProperty().multiply(escala));
        this.text.yProperty().bind(estrella.yProperty().multiply(escala));
    }

    @Override
    public Node getRoot() {
        return text;
    }

    @Override
    public void resaltar(boolean resaltar) {
        if (resaltar) {
            // se pinta también el borde resaltándolo más
            text.strokeProperty().bind(text.fillProperty());
        } else {
            text.strokeProperty().unbind();
            text.strokeProperty().set(Color.TRANSPARENT);
        }

    }

    @Override
    public void highlight(Color color) {
        text.setFill(color);
    }
}
