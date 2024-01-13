package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class DataImportService {

    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final UserInfoService userInfoService;
    private final CategoryService categoryService;

    @Transactional
    public void importFromDrebedengi(String zipPath, String userId) throws IOException {

        userInfoService.isUserExists(userId);

        File file = new File(zipPath);
        List<String> fileList = unzip(file, "c:\\drebedengi\\" + UUID.randomUUID());

        //И М П О Р Т   В А Л Ю Т

        //Идентификаторы чужих валют <чужой id, код валюты>
        Map<Long, String> importedCurrenciesMap = new HashMap<>();

        String currencyPath = fileList.stream()
                .filter(path -> path.endsWith("currency.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required currency.txt"));

        try (Stream<String> currencyLines = Files.lines(Paths.get(currencyPath), StandardCharsets.UTF_8)) {
            currencyLines.forEach(line -> {
                String[] split = line.split(";");

                String currencyCode = split[0];
                Long importedCurrencyId = Long.valueOf(split[2].trim().replace("\"", ""));

                currencyCode = currencyCode.trim().replace("\"", "").toUpperCase();

                currencyService.attachCurrencyToUser(userId, currencyCode);

                importedCurrenciesMap.put(importedCurrencyId, currencyCode);

            });
        }

        //И М П О Р Т   С Ч Е Т О В

        //Идентификаторы чужих счетов <чужой id, код валюты>
        Map<Long, String> importedAccountsMap = new HashMap<>();

        String accountPath = fileList.stream()
                .filter(path -> path.endsWith("account_category.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required account_category.txt"));

        Set<String> familyAccountNames = accountService.findAllFamilyAccountsByUserId(userId).stream().map(AccountEntity::getName).collect(Collectors.toSet());

        try (Stream<String> accountLines = Files.lines(Paths.get(accountPath), StandardCharsets.UTF_8)) {
            accountLines.sequential().forEach(line -> {
                String[] split = line.split(";");
                String name = split[0].trim().replace("\"", "");
                Long importedAccountId = Long.valueOf(split[1].trim().replace("\"", ""));

                String accountName = name;
                int nameIndex = 1;
                while (familyAccountNames.contains(accountName)) {
                    accountName = name + nameIndex;
                    nameIndex++;
                }

                accountService.create(userId, AccountCreate.builder().name(accountName).build());

                familyAccountNames.add(accountName);
                importedAccountsMap.put(importedAccountId, accountName);
            });
        }

        //И М П О Р Т   Р А С Х О Д Н Ы Х   К А Т Е Г О Р И Й

        //Идентификаторы чужих категорий <чужой id, наш id>
        Map<Long, Long> importedExpenceCategoriesMap = new HashMap<>();
        String expCategoriesPath = fileList.stream()
                .filter(path -> path.endsWith("expense_category.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required expense_category.txt"));

        Set<String> familyExpCategories = categoryService.findAllFamilyCategoriesByUserId(userId).stream()
                .filter(c -> !c.isIncome())
                .map(CategoryEntity::getName)
                .collect(Collectors.toSet());

        try (Stream<String> expCatLines = Files.lines(Paths.get(expCategoriesPath), StandardCharsets.UTF_8)) {
            expCatLines.sequential().forEach(line -> {
                String[] split = line.split(";");
                String name = split[0].trim().replace("\"", "");
                Long importedCategoryId = Long.valueOf(split[1].trim().replace("\"", ""));

                String categoryName = name;
                int nameIndex = 1;
                while (familyExpCategories.contains(categoryName)) {
                    categoryName = name + nameIndex;
                    nameIndex++;
                }

                CategoryEntity createdCategory = categoryService.create(userId, new CategoryCreate(categoryName, false));

                familyExpCategories.add(categoryName);
                importedExpenceCategoriesMap.put(importedCategoryId, createdCategory.getId());
            });
        }

        //И М П О Р Т   Д О Х О Д Н Ы Х   К А Т Е Г О Р И Й

        //Идентификаторы чужих категорий <чужой id, наш id>
        Map<Long, Long> importedIncomeCategoriesMap = new HashMap<>();
        String incCategoriesPath = fileList.stream()
                .filter(path -> path.endsWith("income_category.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required income_category.txt"));

        Set<String> familyIncCategories = categoryService.findAllFamilyCategoriesByUserId(userId).stream()
                .filter(CategoryEntity::isIncome)
                .map(CategoryEntity::getName)
                .collect(Collectors.toSet());

        try (Stream<String> incCatLines = Files.lines(Paths.get(incCategoriesPath), StandardCharsets.UTF_8)) {
            incCatLines.sequential().forEach(line -> {
                String[] split = line.split(";");
                String name = split[0].trim().replace("\"", "");
                Long importedCategoryId = Long.valueOf(split[1].trim().replace("\"", ""));

                String categoryName = name;
                int nameIndex = 1;
                while (familyIncCategories.contains(categoryName)) {
                    categoryName = name + nameIndex;
                    nameIndex++;
                }

                CategoryEntity createdCategory = categoryService.create(userId, new CategoryCreate(categoryName, true));

                familyIncCategories.add(categoryName);
                importedIncomeCategoriesMap.put(importedCategoryId, createdCategory.getId());
            });
        }

        //И М П О Р Т   Д О Х О Д О В
        //И М П О Р Т   Р А С Х О Д О В
        //И М П О Р Т   П Е Р Е М Е Щ Е Н И Й
        //И М П О Р Т   О Б М Е Н О В   В А Л Ю Т Ы

    }

    public static List<String> unzip(File source, String out) throws IOException {
        List<String> fileList = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {

            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {

                File file = new File(out, entry.getName());

                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    fileList.add(file.getAbsolutePath());
                    File parent = file.getParentFile();

                    if (!parent.exists()) {
                        parent.mkdirs();
                    }

                    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {

                        int bufferSize = Math.toIntExact(entry.getSize());
                        byte[] buffer = new byte[bufferSize > 0 ? bufferSize : 1];
                        int location;

                        while ((location = zis.read(buffer)) != -1) {
                            bos.write(buffer, 0, location);
                        }
                    }
                }
                entry = zis.getNextEntry();
            }
        }
        return fileList;
    }
}
