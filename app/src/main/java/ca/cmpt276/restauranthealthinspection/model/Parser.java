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
    public static void parseData(RestaurantManager manager, File inspectionData, File restaurantData) throws IOException {
        List<Inspection> inspections = parseInspections(inspectionData);
        List<Restaurant> restaurants = parseRestaurants(restaurantData);

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
    public static List<Restaurant> parseRestaurants(File restaurantData) throws FileNotFoundException {
        //noinspection unchecked
        return (List<Restaurant>) new CsvToBeanBuilder(new FileReader(restaurantData.getAbsolutePath()))
                .withType(Restaurant.class)
                .build()
                .parse();
    }

    // Parses the inspections from data and generate a list containing the inspections
    public static List<Inspection> parseInspections(File inspectionData) throws FileNotFoundException {
        //noinspection unchecked
        return (List<Inspection>) new CsvToBeanBuilder(new FileReader(inspectionData.getAbsolutePath()))
                .withType(Inspection.class)
                .build()
                .parse();
    }

    // Parses the violations from data and generate a list containing the violations
    public static List<Violation> parseViolations(String violLump) {
        //noinspection unchecked
        return (List<Violation>) new CsvToBeanBuilder(new StringReader(violLump))
                .withType(Violation.class)
                .build()
                .parse();
    }
}