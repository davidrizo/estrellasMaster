package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.CuerpoCelesteView;
import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoMoved extends Evento<CuerpoCelesteView<?>> {
    private final double x;
    private final double y;

    public EventoMoved(CuerpoCelesteView<?> vista, double x, double y) {
        super(vista);
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
