package es.ua.dlsi.mpaee.estrellas;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import java.awt.*;

public class Planeta extends CuerpoCeleste {
    FloatProperty red;
    FloatProperty green;
    FloatProperty blue;

    /**
     *
     * @param nombre
     * @param x
     * @param y
     * @param red 0..1
     * @param green 0..1
     * @param blue 0..1
     */
    public Planeta(String nombre, double x, double y, float red, float green, float blue) {
        super(nombre, x, y);
        this.red = new SimpleFloatProperty(red);
        this.green = new SimpleFloatProperty(green);
        this.blue = new SimpleFloatProperty(blue);
    }

    public Planeta(Planeta origen) {
        super(origen);
        this.red = origen.red;
        this.green = origen.green;
        this.blue = origen.blue;
    }

    @Override
    public CuerpoCeleste clone() {
        return new Planeta(this);
    }

    public Float getRed() {
        return red.floatValue();
    }

    public Float getGreen() {
        return green.floatValue();
    }

    public Float getBlue() {
        return blue.floatValue();
    }

    public void setRed(Float red) {
        this.red.set(red);
    }

    public void setGreen(Float green) {
        this.green.set(green);
    }

    public void setBlue(Float blue) {
        this.blue.set(blue);
    }

    public FloatProperty blueProperty() {
        return blue;
    }

    public FloatProperty greenProperty() {
        return green;
    }

    public FloatProperty redProperty() {
        return red;
    }
}
