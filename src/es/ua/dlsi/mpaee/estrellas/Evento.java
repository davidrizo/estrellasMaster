package es.ua.dlsi.mpaee.estrellas;

public class Evento<TipoElemento> {
    TipoElemento elemento;

    public Evento(TipoElemento elemento) {
        this.elemento = elemento;
    }

    public Evento() {
    }

    public TipoElemento getElemento() {
        return elemento;
    }
}
