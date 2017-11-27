package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoMouseEntered extends Evento<Text> {
    public EventoMouseEntered(Text text) {
        super(text);
    }
}
