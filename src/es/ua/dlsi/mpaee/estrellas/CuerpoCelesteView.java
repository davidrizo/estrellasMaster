package es.ua.dlsi.mpaee.estrellas;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class CuerpoCelesteView<TipoCuerpoCeleste extends CuerpoCeleste> {
    protected DoubleProperty escala;
    protected TipoCuerpoCeleste cuerpoCeleste;

    public CuerpoCelesteView(TipoCuerpoCeleste cuerpoCeleste) {
        this.cuerpoCeleste = cuerpoCeleste;
        escala = new SimpleDoubleProperty(1); // por defecto no está escalado
    }

    public TipoCuerpoCeleste getCuerpoCeleste() {
        return cuerpoCeleste;
    }

    public abstract Node getRoot();

    //TODO Estos nombres no son lo más afortunados ...
    public abstract void resaltar(boolean resaltar);

    public abstract void highlight(Color color);

    public DoubleProperty escalaProperty() {
        return escala;
    }

    public double getEscala() {
        return escala.get();
    }

    public void setEscala(double escala) {
        this.escala.set(escala);
    }
}
