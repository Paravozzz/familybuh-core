package ru.homebuh.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.homebuh.core.controller.dto.*;
import ru.homebuh.core.domain.AccountEntity;
import ru.homebuh.core.domain.CategoryEntity;
import ru.homebuh.core.domain.UserInfoEntity;
import ru.homebuh.core.mapper.AccountMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final AccountMapper accountMapper;
    private final CurrencyService currencyService;
    private final UserInfoService userInfoService;
    private final CategoryService categoryService;
    private final OperationService operationService;
    private final TransferService transferService;

    @Transactional
    public void importData1(String zipPath, String userId) throws IOException {

        userInfoService.isUserExists(userId);

        //О Ч И С Т К А   П О Л Ь З О В А Т Е Л Ь С К И Х   Д А Н Н Ы Х
        List<UserInfoEntity> family = userInfoService.findAllFamilyMembers(userId);
        Set<String> familyIds = family.stream().map(UserInfoEntity::getId).collect(Collectors.toSet());
        transferService.deleteAllFamilyTransfers(familyIds);
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
        File zipFile = new File(zipPath);
        List<String> fileList = unzip(zipFile);

        //И М П О Р Т   В А Л Ю Т
        String currencyPath = fileList.stream()
                .filter(path -> path.endsWith("currency.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required currency.txt"));

        //Идентификаторы чужих валют <чужой id, код валюты>
        Map<Long, String> importedCurrenciesMap = new HashMap<>();

        try (Stream<String> currencyLines = Files.lines(Paths.get(currencyPath), StandardCharsets.UTF_8)) {
            currencyLines
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        String[] split = line.split(";");

                        String currencyCode = split[0];
                        Long importedCurrencyId = Long.valueOf(split[2].trim().replace("\"", ""));

                        currencyCode = currencyCode.trim().replace("\"", "").toUpperCase();

                        currencyService.attachCurrencyToUser(userId, currencyCode);

                importedCurrenciesMap.put(importedCurrencyId, currencyCode);

            });
        }

        //И М П О Р Т   С Ч Е Т О В
        String accountsPath = fileList.stream()
                .filter(path -> path.endsWith("account_category.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required account_category.txt"));

        Set<String> familyAccountNames = accountService.findAllFamilyAccountsByUserId(userId).stream().map(AccountEntity::getName).collect(Collectors.toSet());

        //Идентификаторы чужих счетов <чужой id, суммарная информация по счёту>
        Map<Long, AccountSummary> importedAccountsMap = new HashMap<>();

        try (Stream<String> accounts = Files.lines(Paths.get(accountsPath), StandardCharsets.UTF_8)) {
            accounts.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        String[] split = line.split(";");
                        String name = split[0].trim().replace("\"", "");
                        Long importedAccountId = Long.valueOf(split[1].trim().replace("\"", ""));

                        String accountName = getUniqueName(familyAccountNames, name);

                        AccountSummary createdAccount = accountService.create(userId, new AccountCreate(accountName, "", Collections.emptyList()));

                        familyAccountNames.add(accountName);
                importedAccountsMap.put(importedAccountId, createdAccount);
            });
        }

        //И М П О Р Т   Р А С Х О Д Н Ы Х   К А Т Е Г О Р И Й
        String expCategoriesPath = fileList.stream()
                .filter(path -> path.endsWith("expense_category.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required expense_category.txt"));

        Set<String> familyExpenseCategories = categoryService.findAllFamilyCategoriesByUserId(userId).stream()
                .filter(c -> !c.isIncome())
                .map(CategoryEntity::getName)
                .collect(Collectors.toSet());

        //Идентификаторы чужих категорий <чужой id, наш id>
        Map<Long, Long> importedExpenseCategoriesMap = new HashMap<>();

        try (Stream<String> expenseCategories = Files.lines(Paths.get(expCategoriesPath), StandardCharsets.UTF_8)) {
            expenseCategories.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        String[] split = line.split(";");
                        String name = split[0].trim().replace("\"", "");
                        Long importedCategoryId = Long.valueOf(split[1].trim().replace("\"", ""));

                        String categoryName = getUniqueName(familyExpenseCategories, name);

                        CategoryEntity createdCategory = categoryService.create(userId, new CategoryCreate(categoryName, false));

                        familyExpenseCategories.add(categoryName);
                        importedExpenseCategoriesMap.put(importedCategoryId, createdCategory.getId());
                    });
        }

        //И М П О Р Т   Д О Х О Д Н Ы Х   К А Т Е Г О Р И Й
        String incCategoriesPath = fileList.stream()
                .filter(path -> path.endsWith("income_category.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required income_category.txt"));

        Set<String> familyIncomeCategories = categoryService.findAllFamilyCategoriesByUserId(userId).stream()
                .filter(CategoryEntity::isIncome)
                .map(CategoryEntity::getName)
                .collect(Collectors.toSet());

        //Идентификаторы чужих категорий <чужой id, наш id>
        Map<Long, Long> importedIncomeCategoriesMap = new HashMap<>();

        try (Stream<String> incCatLines = Files.lines(Paths.get(incCategoriesPath), StandardCharsets.UTF_8)) {
            incCatLines.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        String[] split = line.split(";");
                        String name = split[0].trim().replace("\"", "");
                        Long importedCategoryId = Long.valueOf(split[1].trim().replace("\"", ""));

                        String categoryName = getUniqueName(familyIncomeCategories, name);

                        CategoryEntity createdCategory = categoryService.create(userId, new CategoryCreate(categoryName, true));

                        familyIncomeCategories.add(categoryName);
                        importedIncomeCategoriesMap.put(importedCategoryId, createdCategory.getId());
                    });
        }

        //И М П О Р Т   Д О Х О Д О В

        String incomesPath = fileList.stream()
                .filter(path -> path.endsWith("income.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required income.txt"));

        try (Stream<String> incomes = Files.lines(Paths.get(incomesPath), StandardCharsets.UTF_8)) {
            incomes.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        OperationCreate operationCreate = getOperationCreate(importedCurrenciesMap, importedAccountsMap, importedIncomeCategoriesMap, line);
                        //если categoryId = null, то это начальный остаток по счёту
                        if (operationCreate.getCategoryId() == null) {
                            Long accountId = operationCreate.getAccountId();
                            AccountSummary accountSummary = accountService.findAccountSummaryByAccountId(accountId);
                            AccountUpdate accountUpdate = accountMapper.mapSummaryToUpdate(accountSummary);
                            accountUpdate.getInitialBalance().stream()
                                    .filter(accountBalanceUpdate -> accountBalanceUpdate.getAccountId().equals(accountId))
                                    .findFirst()
                                    .get()
                                    .setAmount(operationCreate.getAmount());
                            accountService.update(userId, accountUpdate);
                            return;
                        }

                        operationService.createIncome(userId, operationCreate);
                    });
        }

        //И М П О Р Т   Р А С Х О Д О В
        String expensesPath = fileList.stream()
                .filter(path -> path.endsWith("expense.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required expense.txt"));

        try (Stream<String> expenses = Files.lines(Paths.get(expensesPath), StandardCharsets.UTF_8)) {
            expenses.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        OperationCreate operationCreate = getOperationCreate(importedCurrenciesMap, importedAccountsMap, importedExpenseCategoriesMap, line);
                        operationService.createExpense(userId, operationCreate);
                    });
        }

        //И М П О Р Т   П Е Р Е М Е Щ Е Н И Й
        String transferPath = fileList.stream()
                .filter(path -> path.endsWith("transfer.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required transfer.txt"));

        try (Stream<String> transfers = Files.lines(Paths.get(transferPath), StandardCharsets.UTF_8)) {
            transfers.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                        TransferCreate transferCreate = getTransferCreate(importedCurrenciesMap, importedAccountsMap, line);
                        transferService.transferCreate(userId, transferCreate);
                    });
        }

        //И М П О Р Т   О Б М Е Н О В   В А Л Ю Т Ы
        String exchangePath = fileList.stream()
                .filter(path -> path.endsWith("exchange.txt"))
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Not found required exchange.txt"));

        try (Stream<String> exchanges = Files.lines(Paths.get(exchangePath), StandardCharsets.UTF_8)) {
            exchanges.sequential()
                    .filter(line -> !line.isBlank())
                    .forEach(line -> {

                    });
        }
    }

    private static String getUniqueName(Set<String> existentNames, String name) {
        String uniqueName = name;
        int nameIndex = 1;
        while (existentNames.contains(uniqueName)) {
            uniqueName = name + "(" + nameIndex + ")";
            nameIndex++;
        }
        return uniqueName;
    }

    private static OperationCreate getOperationCreate(Map<Long, String> importedCurrenciesMap, Map<Long, AccountSummary> importedAccountsMap, Map<Long, Long> importedCategoriesMap, String line) {
        String[] split = line.split(";");
        String amount = split[6].trim().replace("\"", "").replace(",", ".");
        Long accountOldId = Long.valueOf(split[7].trim().replace("\"", ""));
        AccountSummary accountSummary = importedAccountsMap.get(accountOldId);
        Long currencyOldId = Long.valueOf(split[4].trim().replace("\"", ""));
        String currencyCode = importedCurrenciesMap.get(currencyOldId);
        Long accountId = accountSummary.getInitialBalance().stream().filter(s -> s.getCurrencyCode().equalsIgnoreCase(currencyCode)).findFirst().get().getAccountId();
        String categoryOldIdStr = split[5].trim().replace("\"", "");
        Long categoryOldId = Long.valueOf(categoryOldIdStr);
        Long categoryId = importedCategoriesMap.get(categoryOldId);
        String description = split[3].trim().replace("\"", "");
        String dateOld = split[2].trim().replace("\"", "").replace(" ", "T");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDate = LocalDateTime.parse(dateOld, formatter);
        OffsetDateTime date = OffsetDateTime.of(localDate, ZoneOffset.UTC);
        return new OperationCreate(amount, currencyCode, accountId, categoryId, description, date);
    }

    public static List<String> unzip(File source) throws IOException {
        Path out = Files.createTempDirectory("homebuh");
        List<String> fileList = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {

            ZipEntry entry = zis.getNextEntry();

            while (entry != null) {

                File file = new File(out.toAbsolutePath().toString(), entry.getName());

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

    private static TransferCreate getTransferCreate(Map<Long, String> importedCurrenciesMap, Map<Long, AccountSummary> importedAccountsMap, String line) {
        String[] split = line.split(";");

        String amount = split[2].trim().replace("\"", "").replace(",", ".");

        Long expenseAccountOldId = Long.valueOf(split[8].trim().replace("\"", ""));
        AccountSummary expenseAccountSummary = importedAccountsMap.get(expenseAccountOldId);

        Long incomeAccountOldId = Long.valueOf(split[7].trim().replace("\"", ""));
        AccountSummary incomeAccountSummary = importedAccountsMap.get(incomeAccountOldId);

        Long currencyOldId = Long.valueOf(split[6].trim().replace("\"", ""));
        String currencyCode = importedCurrenciesMap.get(currencyOldId);

        Long expenseAccountId = expenseAccountSummary.getInitialBalance().stream().filter(s -> s.getCurrencyCode().equalsIgnoreCase(currencyCode)).findFirst().get().getAccountId();
        Long incomeAccountId = incomeAccountSummary.getInitialBalance().stream().filter(s -> s.getCurrencyCode().equalsIgnoreCase(currencyCode)).findFirst().get().getAccountId();

        String description = split[5].trim().replace("\"", "");

        String dateOld = split[4].trim().replace("\"", "").replace(" ", "T");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime localDate = LocalDateTime.parse(dateOld, formatter);
        OffsetDateTime date = OffsetDateTime.of(localDate, ZoneOffset.ofHours(0));

        return new TransferCreate(amount, expenseAccountId, incomeAccountId, description, date);
    }
}
