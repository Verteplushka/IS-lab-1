package bean;

import entity.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import lombok.Getter;
import lombok.Setter;
import service.ImportHistoryService;
import service.ProductService;
import util.CsvParser;

import java.io.*;
import java.util.List;

@Named
@SessionScoped
@Getter
@Setter
public class ImportBean implements Serializable {

    @Inject
    private ImportHistoryService importHistoryService;
    @Inject
    private ProductService productService;
    @Inject
    private UserBean userBean;

    private DataModel<ImportHistory> importHistory;
    private String username;

    public List<ImportHistory> getImportHistory() {
        List<ImportHistory> history = importHistoryService.getImportHistoryForUser(username);  // Получаем историю для пользователя
        importHistory = new ListDataModel<>(history);
        return history;
    }

    public List<ImportHistory> getAllImportHistory() {
        List<ImportHistory> history = importHistoryService.getAllImportHistory();  // Получаем всю историю для админа
        importHistory = new ListDataModel<>(history);
        return history;
    }

    private Part file;

    public void importProducts() throws IOException {
        InputStream fileContent = file.getInputStream();

        // Преобразуем InputStream в строку
        BufferedReader reader = new BufferedReader(new InputStreamReader(fileContent));
        StringBuilder fileContentStr = new StringBuilder();
        String line;

        boolean isFirstLine = true; // Флаг для пропуска заголовка

        while ((line = reader.readLine()) != null) {
            line = line.trim(); // Убираем пробельные символы в начале и конце строки
            if (isFirstLine) {
                isFirstLine = false; // Пропускаем первую строку (заголовок)
                continue;
            }
            fileContentStr.append(line).append("\n");
        }

        // Вывод содержимого файла для проверки
        System.out.println("File Content Without Header:\n" + fileContentStr.toString());

        // Создаем парсер
        CsvParser parser = new CsvParser();
        try {
            // Парсим CSV
            List<Product> products = parser.parseCSV(file);

            System.out.println(products.size());

            for (Product product : products) {
                product.setUser(userBean.getUser());
                productService.save(product, product.getCoordinates(), product.getManufacturer(), product.getOwner(), product.getManufacturer().getOfficialAddress(), product.getOwner().getLocation(), userBean.getUser());
            }

            // Вывод информации о результатах парсинга
            System.out.println("Parsed Products Count: " + products.size());
            System.out.println("First Product Details:");

            if (!products.isEmpty()) {
                Product firstProduct = products.get(0);
                System.out.println("  Name: " + firstProduct.getName());
                System.out.println("  Price: " + firstProduct.getPrice());
                System.out.println("  Rating: " + firstProduct.getRating());
                if (firstProduct.getManufacturer() != null) {
                    System.out.println("  Manufacturer Name: " + firstProduct.getManufacturer().getName());
                } else {
                    System.out.println("  Manufacturer: null");
                }
            } else {
                System.out.println("No products parsed.");
            }
        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
        }
    }



}

