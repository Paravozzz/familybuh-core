package ru.homebuh.core.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class DataImportServiceTest {

    @Autowired
    private DataImportService dataImportService;

    @Test
    @Disabled
    void importFromDrebedengi() throws IOException {
        this.dataImportService.importFromDrebedengi("c:\\Users\\ASUS\\Downloads\\313214.zip", "0613881c-0e3a-41b0-94be-959f5aa202fa");
    }
}