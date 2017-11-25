package es.ua.dlsi.mpaee.estrellas;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;


public class Modelo {
    public static Modelo instance = null;
    private ListProperty<Estrella> estrellas;

    private Modelo() {
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

}