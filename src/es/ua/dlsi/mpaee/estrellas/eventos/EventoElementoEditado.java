package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;

public class EventoElementoEditado<TipoElemento> extends Evento<TipoElemento> {
    public EventoElementoEditado(TipoElemento tipoElemento) {
        super(tipoElemento);
    }
}
