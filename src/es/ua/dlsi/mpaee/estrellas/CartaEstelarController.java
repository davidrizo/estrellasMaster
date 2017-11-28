package es.ua.dlsi.mpaee.estrellas;

import es.ua.dlsi.im3.gui.ShowError;
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
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Optional;

/**
 * Sólo permitimos insertar manualmente estrellas. El resto de cuerpos celestes los insertará el sistema
 */
public class CartaEstelarController {
    //TODO Lo podemos hacer con un slider
    private static final double ESCALA = 0.5;
    protected static final Color HOVER = Color.MAROON;
    protected static final Color SELECTED = Color.RED;
    protected static final Color UNSELECTED = Color.BLACK;


    private final Pane pane;
    Stage stage;
    HashMap<CuerpoCeleste, CuerpoCelesteView<?>> cuerpoCelestes;
    EstadoCRUD estadoCRUD;
    private CuerpoCelesteView<?> elementoSeleccionado;
    final Modelo modelo;
    /**
     * Lo usaremos más adelante con el patrón Comando
     */
    CuerpoCeleste cuerpoCelesteOriginal;
    private TextField elementoInsertando;

    CommandManager commandManager;


    public CartaEstelarController(Modelo modelo) throws Exception {
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

        cuerpoCelestes = new HashMap<>();
        for (CuerpoCeleste cuerpoCeleste: modelo.cuerpoCelestesProperty()) {
            addCuerpoCeleste(cuerpoCeleste);
        }

        modelo.cuerpoCelestesProperty().addListener(new ListChangeListener<CuerpoCeleste>() {
            @Override
            public void onChanged(Change<? extends CuerpoCeleste> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        /*for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutar
                        }*/
                    } else if (c.wasUpdated()) {
                        //update item - no lo necesitamos de momento porque lo tenemos todo con binding, si no podríamos actualizar aquí
                    } else {
                        for (CuerpoCeleste remitem : c.getRemoved()) {
                            CuerpoCelesteView<?> cuerpoCelesteView = cuerpoCelestes.get(remitem);
                            pane.getChildren().remove(cuerpoCelesteView.getRoot());
                        }
                        for (CuerpoCeleste additem : c.getAddedSubList()) {
                            try {
                                addCuerpoCeleste(additem);
                            } catch (Exception e) {
                                //TODO Log
                                throw new RuntimeException(e);
                            }
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
            ShowError.show(stage, "No se puede rehacer", e);
        }
    }

    private void deshacer() {
        try {
            commandManager.undo();
        } catch (Exception e) {
            ShowError.show(stage, "No se puede deshacer", e);
        }
    }

    private CuerpoCelesteView addCuerpoCeleste(CuerpoCeleste cuerpoCeleste) throws Exception {
        CuerpoCelesteView cuerpoCelesteView = CuerpoCelesteViewFactory.getInstance().create(cuerpoCeleste);
        cuerpoCelesteView.escalaProperty().set(ESCALA); // la escala podría ser un slider / zoom
        cuerpoCelestes.put(cuerpoCeleste, cuerpoCelesteView);
        pane.getChildren().add(cuerpoCelesteView.getRoot());

        // interacción
        cuerpoCelesteView.getRoot().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMouseEntered(cuerpoCelesteView));
            }
        });
        cuerpoCelesteView.getRoot().setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMouseExited(cuerpoCelesteView));
            }
        });
        cuerpoCelesteView.getRoot().setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoClicked(cuerpoCelesteView, event.isAltDown(), event.getClickCount() == 2, event.getX(), event.getY()));
                event.consume(); // para que no pase el evento también al panel
            }
        });
        cuerpoCelesteView.getRoot().setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lanzarEvento(new EventoMoved(cuerpoCelesteView, event.getX(), event.getY()));
                event.consume();
            }
        });
        return cuerpoCelesteView;
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
                modelo.seleccionar(elementoSeleccionado.getCuerpoCeleste());
                break;
            case insertando:
                break;
            case editando:
                cuerpoCelesteOriginal = elementoSeleccionado.getCuerpoCeleste().clone();
                break;
        }
        this.estadoCRUD = estado;
    }

    public void lanzarEvento(Evento<CuerpoCelesteView<?>> evento) {
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
                    elementoSeleccionado.getCuerpoCeleste().copia(cuerpoCelesteOriginal);
                    resaltarEditando(elementoSeleccionado, false);
                }
                break;
        }
    }

    private void comandoInsertar() {
        ICommand command = new ICommand() {
            CuerpoCeleste cuerpoCelesteDestino;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                cuerpoCelesteDestino = new Estrella(elementoInsertando.getText(), elementoInsertando.getLayoutX() / ESCALA, elementoInsertando.getLayoutY() / ESCALA);
                pane.getChildren().remove(elementoInsertando);
                elementoInsertando = null;
                modelo.add(cuerpoCelesteDestino);
                cambiaEstado(EstadoCRUD.sinSeleccion);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                modelo.borrar(cuerpoCelesteDestino);
            }

            @Override
            public void redo() throws Exception {
                modelo.add(cuerpoCelesteDestino);
            }

            @Override
            public String getEventName() {
                return "Insertar";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (Exception e) {
            ShowError.show(stage, "No se puede insertar", e);
        }
    }

    private void comandoEditar() {
        ICommand command = new ICommand() {
            // Debemos usar estos datos que se apilan, no podemos usar los del controller directamente
            // porque pueden haber inserciones / borrados
            CuerpoCeleste cuerpoCelesteDestino;
            CuerpoCeleste contenidoSinEditar;
            CuerpoCeleste contenidoEditado;
            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                resaltarEditando(elementoSeleccionado, false);
                cuerpoCelesteDestino = elementoSeleccionado.getCuerpoCeleste();
                contenidoSinEditar = cuerpoCelesteOriginal.clone();
                contenidoEditado = cuerpoCelesteDestino.clone();
                cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                modelo.editar(cuerpoCelesteDestino);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                cuerpoCelesteDestino.copia(contenidoSinEditar);
                modelo.editar(cuerpoCelesteDestino);
            }

            @Override
            public void redo() throws Exception {
                cuerpoCelesteDestino.copia(contenidoEditado);
                modelo.editar(cuerpoCelesteDestino);
            }

            @Override
            public String getEventName() {
                return "Editar";
            }
        };
        try {
            commandManager.executeCommand(command);
        } catch (Exception e) {
            ShowError.show(stage, "No se puede editar", e);
        }
    }

    private void comandoBorrar() {
        // No estamos teniendo a propósito en cuenta el orden en que se mostrarán en la lista de CRUDController
        ICommand command = new ICommand() {
            // Debemos usar estos datos que se apilan, no podemos usar los del controller directamente
            // porque pueden haber inserciones / borrados
            CuerpoCeleste cuerpoCelesteDestino;
            CuerpoCelesteView vistaSeleccionada;

            @Override
            public void execute(IObservableTaskRunner observer) throws Exception {
                vistaSeleccionada = elementoSeleccionado;
                cuerpoCelesteDestino = elementoSeleccionado.getCuerpoCeleste();
                doBorrar();
            }

            private void doBorrar() {
                modelo.borrar(cuerpoCelesteDestino);
                elementoSeleccionado = null;
                cambiaEstado(EstadoCRUD.sinSeleccion);
            }

            @Override
            public boolean canBeUndone() {
                return true;
            }

            @Override
            public void undo() throws Exception {
                modelo.add(cuerpoCelesteDestino); // esto desencadena la inserción en esta vista
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
            ShowError.show(stage, "No se puede borrar", e);
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

    private void mover(CuerpoCelesteView elemento, double x, double y) {
        CuerpoCeleste cuerpoCeleste = elemento.getCuerpoCeleste();
        cuerpoCeleste.setX(x/ESCALA);
        cuerpoCeleste.setY(y/ESCALA);
    }

    private void resaltarEditando(CuerpoCelesteView elemento, boolean resaltar) {
        elemento.resaltar(resaltar);
    }

    private void borrar() {
        // véase http://code.makery.ch/blog/javafx-dialogs-official/ para el resto de diálogos
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("cuerpoCelestes");
        alert.setHeaderText("Confirmación");
        alert.setContentText("¿Deseas borrar la cuerpoCeleste " + elementoSeleccionado.getCuerpoCeleste() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            lanzarEvento(new EventoAceptar());
        } else {
            lanzarEvento(new EventoCancelar());
        }

    }

    private void highlight(CuerpoCelesteView elemento, Color color) {
        elemento.highlight(color);
    }
}
