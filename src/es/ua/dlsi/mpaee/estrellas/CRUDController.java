package es.ua.dlsi.mpaee.estrellas;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

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

    /**
     * Para que cuando cancelemos una inserción volvamos al estado anterior
     */
    Estrella ultimaEstrellaSeleccionada;

    final Modelo modelo;

    /**
     * Lo usaremos más adelante con el patrón Comando
     */
    Estrella estrellaOriginal;

    /**
     * Por comodidad al desarrollar
     */
    Estrella estrellaSeleccionada;

    /**
     * Estado de la vista (aún muy simple)
     */
    BooleanProperty insertando;
    BooleanProperty editando;

    // En el constructor no podemos trabajar con elementos aún no inyectados de la vista
    public CRUDController() {
        modelo = Modelo.getInstance();
        insertando = new SimpleBooleanProperty(false);
        editando = new SimpleBooleanProperty(false);
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

        // bindings
        lvEstrellas.itemsProperty().bind(modelo.estrellasProperty()); // no bidireccional para que se centralice en modelo
        btnGuardar.disableProperty().bind(insertando.not().and(editando.not()));
        btnCancelar.disableProperty().bind(btnGuardar.disableProperty());

        // formatos en https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html
        String formato = "%+.1f";
        labelX.textProperty().bind(sliderX.valueProperty().asString(formato));
        labelY.textProperty().bind(sliderY.valueProperty().asString(formato));

        // interacción
        btnBorrar.disableProperty().bind(lvEstrellas.getSelectionModel().selectedItemProperty().isNull());
        estrellaSeleccionada = null;

        lvEstrellas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Estrella>() {
            @Override
            public void changed(ObservableValue<? extends Estrella> observable, Estrella oldValue, Estrella newValue) {
                if (newValue != null) {
                    ultimaEstrellaSeleccionada = newValue;
                }
                //TO-DO Cancelar al cambiar la selección
                /*if (editando.or(insertando).get()) {
                    handleCancelar();
                }*/
                cargaEstrella(newValue);
            }
        });
        inputNombre.setOnKeyTyped(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                editar();
            }
        });

        sliderX.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                editar();
            }
        });
        sliderY.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                editar();
            }
        });
    }

    private void editar() {
        if (!editando.get()) {
            estrellaOriginal = new Estrella(estrellaSeleccionada);
            editando.setValue(true);
        }
    }

    private void cargaEstrella(Estrella estrella) {
        if (estrellaSeleccionada != null) {
            estrellaSeleccionada.nombreProperty().unbindBidirectional(inputNombre.textProperty());
            estrellaSeleccionada.xProperty().unbindBidirectional(sliderX.valueProperty());
            estrellaSeleccionada.yProperty().unbindBidirectional(sliderY.valueProperty());
        }

        if (estrella != null) {
            inputNombre.textProperty().bindBidirectional(estrella.nombreProperty());
            sliderX.valueProperty().bindBidirectional(estrella.xProperty());
            sliderY.valueProperty().bindBidirectional(estrella.yProperty());
        }
        estrellaSeleccionada = estrella;

    }

    @FXML
    private void handleInsertar() {
        lvEstrellas.getSelectionModel().clearSelection();
        inputNombre.requestFocus();
        lvEstrellas.getItems().add(new Estrella());
        lvEstrellas.getSelectionModel().selectLast();
        insertando.setValue(true); // debemos ponerlo al final para que no cancele en el listener de lvEstrellas
    }

    @FXML
    private void handleGuardar() {
        lvEstrellas.refresh();
        insertando.setValue(false);
        editando.setValue(false);
    }

    @FXML
    private void handleCancelar() {
        if (insertando.get()) {
            lvEstrellas.getItems().remove(this.estrellaSeleccionada);
            insertando.setValue(false);
        } else if (editando.get()) {
            estrellaSeleccionada.copia(this.estrellaOriginal);
            editando.set(false);
        }
    }

    @FXML
    private void handleBorrar() {
        // véase http://code.makery.ch/blog/javafx-dialogs-official/ para el resto de diálogos
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Estrellas");
        alert.setHeaderText("Confirmación");
        alert.setContentText("¿Deseas borrar la estrella " + estrellaSeleccionada + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            // ... user chose OK
            lvEstrellas.getItems().remove(estrellaSeleccionada);
        } else {
            //no-op
        }
    }
}
