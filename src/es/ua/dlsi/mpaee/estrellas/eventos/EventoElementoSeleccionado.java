package es.ua.dlsi.mpaee.estrellas.eventos;

import es.ua.dlsi.mpaee.estrellas.Evento;

public class EventoElementoSeleccionado<TipoElemento> extends Evento<TipoElemento> {
    public EventoElementoSeleccionado(TipoElemento tipoElemento) {
        super(tipoElemento);
    }
}
