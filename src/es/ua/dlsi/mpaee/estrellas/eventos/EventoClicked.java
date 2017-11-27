package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoClicked extends Evento<Text> {
    private final boolean dobleClick;
    private final boolean alt;

    public EventoClicked(Text text, boolean alt, boolean dobleClick) {
        super(text);
        this.alt = alt;
        this.dobleClick = dobleClick;
    }

    public boolean isDobleClick() {
        return dobleClick;
    }

    public boolean isAlt() {
        return alt;
    }
}
