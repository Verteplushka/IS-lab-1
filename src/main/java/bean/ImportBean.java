package bean;

import entity.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.model.DataModel;
import jakarta.faces.model.ListDataModel;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import service.ImportHistoryService;
import service.ProductService;
import util.CsvParser;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Named
@RequestScoped
@Getter
@Setter
public class ImportBean implements Serializable {

    @Inject
    private ImportHistoryService importHistoryService;
    @Inject
    private ProductService productService;
    @Inject
    private UserBean userBean;


    public List<ImportHistory> getAllImportHistory() {
        List<ImportHistory> history = importHistoryService.getAllImportHistory();
        if(userBean.getUser().getRole() == User.Role.ADMIN){
            return history;
        }
        return history.stream()
                .filter(each_history -> each_history.getUser().equals(userBean.getUser().getLogin()))
                .collect(Collectors.toList());
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

            productService.saveAll(products, userBean.getUser());

            importHistoryService.save(new ImportHistory(OperationStatus.SUCCESS, userBean.getUser().getLogin(), products.size()));

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
            importHistoryService.save(new ImportHistory(OperationStatus.FAILED, userBean.getUser().getLogin(), null));
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error occurred: " + e.getMessage(), "Error occurred: " + e.getMessage()));
            System.out.println("Error occurred: " + e.getMessage() + " class: " + e.getClass());
        }

    }

}

