package org.monarchinitiative.jannovar_sieve.gui;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.monarchinitiative.jannovar_sieve.gui.controllers.MainController;

import java.util.Locale;
import java.util.concurrent.ExecutorService;

public class Play extends Application {

    private static final String WINDOW_TITLE = "Jannovar Sieve App";

    private Injector injector;

    public void start(Stage window) throws Exception {
        Locale.setDefault(new Locale("en", "US"));

        // Apply CSS
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        StyleManager.getInstance().addUserAgentStylesheet("jannovar-sieve-gui.css");

        injector = Guice.createInjector(new JannovarSieveModule());
        Parent rootNode = FXMLLoader.load(MainController.class.getResource("Main.fxml"), null,
                new JavaFXBuilderFactory(), injector::getInstance);
        window.setTitle(WINDOW_TITLE);
        window.setScene(new Scene(rootNode));
        window.show();
    }

    @Override
    public void stop() throws Exception {
        ExecutorService executorService = injector.getInstance(ExecutorService.class);
        executorService.shutdown();
    }
}
