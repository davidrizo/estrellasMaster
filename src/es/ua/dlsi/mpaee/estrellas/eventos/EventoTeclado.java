package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

public class EventoTeclado extends Evento {
    KeyEvent eventoTeclado;
    public EventoTeclado(KeyEvent event) {
        this.eventoTeclado = event;
    }

    public KeyEvent getEventoTeclado() {
        return eventoTeclado;
    }
}
