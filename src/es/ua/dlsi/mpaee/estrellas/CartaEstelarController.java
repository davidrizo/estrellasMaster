package es.ua.dlsi.mpaee.estrellas;

import es.ua.dlsi.mpaee.estrellas.eventos.*;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Optional;

public class CartaEstelarController {
    //TODO Lo podemos hacer con un slider
    private static final double ESCALA = 0.5;
    private static final Color HOVER = Color.MAROON;
    private static final Color SELECTED = Color.RED;
    private static final Color UNSELECTED = Color.BLACK;

    private final Pane pane;
    Stage stage;
    HashMap<Estrella, Text> estrellas;
    EstadoCRUD estadoCRUD;
    private Text elementoSeleccionado;
    final Modelo modelo;
    /**
     * Lo usaremos más adelante con el patrón Comando
     */
    Estrella estrellaOriginal;


    public CartaEstelarController(Modelo modelo) {
        this.modelo = modelo;
        stage = new Stage();
        estadoCRUD = EstadoCRUD.sinSeleccion;
        stage.setMinWidth(Constantes.MAX_COORDENADA_X*ESCALA);
        stage.setMinHeight(Constantes.MAX_COORDENADA_Y*ESCALA);
        pane = new Pane();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();

        estrellas = new HashMap<>();
        for (Estrella estrella: modelo.estrellasProperty()) {
            addEstrella(estrella);
        }

        modelo.estrellasProperty().addListener(new ListChangeListener<Estrella>() {
            @Override
            public void onChanged(Change<? extends Estrella> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        /*for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutar
                        }*/
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (Estrella remitem : c.getRemoved()) {
                            pane.getChildren().remove(estrellas.get(remitem));
                        }
                        for (Estrella additem : c.getAddedSubList()) {
                            addEstrella(additem);
                        }
                    }
                }
            }
        });

        pane.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:
                        lanzarEvento(new EventoTeclado(event));
                        break;
                    case DELETE:
                        lanzarEvento(new EventoBorrar());
                        event.consume();
                        break;
                }
            }
        });

        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoClicked(null, event.isAltDown(), event.getClickCount()==2));
            }
        });

        pane.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMoved(null, event.getX(), event.getY()));
                event.consume();
            }
        });

    }

    private void addEstrella(Estrella estrella) {
        Text text = new Text();
        text.setUserData(estrella);
        text.textProperty().bind(estrella.nombreProperty());
        text.xProperty().bind(estrella.xProperty().multiply(ESCALA));
        text.yProperty().bind(estrella.yProperty().multiply(ESCALA));
        estrellas.put(estrella, text);
        pane.getChildren().add(text);

        // interacción
        text.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMouseEntered(text));
            }
        });
        text.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMouseExited(text));
            }
        });
        text.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoClicked(text, event.isAltDown(), event.getClickCount() == 2));
                event.consume(); // para que no pase el evento también al panel
            }
        });
        text.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMoved(text, event.getX(), event.getY()));
                event.consume();
            }
        });
    }

    public void show() {
        stage.show();
    }

    //// ------ Máquina de estados ----
    public void cambiaEstado(EstadoCRUD estado) {
        switch (estado) {
            case sinSeleccion:
                elementoSeleccionado = null;
                modelo.deseleccionar();
                break;
            case consultadoSeleccionado:
                modelo.seleccionar((Estrella)elementoSeleccionado.getUserData());
                break;
            case insertando:
                break;
            case editando:
                estrellaOriginal = new Estrella((Estrella) elementoSeleccionado.getUserData());
                break;
        }
        this.estadoCRUD = estado;
    }

    public void lanzarEvento(Evento<Text> evento) {
        switch (this.estadoCRUD) {
            case sinSeleccion:
                if (evento instanceof EventoMouseEntered) {
                    highlight(evento.getElemento(), HOVER);
                } else if (evento instanceof EventoMouseExited) {
                    highlight(evento.getElemento(), UNSELECTED);
                } else if (evento instanceof EventoClicked && evento.getElemento() != null) {
                    highlight(evento.getElemento(), SELECTED);
                    elementoSeleccionado = evento.getElemento();
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                }
                break;
            case consultadoSeleccionado:
                if (evento instanceof EventoMouseEntered) {
                    if (evento.getElemento() != elementoSeleccionado) {
                        highlight(evento.getElemento(), HOVER);
                    }
                } else if (evento instanceof EventoMouseExited) {
                    if (evento.getElemento() != elementoSeleccionado) {
                        highlight(evento.getElemento(), UNSELECTED);
                    }
                } else if (evento instanceof EventoClicked && evento.getElemento() != null) {
                    if (((EventoClicked)evento).isDobleClick()) {
                        resaltarEditando(elementoSeleccionado, true);
                        cambiaEstado(EstadoCRUD.editando);
                    } else {
                        highlight(elementoSeleccionado, UNSELECTED);
                        highlight(evento.getElemento(), SELECTED);
                        elementoSeleccionado = evento.getElemento();
                        cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                    }
                } else if (evento instanceof EventoTeclado && ((EventoTeclado) evento).getEventoTeclado().getCode() == KeyCode.ESCAPE
                        || evento instanceof EventoClicked && evento.getElemento() == null) {
                    highlight(elementoSeleccionado, UNSELECTED);
                    elementoSeleccionado = null;
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                } else if (evento instanceof EventoBorrar) {
                    cambiaEstado(EstadoCRUD.borrando);
                    borrar();
                }
                break;
            case borrando:
                if (evento instanceof EventoAceptar) {
                    modelo.borrar((Estrella) elementoSeleccionado.getUserData());
                    elementoSeleccionado = null;
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                } else if (evento instanceof EventoCancelar) {
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                }
                break;
            case insertando:
                break;
            case editando:
                if (evento instanceof EventoMoved) {
                    EventoMoved em = (EventoMoved) evento;
                    mover(elementoSeleccionado, em.getX(), em.getY());
                } else if (evento instanceof EventoClicked) {
                    // aceptamos - no es necesario lanzar otro evento para guardar
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                    modelo.editar((Estrella) elementoSeleccionado.getUserData());
                    resaltarEditando(elementoSeleccionado, false);
                } else if (evento instanceof EventoTeclado && ((EventoTeclado) evento).getEventoTeclado().getCode() == KeyCode.ESCAPE) {
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                    ((Estrella) elementoSeleccionado.getUserData()).copia(estrellaOriginal);
                    resaltarEditando(elementoSeleccionado, false);
                }
                break;
        }
    }

    private void mover(Text elemento, double x, double y) {
        Estrella estrella = (Estrella) elemento.getUserData();
        estrella.setX(x/ESCALA);
        estrella.setY(y/ESCALA);
    }

    private void resaltarEditando(Text elemento, boolean resaltar) {
        if (resaltar) {
            // se pinta también el borde resaltándolo más
            elemento.strokeProperty().bind(elemento.fillProperty());
        } else {
            elemento.strokeProperty().unbind();
            elemento.strokeProperty().set(Color.TRANSPARENT);
        }
    }

    private void borrar() {
        // véase http://code.makery.ch/blog/javafx-dialogs-official/ para el resto de diálogos
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Estrellas");
        alert.setHeaderText("Confirmación");
        alert.setContentText("¿Deseas borrar la estrella " + elementoSeleccionado.getUserData() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            lanzarEvento(new EventoAceptar());
        } else {
            lanzarEvento(new EventoCancelar());
        }

    }

    private void highlight(Text elemento, Color color) {
        elemento.setFill(color);
    }
}
