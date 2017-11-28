package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.CuerpoCelesteView;
import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.text.Text;

public class EventoMouseExited extends Evento<CuerpoCelesteView<?>> {
    public EventoMouseExited(CuerpoCelesteView<?> view) {
        super(view);
    }
}
