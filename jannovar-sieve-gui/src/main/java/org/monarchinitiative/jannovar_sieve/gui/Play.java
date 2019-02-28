package org.monarchinitiative.jannovar_sieve.gui;

//import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Play extends Application {

    private static final String WINDOW_TITLE = "Jannovar Sieve App";

    private ExecutorService executorService;

    @Override
    public void init() throws Exception {
        super.init();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public void start(Stage window) throws Exception {
        Locale.setDefault(new Locale("en", "US"));

        // Apply CSS
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
//        StyleManager.getInstance().addUserAgentStylesheet("jannovar-sieve-gui.css");

        MainController mainController = new MainController(executorService);

        FXMLLoader loader = new FXMLLoader(MainController.class.getResource("Main.fxml"));
        loader.setControllerFactory(clz -> mainController);
        Parent rootNode = loader.load();
        window.setTitle(WINDOW_TITLE);
        window.setScene(new Scene(rootNode));
        window.show();
    }

    @Override
    public void stop() throws Exception {
        executorService.shutdown();
    }
}
