package org.monarchinitiative.jannovar_sieve.gui;

import com.google.inject.AbstractModule;
import org.monarchinitiative.jannovar_sieve.gui.controllers.MainController;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JannovarSieveModule extends AbstractModule {


    @Override
    protected void configure() {
        bind(ExecutorService.class)
                .toInstance(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));

        bind(MainController.class);


    }
}
