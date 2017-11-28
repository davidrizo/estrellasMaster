package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.CuerpoCeleste;
import es.ua.dlsi.mpaee.estrellas.Evento;

public class EventoValorCambiado extends Evento<CuerpoCeleste> {
    public EventoValorCambiado(CuerpoCeleste estrella) {
        super(estrella);
    }
}
