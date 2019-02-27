package org.monarchinitiative.jannovar_sieve.gui.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.monarchinitiative.jannovar_sieve.gui.util.PopUps;
import org.monarchinitiative.jannovar_sieve.gui.util.WidthAwareTextFields;

import java.io.File;
import java.util.concurrent.ExecutorService;

public class MainController {

    private final ExecutorService executorService;

    @FXML
    public Button browseButton;

    @FXML
    public Label status;

    @FXML
    public TextField geneTextField;

    @FXML
    public Button addButton;

    @FXML
    public Button removeButton;

    @FXML
    public ListView<String> geneNamesListView;

    @FXML
    public Button saveSelectionButton;

    private AutoCompletionBinding<String> geneSymbolAutocompletion;

    private ObjectProperty<JannovarData> jannovarDataProperty = new SimpleObjectProperty<>();

    @Inject
    public MainController(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @FXML
    public void browseButtonAction() {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter jtdb = new FileChooser.ExtensionFilter("Jannovar transcript database", "*.ser");
        chooser.getExtensionFilters().add(jtdb);
        chooser.setSelectedExtensionFilter(jtdb);

        String ud = System.getProperty("user.dir");
        chooser.setInitialDirectory(new File(ud));

        chooser.setTitle("Select Jannovar transcript database");
        File chosenDbPath = chooser.showOpenDialog(browseButton.getScene().getWindow());
        if (chosenDbPath == null) {
            return;
        }

        Deserializer deserializer = new Deserializer(chosenDbPath);
        status.textProperty().bind(deserializer.messageProperty());
        deserializer.setOnSucceeded(e -> jannovarDataProperty.set(deserializer.getJannovarData()));

        executorService.submit(deserializer);
    }

    @FXML
    public void addButtonAction() {
        String symbol = geneTextField.getText();
        if (jannovarDataProperty.get().getTmByGeneSymbol().keySet().contains(symbol)) {
            geneNamesListView.getItems().add(symbol);
        } else {
            PopUps.showWarningDialog("Add gene symbol", "Sorry", "Gene symbol '" + symbol + "' is not present in the database");
        }
        geneTextField.clear();
    }


    @FXML
    public void removeButtonAction() {
        final ObservableList<Integer> selected = geneNamesListView.getSelectionModel().getSelectedIndices();
        selected.forEach(i -> geneNamesListView.getItems().remove(i.intValue()));
    }

    @FXML
    public void saveSelectionButtonAction() {
        FileChooser chooser = new FileChooser();

        chooser.setTitle("Save partial Jannovar transcript database");
        chooser.setInitialFileName("smallDb.ser");

        File whereToSave = chooser.showSaveDialog(saveSelectionButton.getScene().getWindow());

        ImmutableList.Builder<TranscriptModel> transcripts = ImmutableList.builder();

        ImmutableMultimap<String, TranscriptModel> tmByGeneSymbol = jannovarDataProperty.get().getTmByGeneSymbol();
        geneNamesListView.getItems().forEach(symbol -> transcripts.addAll(tmByGeneSymbol.get(symbol)));

        JannovarData jannovarData = new JannovarData(jannovarDataProperty.get().getRefDict(), transcripts.build());

        JannovarDataSerializer serializer = new JannovarDataSerializer(whereToSave.getAbsolutePath());
        try {
            serializer.save(jannovarData);
            PopUps.showInfoMessage("Success!", "Save Jannovar database");
        } catch (SerializationException e) {
            PopUps.showException("Save Jannovar database", "Error", "Unable to save the database", e);
        }
    }

    public void initialize() {
        // enable gene symbol suggestions after loading Jannovar transcript database
        jannovarDataProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                final ImmutableSet<String> symbols = newValue.getTmByGeneSymbol().keySet();
                geneSymbolAutocompletion = WidthAwareTextFields.bindWidthAwareAutoCompletion(geneTextField, symbols);
            } else {
                if (geneSymbolAutocompletion != null) {
                    geneSymbolAutocompletion.dispose();
                }
            }
        });

        geneTextField.disableProperty().bind(jannovarDataProperty.isNull());
        addButton.disableProperty().bind(jannovarDataProperty.isNull());
        removeButton.disableProperty().bind(jannovarDataProperty.isNull());
        geneNamesListView.disableProperty().bind(jannovarDataProperty.isNull());
        saveSelectionButton.disableProperty().bind(jannovarDataProperty.isNull());
    }
}

