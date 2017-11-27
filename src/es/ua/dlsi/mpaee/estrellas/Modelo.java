package es.ua.dlsi.mpaee.estrellas;

import es.ua.dlsi.mpaee.estrellas.eventos.EventoElementoEditado;
import es.ua.dlsi.mpaee.estrellas.eventos.EventoElementoSeleccionado;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


class Modelo {
    public static Modelo instance = null;
    private ListProperty<Estrella> estrellas;
    private HashMap<Class<? extends Evento>, List<INotificable>> colasNotificaciones;

    private Modelo() {
        colasNotificaciones = new HashMap<>();
        // https://es.wikipedia.org/wiki/Anexo:Estrellas
        // https://es.wikipedia.org/wiki/Osa_Mayor#/media/File:Osamayor.png
        estrellas = new SimpleListProperty<>(FXCollections.observableArrayList(
                new Estrella("Benetnasch", 143, 401),
                new Estrella("Mizar", 288, 286),
                new Estrella("Alioth", 406, 286),
                new Estrella("Megreth", 545, 273),
                new Estrella("Pechda", 629, 363),
                new Estrella("Merak", 815, 266),
                new Estrella("Dubhe", 780, 122)
                ));
    }

    public static final Modelo getInstance() {
        synchronized (Modelo.class) {
            if (instance == null) {
                instance = new Modelo();
            }
        }
        return instance;
    }

    public ListProperty<Estrella> estrellasProperty() {
        return estrellas;
    }

    public void add(Estrella estrella) {
        estrellas.add(estrella);
    }

    public void editar(Estrella estrella) {
        // no hacemos nada más porque no aún no es persistente
        notificar(new EventoElementoEditado(estrella));
    }

    private void notificar(Evento evento) {
        List<INotificable> suscripciones = colasNotificaciones.get(evento.getClass());
        if (suscripciones != null) {
            for (INotificable notificable : suscripciones) {
                notificable.notificar(evento);
            }
        }

    }

    public void suscribir(Class<? extends Evento> claseEventos, INotificable notificable) {
        List<INotificable> suscripciones = colasNotificaciones.get(claseEventos);
        if (suscripciones == null) {
            suscripciones = new LinkedList<>();
            colasNotificaciones.put(claseEventos, suscripciones);
        }
        suscripciones.add(notificable);
    }

    public void borrar(Estrella estrella) {
        estrellas.remove(estrella);
    }

    public void seleccionar(Estrella estrella) {
        notificar(new EventoElementoSeleccionado(estrella));
    }
}