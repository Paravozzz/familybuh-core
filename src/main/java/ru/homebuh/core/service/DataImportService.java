package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.AccountCreate;
import ru.homebuh.core.controller.dto.AccountSummary;
import ru.homebuh.core.controller.dto.CategoryCreate;
import ru.homebuh.core.controller.dto.OperationCreate;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.UserInfoEntity;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
    private final OperationService operationService;

    @Transactional
    public void importData1(String zipPath, String userId) throws IOException {

        userInfoService.isUserExists(userId);

        //О Ч И С Т К А   П О Л Ь З О В А Т Е Л Ь С К И Х   Д А Н Н Ы Х
        List<UserInfoEntity> family = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyIds = family.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        operationService.deleteAllFamilyOperations(familyIds);
        accountService.deleteAllFamilyAccounts(familyIds);
        family.forEach(userInfoEntity -> userInfoEntity.getCurrencies().clear());
        Set<Long> familyCategotiesIds = new HashSet<>();
        family.forEach(user -> {
            List<CategoryEntity> categories = user.getCategories();
            Set<Long> categotiesIds = categories.stream().map(CategoryEntity::getId).collect(Collectors.toSet());
            familyCategotiesIds.addAll(categotiesIds);
            user.getCategories().clear();
        });
        categoryService.deleteAllCategoriesByIdIn(familyCategotiesIds);
        userInfoService.saveAll(family);

        //Р А С П А К О В К А   А Р Х И В А
        File file = new File(zipPath);
        List<String> fileList = unzip(file, "c:\\homebuhdata\\" + UUID.randomUUID());

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

        //Идентификаторы чужих счетов <чужой id, суммарная информация по счёту>
        Map<Long, AccountSummary> importedAccountsMap = new HashMap<>();

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

                String accountName = getUniqueName(familyAccountNames, name);

                AccountSummary createdAccount = accountService.create(userId, AccountCreate.builder().name(accountName).build());

                familyAccountNames.add(accountName);
                importedAccountsMap.put(importedAccountId, createdAccount);
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

                String categoryName = getUniqueName(familyExpCategories, name);

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

                String categoryName = getUniqueName(familyIncCategories, name);

                CategoryEntity createdCategory = categoryService.create(userId, new CategoryCreate(categoryName, true));

                familyIncCategories.add(categoryName);
                importedIncomeCategoriesMap.put(importedCategoryId, createdCategory.getId());
            });
        }

        //И М П О Р Т   Д О Х О Д О В

        String incPath = fileList.stream()
                .filter(path -> path.endsWith("income.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required income.txt"));

        try (Stream<String> expLines = Files.lines(Paths.get(incPath), StandardCharsets.UTF_8)) {
            expLines.sequential().forEach(line -> {
                OperationCreate operationCreate = getOperationCreate(userId, importedCurrenciesMap, importedAccountsMap, importedIncomeCategoriesMap, line);
                operationService.incomeCreate(userId, operationCreate);
            });
        }

        //И М П О Р Т   Р А С Х О Д О В
        String expPath = fileList.stream()
                .filter(path -> path.endsWith("expense.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required expense.txt"));

        try (Stream<String> expLines = Files.lines(Paths.get(expPath), StandardCharsets.UTF_8)) {
            expLines.sequential().forEach(line -> {
                OperationCreate operationCreate = getOperationCreate(userId, importedCurrenciesMap, importedAccountsMap, importedExpenceCategoriesMap, line);
                operationService.expenseCreate(userId, operationCreate);
            });
        }

        //И М П О Р Т   П Е Р Е М Е Щ Е Н И Й
        String transferPath = fileList.stream()
                .filter(path -> path.endsWith("transfer.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required transfer.txt"));

        //И М П О Р Т   О Б М Е Н О В   В А Л Ю Т Ы
        String exchangePath = fileList.stream()
                .filter(path -> path.endsWith("exchange.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required exchange.txt"));
    }

    private static String getUniqueName(Set<String> existentNames, String name) {
        String uniqueName = name;
        int nameIndex = 1;
        while (existentNames.contains(uniqueName)) {
            uniqueName = name + nameIndex;
            nameIndex++;
        }
        return uniqueName;
    }

    private static OperationCreate getOperationCreate(String userId, Map<Long, String> importedCurrenciesMap, Map<Long, AccountSummary> importedAccountsMap, Map<Long, Long> importedCategoriesMap, String line) {
        String[] split = line.split(";");
        String amount = split[6].trim().replace("\"", "").replace(",", ".");
        Long accountOldId = Long.valueOf(split[7].trim().replace("\"", ""));
        AccountSummary accountSummary = importedAccountsMap.get(accountOldId);
        Long currencyOldId = Long.valueOf(split[4].trim().replace("\"", ""));
        String currencyCode = importedCurrenciesMap.get(currencyOldId);
        Long accountId = accountSummary.getInitialBalance().stream().filter(s -> s.getCurrencyCode().equalsIgnoreCase(currencyCode)).findFirst().get().getAccountId();
        String categoryOldIdStr = split[5].trim().replace("\"", "");

        //TODO: если categoryOldIdStr = "-1", то это начальный остаток по счёту

        Long categoryOldId = Long.valueOf(categoryOldIdStr);
        Long categoryId = importedCategoriesMap.get(categoryOldId);
        String description = split[3].trim().replace("\"", "");
        String dateOld = split[2].trim().replace("\"", "").replace(" ", "T");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDate = LocalDateTime.parse(dateOld, formatter);
        OffsetDateTime date = OffsetDateTime.of(localDate, ZoneOffset.UTC);
        return new OperationCreate(amount, currencyCode, accountId, categoryId, description, date, userId);
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
