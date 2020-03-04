package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;

import java.io.*;

import java.util.List;

/**
 * Represents the parser for parsing the CSV data.
 *
 * The data is parsed using OpenCSV.
 */
public class Parser {
    // Parse data from CSV files
    public static void parseData(RestaurantManager manager, InputStreamReader inspectionDataReader, InputStreamReader restaurantDataReader) throws IOException {
        List<Inspection> inspections = parseInspections(inspectionDataReader);
        List<Restaurant> restaurants = parseRestaurants(restaurantDataReader);

        CSVParser parser = new CSVParserBuilder()
                .withSeparator('|')
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        for (Inspection ins : inspections) {
            CSVReader reader = new CSVReaderBuilder(new StringReader(ins.getViolLump()))
                    .withCSVParser(parser)
                    .build();
            try {
                List<String[]> violLump = reader.readAll();
                for (String[] violationsArray : violLump) {
                    for (String violationString : violationsArray) {
                        for (Violation v : parseViolations(violationString)) {
                            ins.add(v);
                        }
                    }
                }
            } catch (CsvException | IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }

        // 205,Critical,Cold potentially hazardous food stored/displayed above 4 Â°C. [s. 14(2)],Not Repeat|
        // 209,Not Critical,Food not protected from contamination [s. 12(a)],Not Repeat|306,Not Critical,Food premises not maintained in a sanitary condition [s. 17(1)],Not Repeat|
        // 308,Not Critical,Equipment/utensils/food contact surfaces are not in good working order [s. 16(b)],Not Repeat|
        // 402,Critical,Employee does not wash hands properly or at adequate frequency [s. 21(3)],Not Repeat

        addRestaurantsToManager(manager, inspections, restaurants);
    }

    // Add restaurants to manager
    private static void addRestaurantsToManager(RestaurantManager manager, List<Inspection> inspections, List<Restaurant> restaurants) {
        for (Restaurant res : restaurants) {
            for (Inspection ins : inspections) {
                if (ins.getInsTrackingNumber().
                        equals(res.getResTrackingNumber())) {
                    res.add(ins);
                }
            }
            manager.add(res);
        }
    }

    // Parses the restaurants from data and generate a list containing the restaurants
    private static List<Restaurant> parseRestaurants(InputStreamReader restaurantDataReader) throws FileNotFoundException {
        //noinspection unchecked
        return (List<Restaurant>) new CsvToBeanBuilder(restaurantDataReader)
                .withType(Restaurant.class)
                .build()
                .parse();
    }

    // Parses the inspections from data and generate a list containing the inspections
    private static List<Inspection> parseInspections(InputStreamReader inspectionDataReader) throws FileNotFoundException {
        //noinspection unchecked
        return (List<Inspection>) new CsvToBeanBuilder(inspectionDataReader)
                .withType(Inspection.class)
                .build()
                .parse();
    }

    // Parses the violations from data and generate a list containing the violations
    private static List<Violation> parseViolations(String violLump) {
        //noinspection unchecked
        return (List<Violation>) new CsvToBeanBuilder(new StringReader(violLump))
                .withType(Violation.class)
                .build()
                .parse();
    }
}