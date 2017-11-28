package es.ua.dlsi.mpaee.estrellas;

public class Estrella extends CuerpoCeleste {
    public Estrella(String nombre, double x, double y) {
        super(nombre, x, y);
    }

    public Estrella() {
    }

    public Estrella(CuerpoCeleste origen) {
        super(origen);
    }

    @Override
    public Estrella clone() {
        return new Estrella(this);
    }
}
