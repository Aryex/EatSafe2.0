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
 *
 */
public class Parser {
    public static void parseData(RestaurantManager manager, File inspectionData, File restaurantData) throws FileNotFoundException {
        List<Inspection> inspections = parseInspections(inspectionData);
        List<Restaurant> restaurants = parseRestaurants(restaurantData);

//            for (Inspection ins : inspections) {
//                CSVParser parser = new CSVParserBuilder()
//                        .withSeparator('|')
//                        .build();
//                CSVReader reader = new CSVReaderBuilder(new StringReader(ins.getViolLump()))
//                        .withCSVParser(parser)
//                        .build();
//                try {
//                    List<String[]> myEntries = reader.readAll();
//                    for (String[] sarr : myEntries) {
//                        for (String s : sarr) {
//                            System.out.println(s);
//                        }
//                    }
//                } catch (CsvException | IOException e) {
//                    e.printStackTrace();
//                }
//            }

        for (Restaurant res : restaurants) {
            for (Inspection ins : inspections) {
                if(ins.getInsTrackingNumber().equals(res.getResTrackingNumber())) {
                    res.add(ins);
                }
                System.out.println(ins.getDaysInBetween() + " days ago" + " | " + ins.getFromCurrentDate());
            }
            manager.add(res);
        }
    }

    public static List<Restaurant> parseRestaurants(File restaurantData) throws FileNotFoundException {
        //noinspection unchecked
        return (List<Restaurant>) new CsvToBeanBuilder(new FileReader(restaurantData.getAbsolutePath()))
                .withType(Restaurant.class)
                .build()
                .parse();
    }

    public static List<Inspection> parseInspections(File inspectionData) throws FileNotFoundException {
        //noinspection unchecked
        return (List<Inspection>) new CsvToBeanBuilder(new FileReader(inspectionData.getAbsolutePath()))
                .withType(Inspection.class)
                .build()
                .parse();
    }
}
