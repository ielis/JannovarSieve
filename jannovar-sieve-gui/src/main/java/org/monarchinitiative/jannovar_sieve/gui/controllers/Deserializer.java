package org.monarchinitiative.jannovar_sieve.gui.controllers;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import javafx.concurrent.Task;

import java.io.File;

public class Deserializer extends Task<Void> {

    private final File dbPath;

    private JannovarData jannovarData;

    public Deserializer(File dbPath) {
        this.dbPath = dbPath;
    }

    public JannovarData getJannovarData() {
        return jannovarData;
    }

    @Override
    protected Void call() throws Exception {
        updateMessage("Deserializing " + dbPath.getName());
        updateProgress(-1, 1);
        JannovarDataSerializer serializer = new JannovarDataSerializer(dbPath.getAbsolutePath());
        jannovarData = serializer.load();

        updateMessage("Success!");
        updateProgress(1, 1);
        return null;
    }
}
