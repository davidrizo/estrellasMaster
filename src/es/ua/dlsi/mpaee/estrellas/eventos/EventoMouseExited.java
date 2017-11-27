package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoMouseExited extends Evento<Text> {
    public EventoMouseExited(Text text) {
        super(text);
    }
}
