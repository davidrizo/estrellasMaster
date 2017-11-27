package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Estrella;
import es.ua.dlsi.mpaee.estrellas.Evento;

public class EventoValorCambiado extends Evento<Estrella> {
    public EventoValorCambiado(Estrella estrella) {
        super(estrella);
    }
}
