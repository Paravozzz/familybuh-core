package ru.homebuh.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    @Autowired
    private DataImportService dataImportService;

    @Override
    public void run(String... args) throws Exception {

        this.dataImportService.importData1("c:\\homebuhdata\\313214.zip", "0613881c-0e3a-41b0-94be-959f5aa202fa");

    }
}
