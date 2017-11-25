package es.ua.dlsi.mpaee.estrellas;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Estrella {
    private final StringProperty nombre;
    private final DoubleProperty x;
    private final DoubleProperty y;

    public Estrella(String nombre, double x, double y) {
        this.nombre = new SimpleStringProperty(nombre);
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
    }

    public Estrella() {
        this.nombre = new SimpleStringProperty("");
        this.x = new SimpleDoubleProperty(0);
        this.y = new SimpleDoubleProperty(0);
    }

    public Estrella(Estrella origen) {
        this.nombre = new SimpleStringProperty(origen.nombre.get());
        this.x = new SimpleDoubleProperty(origen.x.get());
        this.y = new SimpleDoubleProperty(origen.y.get());
    }

    public void copia(Estrella origen) {
        this.nombre.setValue(origen.nombre.getValue());
        this.x.setValue(origen.x.get());
        this.y.setValue(origen.y.get());

    }
    public StringProperty nombreProperty() {
        return nombre;
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public DoubleProperty yProperty() {
        return y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(nombre.get());
        sb.append(' ');
        sb.append('(');
        sb.append(x.get());
        sb.append(',');
        sb.append(y.get());
        sb.append(')');
        return sb.toString();
    }

    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.setValue(nombre);
    }

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    public void setX(double x) {
        this.x.setValue(x);
    }

    public void setY(double y) {
        this.y.setValue(y);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Estrella estrella = (Estrella) o;

        if (nombre != null ? !nombre.equals(estrella.nombre) : estrella.nombre != null) return false;
        if (x != null ? !x.equals(estrella.x) : estrella.x != null) return false;
        return y != null ? y.equals(estrella.y) : estrella.y == null;
    }

}
