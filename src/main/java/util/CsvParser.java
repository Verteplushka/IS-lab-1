package util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import entity.*;
import jakarta.servlet.http.Part;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class CsvParser {

    public List<Product> parseCSV(Part file) throws IOException, CsvValidationException {
        CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
        List<Product> products = new ArrayList<>();
        String[] line;

        // Пропуск заголовка
        reader.readNext();

        while ((line = reader.readNext()) != null) {
            Product product = new Product();

            // Установка свойств продукта с немедленным выводом
            System.out.println("Parsing Product Name: " + line[0]);
            product.setName(line[0]);

            // Обработка Coordinates
            System.out.println("Parsing Coordinates x: " + line[1]);
            System.out.println("Parsing Coordinates y: " + line[2]);
            Coordinates coordinates = new Coordinates();
            coordinates.setX(Integer.parseInt(line[1]));
            coordinates.setY(Float.parseFloat(line[2]));
            product.setCoordinates(coordinates);

            // Преобразование строки даты
            System.out.println("Parsing Creation Date: " + line[3]);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            product.setCreationDate(LocalDate.parse(line[3], formatter));

            // Обработка UnitOfMeasure
            System.out.println("Parsing UnitOfMeasure: " + line[4]);
            product.setUnitOfMeasure(UnitOfMeasure.valueOf(line[4].toUpperCase()));

            // Обработка Manufacturer (Organization)
            System.out.println("Parsing Manufacturer Name: " + line[5]);
            Organization manufacturer = new Organization();
            manufacturer.setName(line[5]);
            Address address = new Address();
            System.out.println("Parsing Manufacturer Address Street: " + line[6]);
            address.setStreet(line[6]);
            System.out.println("Parsing Manufacturer Address ZipCode: " + line[7]);
            address.setZipCode(line[7]);
            manufacturer.setOfficialAddress(address);
            System.out.println("Parsing Manufacturer Annual Turnover: " + line[8]);
            manufacturer.setAnnualTurnover(Float.parseFloat(line[8]));
            System.out.println("Parsing Manufacturer Employees Count: " + line[9]);
            manufacturer.setEmployeesCount(Integer.parseInt(line[9]));
            System.out.println("Parsing Manufacturer Full Name: " + line[10]);
            manufacturer.setFullName(line[10]);
            System.out.println("Parsing Manufacturer Type: " + line[11]);
            manufacturer.setType(OrganizationType.valueOf(line[11].toUpperCase()));
            product.setManufacturer(manufacturer);

            // Обработка других полей
            System.out.println("Parsing Product Price: " + line[12]);
            product.setPrice(Long.parseLong(line[12]));
            System.out.println("Parsing Product Manufacture Cost: " + line[13]);
            product.setManufactureCost(Integer.parseInt(line[13]));
            System.out.println("Parsing Product Rating: " + line[14]);
            product.setRating(Integer.parseInt(line[14]));
            System.out.println("Parsing Part Number: " + line[15]);
            product.setPartNumber(line[15]);

            // Обработка Owner (Person)
            System.out.println("Parsing Owner Name: " + line[16]);
            Person owner = new Person();
            owner.setName(line[16]);
            System.out.println("Parsing Owner Eye Color: " + line[17]);
            owner.setEyeColor(Color.valueOf(line[17].toUpperCase()));
            if (line[18] != null && !line[18].isEmpty()) {
                System.out.println("Parsing Owner Hair Color: " + line[18]);
                owner.setHairColor(Color.valueOf(line[18].toUpperCase()));
            }
            Location location = new Location();
            System.out.println("Parsing Owner Location x: " + line[19]);
            location.setX(Integer.parseInt(line[19]));
            System.out.println("Parsing Owner Location y: " + line[20]);
            location.setY(Float.parseFloat(line[20]));
            System.out.println("Parsing Owner Location z: " + line[21]);
            location.setZ(Integer.parseInt(line[21]));
            System.out.println("Parsing Owner Location Name: " + line[22]);
            location.setName(line[22]);
            owner.setLocation(location);
            System.out.println("Parsing Owner Weight: " + line[23]);
            owner.setWeight(Double.parseDouble(line[23]));
            if (line[24] != null && !line[24].isEmpty()) {
                System.out.println("Parsing Owner Nationality: " + line[24]);
                owner.setNationality(Country.valueOf(line[24].toUpperCase()));
            }
            product.setOwner(owner);

            products.add(product);
            System.out.println("Product successfully parsed and added to the list.");
            System.out.println("---------------------------------------------------");
        }
        return products;
    }
}
