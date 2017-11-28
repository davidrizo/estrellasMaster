package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.CuerpoCelesteView;
import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoClicked extends Evento<CuerpoCelesteView<?>> {
    private final boolean dobleClick;
    private final boolean alt;
    private final double x;
    private final double y;

    public EventoClicked(CuerpoCelesteView view, boolean alt, boolean dobleClick, double x, double y) {
        super(view);
        this.alt = alt;
        this.dobleClick = dobleClick;
        this.x = x;
        this.y = y;
    }

    public boolean isDobleClick() {
        return dobleClick;
    }

    public boolean isAlt() {
        return alt;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
