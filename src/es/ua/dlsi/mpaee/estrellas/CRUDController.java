package es.ua.dlsi.mpaee.estrellas;

import es.ua.dlsi.mpaee.estrellas.eventos.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CRUDController implements Initializable {
    @FXML
    ListView<Estrella> lvEstrellas;

    @FXML
    Button btnBorrar;

    @FXML
    Button btnInsertar;

    @FXML
    Button btnCancelar;

    @FXML
    Button btnGuardar;

    @FXML
    TextField inputNombre;

    @FXML
    Slider sliderX;

    @FXML
    Slider sliderY;

    @FXML
    Label labelX;

    @FXML
    Label labelY;

    @FXML
    VBox vboxLista;

    @FXML
    HBox panelBotonesEdicion;

    @FXML
    GridPane formulario;

    Estrella estrellaEnFormulario;

    final Modelo modelo;

    /**
     * Lo usaremos más adelante con el patrón Comando
     */
    Estrella estrellaOriginal;

    /**
     * Estado de la vista (aún muy simple)
     */
    EstadoCRUD estadoCRUD;
    private Estrella ultimoElementoSeleccionado;


    // En el constructor no podemos trabajar con elementos aún no inyectados de la vista
    public CRUDController() {
        modelo = Modelo.getInstance();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sliderX.setMin(0);
        sliderY.setMin(0);
        sliderX.setMax(Constantes.MAX_COORDENADA_X);
        sliderX.setBlockIncrement(0.1);
        sliderY.setBlockIncrement(0.1);
        sliderX.setSnapToTicks(true);
        sliderY.setSnapToTicks(true);
        sliderY.setMax(Constantes.MAX_COORDENADA_Y);

        // estados
        cambiaEstado(EstadoCRUD.sinSeleccion);

        // bindings
        lvEstrellas.itemsProperty().bind(modelo.estrellasProperty()); // no bidireccional para que se centralice en modelo

        // formatos en https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
        String formato = "%+.1f";
        labelX.textProperty().bind(sliderX.valueProperty().asString(formato));
        labelY.textProperty().bind(sliderY.valueProperty().asString(formato));

        // interacción
        estrellaEnFormulario = null;

        lvEstrellas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Estrella>() {
            @Override
            public void changed(ObservableValue<? extends Estrella> observable, Estrella oldValue, Estrella newValue) {
                if (newValue == null) {
                    lanzarEvento(new EventoElementoDeseleccionado());
                } else {
                    lanzarEvento(new EventoElementoSeleccionado<>(newValue));
                }
            }
        });
        inputNombre.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                lanzarEvento(new EventoValorCambiado(estrellaEnFormulario));
            }
        });

        sliderX.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                lanzarEvento(new EventoValorCambiado(estrellaEnFormulario));
            }
        });
        sliderY.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                lanzarEvento(new EventoValorCambiado(estrellaEnFormulario));
            }
        });
    }

    private void cargaEstrella(Estrella estrella) {

        if (estrellaEnFormulario != null) {
            estrellaEnFormulario.nombreProperty().unbindBidirectional(inputNombre.textProperty());
            estrellaEnFormulario.xProperty().unbindBidirectional(sliderX.valueProperty());
            estrellaEnFormulario.yProperty().unbindBidirectional(sliderY.valueProperty());
        }

        if (estrella != null) {
            inputNombre.textProperty().bindBidirectional(estrella.nombreProperty());
            sliderX.valueProperty().bindBidirectional(estrella.xProperty());
            sliderY.valueProperty().bindBidirectional(estrella.yProperty());
        }
        estrellaEnFormulario = estrella;

    }

    @FXML
    private void handleInsertar() {
        lanzarEvento(new EventoInsertar());
    }

    @FXML
    private void handleGuardar() {
        lanzarEvento(new EventoGuardar());
    }

    @FXML
    private void handleCancelar() {
        lanzarEvento(new EventoCancelar());
    }

    @FXML
    private void handleBorrar() {
        lanzarEvento(new EventoBorrar());
        // véase http://code.makery.ch/blog/javafx-dialogs-official/ para el resto de diálogos
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Estrellas");
        alert.setHeaderText("Confirmación");
        alert.setContentText("¿Deseas borrar la estrella " + estrellaEnFormulario + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            lvEstrellas.getItems().remove(estrellaEnFormulario);
            lanzarEvento(new EventoAceptar());
        } else {
            lanzarEvento(new EventoCancelar());
        }
    }

    private void vaciarFormulario() {
        if (estrellaEnFormulario != null) {
            estrellaEnFormulario.nombreProperty().unbindBidirectional(inputNombre.textProperty());
            estrellaEnFormulario.xProperty().unbindBidirectional(sliderX.valueProperty());
            estrellaEnFormulario.yProperty().unbindBidirectional(sliderY.valueProperty());
            estrellaEnFormulario = null;
            inputNombre.setText("");
            sliderX.setValue(0);
            sliderY.setValue(0);
        }
    }



    private void deselecciona() {
        if (lvEstrellas.getSelectionModel().getSelectedItem() != null) {
            lvEstrellas.getSelectionModel().clearSelection();
        }
    }

    private void selecciona(Estrella estrella) {
        lvEstrellas.getSelectionModel().select(estrella);
    }

    // Los estados de botones y panel podríamos gestionarlos con binding,
    private void habilitarBotonesGuardarCancelar(boolean habilitar) {
        btnGuardar.setDisable(!habilitar);
        btnCancelar.setDisable(!habilitar);
    }

    private void habilitarPanelConsulta(boolean habilitar) {
        vboxLista.setDisable(!habilitar);
    }

    private void habilitarBotonBorrar(boolean habilitar) {
        btnBorrar.setDisable(!habilitar);
    }

    private void habilitarFormulario(boolean habilitar) {
        formulario.setDisable(!habilitar);
    }

    //// ------ Máquina de estados ----
    public void cambiaEstado(EstadoCRUD estado) {
        switch (estado) {
            case sinSeleccion:
                deselecciona();
                habilitarBotonBorrar(false);
                habilitarPanelConsulta(true);
                habilitarBotonesGuardarCancelar(false);
                vaciarFormulario();
                habilitarFormulario(false);
                break;
            case consultadoSeleccionado:
                habilitarPanelConsulta(true);
                habilitarBotonBorrar(true);
                habilitarFormulario(true);
                break;
            case insertando:
                ultimoElementoSeleccionado = estrellaEnFormulario;
                habilitarPanelConsulta(false);
                habilitarBotonesGuardarCancelar(true);
                habilitarFormulario(true);
                inputNombre.requestFocus();
                cargaEstrella(new Estrella());
                break;
            case editando:
                estrellaOriginal = new Estrella(estrellaEnFormulario);
                habilitarPanelConsulta(false);
                habilitarBotonesGuardarCancelar(true);
                habilitarPanelConsulta(false);
                break;
        }
        this.estadoCRUD = estado;
    }

    public void lanzarEvento(Evento<Estrella> evento) {
        switch (this.estadoCRUD) {
            case sinSeleccion:
                if (evento instanceof EventoElementoSeleccionado) {
                    cargaEstrella(evento.getElemento());
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                } else if (evento instanceof EventoInsertar) {
                    cambiaEstado(EstadoCRUD.insertando);
                }
                break;
            case consultadoSeleccionado:
                if (evento instanceof EventoElementoSeleccionado) {
                    cargaEstrella(evento.getElemento());
                } else if (evento instanceof EventoElementoDeseleccionado) {
                    cargaEstrella(null);
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                } else if (evento instanceof EventoBorrar) {
                    cambiaEstado(EstadoCRUD.borrando);
                } else if (evento instanceof EventoInsertar) {
                    cambiaEstado(EstadoCRUD.insertando);
                } else if (evento instanceof EventoValorCambiado) {
                    cambiaEstado(EstadoCRUD.editando);
                }
                break;
            case borrando:
                if (evento instanceof EventoAceptar) {
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                } else if (evento instanceof EventoCancelar) {
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                }
                break;
            case insertando:
                if (evento instanceof EventoGuardar) {
                    Estrella elementoInsertando = estrellaEnFormulario;
                    modelo.add(elementoInsertando);
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                    selecciona(elementoInsertando); // esto lanzará el evento de selección
                } else if (evento instanceof EventoCancelar) {
                    cambiaEstado(EstadoCRUD.sinSeleccion);
                    selecciona(ultimoElementoSeleccionado); // esto lanzará el evento de selección si no es nulo
                }
                break;
            case editando:
                if (evento instanceof EventoGuardar) {
                    modelo.editar(estrellaEnFormulario);
                    lvEstrellas.refresh(); // para que repinte el valor cambiado (el toString que usa el ListView no es observable)
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                } else if (evento instanceof EventoCancelar) {
                    estrellaEnFormulario.copia(estrellaOriginal);
                    cambiaEstado(EstadoCRUD.consultadoSeleccionado);
                }
                break;
        }
    }
}
