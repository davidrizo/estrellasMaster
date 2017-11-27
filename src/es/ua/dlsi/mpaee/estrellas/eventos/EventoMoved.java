package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoMoved extends Evento<Text> {
    private final double x;
    private final double y;

    public EventoMoved(Text text, double x, double y) {
        super(text);
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
