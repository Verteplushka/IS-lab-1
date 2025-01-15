package util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import entity.*;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStreamReader;
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

            // Обработка UnitOfMeasure
            System.out.println("Parsing UnitOfMeasure: " + line[3]);
            product.setUnitOfMeasure(UnitOfMeasure.valueOf(line[3].toUpperCase()));

            // Обработка Manufacturer (Organization)
            System.out.println("Parsing Manufacturer Name: " + line[4]);
            Organization manufacturer = new Organization();
            manufacturer.setName(line[4]);
            Address address = new Address();
            System.out.println("Parsing Manufacturer Address Street: " + line[5]);
            address.setStreet(line[5]);
            System.out.println("Parsing Manufacturer Address ZipCode: " + line[6]);
            address.setZipCode(line[6]);
            manufacturer.setOfficialAddress(address);
            System.out.println("Parsing Manufacturer Annual Turnover: " + line[7]);
            manufacturer.setAnnualTurnover(Float.parseFloat(line[7]));
            System.out.println("Parsing Manufacturer Employees Count: " + line[8]);
            manufacturer.setEmployeesCount(Integer.parseInt(line[8]));
            System.out.println("Parsing Manufacturer Full Name: " + line[9]);
            manufacturer.setFullName(line[9]);
            System.out.println("Parsing Manufacturer Type: " + line[10]);
            manufacturer.setType(OrganizationType.valueOf(line[10].toUpperCase()));
            product.setManufacturer(manufacturer);

            // Обработка других полей
            System.out.println("Parsing Product Price: " + line[11]);
            product.setPrice(Long.parseLong(line[11]));
            System.out.println("Parsing Product Manufacture Cost: " + line[12]);
            product.setManufactureCost(Integer.parseInt(line[12]));
            System.out.println("Parsing Product Rating: " + line[13]);
            product.setRating(Long.parseLong(line[13]));
            System.out.println("Parsing Part Number: " + line[14]);
            product.setPartNumber(line[14]);

            // Обработка Owner (Person)
            System.out.println("Parsing Owner Name: " + line[15]);
            Person owner = new Person();
            owner.setName(line[15]);
            System.out.println("Parsing Owner Eye Color: " + line[16]);
            owner.setEyeColor(Color.valueOf(line[16].toUpperCase()));
            if (line[17] != null && !line[17].isEmpty()) {
                System.out.println("Parsing Owner Hair Color: " + line[17]);
                owner.setHairColor(Color.valueOf(line[17].toUpperCase()));
            }
            Location location = new Location();
            System.out.println("Parsing Owner Location x: " + line[18]);
            location.setX(Integer.parseInt(line[18]));
            System.out.println("Parsing Owner Location y: " + line[19]);
            location.setY(Float.parseFloat(line[19]));
            System.out.println("Parsing Owner Location z: " + line[20]);
            location.setZ(Integer.parseInt(line[20]));
            System.out.println("Parsing Owner Location Name: " + line[21]);
            location.setName(line[21]);
            owner.setLocation(location);
            System.out.println("Parsing Owner Weight: " + line[22]);
            owner.setWeight(Double.parseDouble(line[22]));
            if (line[23] != null && !line[23].isEmpty()) {
                System.out.println("Parsing Owner Nationality: " + line[23]);
                owner.setNationality(Country.valueOf(line[23].toUpperCase()));
            }
            product.setOwner(owner);

            products.add(product);
            System.out.println("Product successfully parsed and added to the list.");
            System.out.println("---------------------------------------------------");
        }
        return products;
    }
}
