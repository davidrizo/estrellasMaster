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

public class PlanetaView extends CuerpoCelesteView<Planeta> {
    private static final double RADIO = 20;
    Group root;
    Text nombre;
    Circle circle;
    ObjectProperty<Color> circleColor;

    public PlanetaView(Planeta planeta) {
        super(planeta);
        this.nombre = new Text();
        this.nombre.textProperty().bind(planeta.nombreProperty());
        this.nombre.xProperty().bind(planeta.xProperty().multiply(escala));
        this.nombre.yProperty().bind(planeta.yProperty().multiply(escala));
        this.nombre.setFont(Font.font(8));

        this.circleColor = new SimpleObjectProperty<>(Color.color(planeta.getRed(), planeta.getGreen(), planeta.getBlue()));
        Color color;
        ChangeListener<? super Number> listenerColor = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                circleColor.set(Color.color(planeta.getRed(), planeta.getGreen(), planeta.getBlue()));
            }
        };
        planeta.blueProperty().addListener(listenerColor);
        planeta.greenProperty().addListener(listenerColor);
        planeta.redProperty().addListener(listenerColor);

        this.circle = new Circle(RADIO);
        this.circle.centerXProperty().bind(planeta.xProperty().multiply(escala));
        this.circle.centerYProperty().bind(planeta.yProperty().multiply(escala));
        this.circle.fillProperty().bind(circleColor);

        this.root = new Group(this.circle, this.nombre);
    }

    @Override
    public Node getRoot() {
        return root;
    }

    @Override
    public void resaltar(boolean resaltar) {
        if (resaltar) {
            // se pinta también el borde resaltándolo más
            circle.strokeProperty().bind(circle.fillProperty());
        } else {
            circle.strokeProperty().unbind();
            circle.strokeProperty().set(Color.TRANSPARENT);
        }

    }

    @Override
    public void highlight(Color color) {
        nombre.setFill(color);
    }
}
