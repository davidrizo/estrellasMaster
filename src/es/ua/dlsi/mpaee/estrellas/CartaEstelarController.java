package es.ua.dlsi.mpaee.estrellas;

import es.ua.dlsi.im3.gui.command.CommandManager;
import es.ua.dlsi.im3.gui.command.ICommand;
import es.ua.dlsi.im3.gui.command.IObservableTaskRunner;
import es.ua.dlsi.mpaee.estrellas.eventos.*;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
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
    private TextField elementoInsertando;

    CommandManager commandManager;


    public CartaEstelarController(Modelo modelo) {
        this.modelo = modelo;
        commandManager = new CommandManager();

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
                        //TODO Añadir menú para esto
                    case Z:
                        if (event.isShortcutDown()) {
                            deshacer();
                            event.consume();
                        }
                        break;
                    case Y:
                        if (event.isShortcutDown()) {
                            rehacer();
                            event.consume();
                        }
                        break;
                }
            }
        });

        pane.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoClicked(null, event.isAltDown(), event.getClickCount()==2, event.getX(), event.getY()));
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

    private void rehacer() {
        try {
            commandManager.redo();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO Mensaje error
        }
    }

    private void deshacer() {
        try {
            commandManager.undo();
        } catch (Exception e) {
            e.printStackTrace();
            //TODO Mensaje error
        }
    }

    private Text addEstrella(Estrella estrella) {
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
                lanzarEvento(new EventoClicked(text, event.isAltDown(), event.getClickCount() == 2, event.getX(), event.getY()));
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
        return text;
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
                } else if (evento instanceof EventoClicked && evento.getElemento() == null && ((EventoClicked) evento).isDobleClick()) {
                    insertar(((EventoClicked) evento).getX(), ((EventoClicked) evento).getY());
                    cambiaEstado(EstadoCRUD.insertando);
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
                    if (((EventoClicked) evento).isDobleClick()) {
                        resaltarEditando(elementoSeleccionado, true);
                        cambiaEstado(EstadoCRUD.editando);
                    } else {
                        highlight(elementoSeleccionado, UNSELECTED);
                        highlight(evento.getElemento(), SELECTED);
                        elementoSeleccionado = evento.getElemento();
                        cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                    }
                } else if (evento instanceof EventoClicked && evento.getElemento() == null && ((EventoClicked) evento).isDobleClick()) {
                    highlight(elementoSeleccionado, UNSELECTED);
                    EventoClicked ec = (EventoClicked) evento;
                    insertar(ec.getX(), ec.getY());
                    cambiaEstado(EstadoCRUD.insertando);
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
                    comandoBorrar();
                } else if (evento instanceof EventoCancelar) {
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                }
                break;
            case insertando:
                if (evento instanceof EventoCancelar) {
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                    pane.getChildren().remove(elementoInsertando);
                    elementoInsertando = null;
                } else if (evento instanceof EventoAceptar) {
                    comandoInsertar();
                }
                break;
            case editando:
                if (evento instanceof EventoMoved) {
                    EventoMoved em = (EventoMoved) evento;
                    mover(elementoSeleccionado, em.getX(), em.getY());
                } else if (evento instanceof EventoClicked) {
                    // aceptamos - no es necesario lanzar otro evento para guardar
                    comandoEditar();
                } else if (evento instanceof EventoTeclado && ((EventoTeclado) evento).getEventoTeclado().getCode() == KeyCode.ESCAPE) {
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                    ((Estrella) elementoSeleccionado.getUserData()).copia(estrellaOriginal);
                    resaltarEditando(elementoSeleccionado, false);
                }
                break;
        }
    }

    private void comandoInsertar() {
        ICommand command = new ICommand() {

            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                Estrella estrella = new Estrella(elementoInsertando.getText(), elementoInsertando.getLayoutX() / ESCALA, elementoInsertando.getLayoutY() / ESCALA);
                pane.getChildren().remove(elementoInsertando);
                elementoInsertando = null;
                modelo.add(estrella);
                cambiaEstado(EstadoCRUD.sinSeleccion);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                //TODO
            }

            @Override
            public void redo() throws Exception {
                //TODO
            }

            @Override
            public String getEventName() {
                return "Insertar";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO
        }
    }

    private void comandoEditar() {
        ICommand command = new ICommand() {
            // Debemos usar estos datos que se apilan, no podemos usar los del controller directamente
            // porque pueden haber inserciones / borrados
            Estrella estrellaDestino;
            Estrella contenidoSinEditar;
            Estrella contenidoEditado;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                resaltarEditando(elementoSeleccionado, false);
                estrellaDestino = (Estrella) elementoSeleccionado.getUserData();
                contenidoSinEditar = new Estrella(estrellaOriginal);
                contenidoEditado = new Estrella(estrellaDestino);
                cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                modelo.editar(estrellaDestino);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                estrellaDestino.copia(contenidoSinEditar);
                modelo.editar(estrellaDestino);
            }

            @Override
            public void redo() throws Exception {
                estrellaDestino.copia(contenidoEditado);
                modelo.editar(estrellaDestino);
            }

            @Override
            public String getEventName() {
                return "Editar";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO
        }
    }

    private void comandoBorrar() {
        // No estamos teniendo a propósito en cuenta el orden en que se mostrarán en la lista de CRUDController
        ICommand command = new ICommand() {
            // Debemos usar estos datos que se apilan, no podemos usar los del controller directamente
            // porque pueden haber inserciones / borrados
            Estrella estrellaDestino;
            Text vistaSeleccionada;

            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                vistaSeleccionada = elementoSeleccionado;
                estrellaDestino = (Estrella) elementoSeleccionado.getUserData();
                doBorrar();
            }

            private void doBorrar() {
                modelo.borrar(estrellaDestino);
                elementoSeleccionado = null;
                cambiaEstado(EstadoCRUD.sinSeleccion);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                modelo.add(estrellaDestino); // esto desencadena la inserción en esta vista
            }

            @Override
            public void redo() throws Exception {
                doBorrar();
            }

            @Override
            public String getEventName() {
                return "Borrar";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (Exception e) {
            e.printStackTrace();
            //TODO Mensaje error
        }
    }

    private void insertar(double x, double y) {
        elementoInsertando = new TextField();
        elementoInsertando.setLayoutX(x);
        elementoInsertando.setLayoutY(y);
        pane.getChildren().add(elementoInsertando);
        elementoInsertando.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case ESCAPE:
                        lanzarEvento(new EventoCancelar());
                        break;
                    case ENTER:
                        lanzarEvento(new EventoAceptar());
                        break;
                }
            }
        });
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
