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

            product.setName(line[0]);

            Coordinates coordinates = new Coordinates();
            coordinates.setX(Integer.parseInt(line[1]));
            coordinates.setY(Float.parseFloat(line[2]));
            product.setCoordinates(coordinates);

            product.setUnitOfMeasure(UnitOfMeasure.valueOf(line[3].toUpperCase()));

            Organization manufacturer = new Organization();
            manufacturer.setName(line[4]);
            Address address = new Address();
            address.setStreet(line[5]);
            address.setZipCode(line[6]);
            manufacturer.setOfficialAddress(address);
            manufacturer.setAnnualTurnover(Float.parseFloat(line[7]));
            manufacturer.setEmployeesCount(Integer.parseInt(line[8]));
            manufacturer.setFullName(line[9]);
            manufacturer.setType(OrganizationType.valueOf(line[10].toUpperCase()));
            product.setManufacturer(manufacturer);

            product.setPrice(Long.parseLong(line[11]));
            product.setManufactureCost(Integer.parseInt(line[12]));
            product.setRating(Long.parseLong(line[13]));
            product.setPartNumber(line[14]);

            // Обработка Owner (Person)
            Person owner = new Person();
            owner.setName(line[15]);
            owner.setEyeColor(Color.valueOf(line[16].toUpperCase()));
            if (line[17] != null && !line[17].isEmpty()) {
                owner.setHairColor(Color.valueOf(line[17].toUpperCase()));
            }
            Location location = new Location();
            location.setX(Integer.parseInt(line[18]));
            location.setY(Float.parseFloat(line[19]));
            location.setZ(Integer.parseInt(line[20]));
            location.setName(line[21]);
            owner.setLocation(location);
            owner.setWeight(Double.parseDouble(line[22]));
            if (line[23] != null && !line[23].isEmpty()) {
                owner.setNationality(Country.valueOf(line[23].toUpperCase()));
            }
            product.setOwner(owner);

            products.add(product);
        }
        return products;
    }
}
