package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents the parser for parsing the CSV data.
 *
 * The data is parsed using OpenCSV.
 */
class Parser {
    // Parse data from CSV files
    static void parseData(RestaurantManager manager, InputStreamReader inspectionDataReader,
                          InputStreamReader restaurantDataReader, boolean originalFile) throws IOException, CsvValidationException {

        List<Inspection> inspections = parseInspections(inspectionDataReader, originalFile);
        List<Restaurant> restaurants = parseRestaurants(restaurantDataReader);

        parseViolationsInto(inspections);

        addRestaurantsToManager(manager, inspections, restaurants);
    }

    // Parse ViolLump and add violations to inspection
    private static void parseViolationsInto(List<Inspection> inspections) {

        for (Inspection ins : inspections) {

            String violLump = ins.getViolLump();
            if (!violLump.isEmpty()) {
                String[] violChunks = violLump.split("\\|");
                for (String violChunk : violChunks) {
                    ins.add(parseVioChunk(violChunk));
                }
            }
        }
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
    private static List<Restaurant> parseRestaurants(InputStreamReader restaurantDataReader) throws IOException, CsvValidationException {
        List<Restaurant> result = new ArrayList<>();

        CSVReader reader = new CSVReader(restaurantDataReader);
        String[] dataCols;
        //skipping header
        reader.readNext();

        while ((dataCols = reader.readNext()) != null) {
            String resTrackingNumber = dataCols[0];
            String name = dataCols[1];
            String address = dataCols[2];
            String city = dataCols[3];
            String resType = dataCols[4];
            double latitude = Double.parseDouble(dataCols[5]);
            double longitude = Double.parseDouble(dataCols[6]);

            result.add(new Restaurant(
                    resTrackingNumber,
                    name,
                    address,
                    city,
                    resType,
                    latitude,
                    longitude
            ));
        }

        reader.close();
        return result;
    }

    // Parses the inspections from data and generate a list containing the inspections
    private static List<Inspection> parseInspections(InputStreamReader inspectionDataReader, boolean originalFile) throws IOException {
        List<Inspection> result = new ArrayList<>();

        CSVReader reader = new CSVReader(inspectionDataReader);
        String[] dataCols;

        try {
            //skipping the headers
            reader.readNext();
            while (((dataCols = reader.readNext()) != null)) {

                if (!dataCols[0].equals("")) {
                    String inspectionTrackingNum = dataCols[0];
                    Calendar calendar = calendarConverter(dataCols[1]);
                    String inspectionType = dataCols[2];
                    int numCritical = Integer.parseInt(dataCols[3]);
                    int numNonCritical = Integer.parseInt(dataCols[4]);
                    String hazardRating;
                    String vioLump;
                    if (originalFile) {
                        hazardRating = dataCols[5];
                        vioLump = dataCols[6];
                    }
                    else {
                        hazardRating = dataCols[6];
                        vioLump = dataCols[5];
                    }
                    result.add(new Inspection(
                            inspectionTrackingNum,
                            calendar,
                            inspectionType,
                            numCritical,
                            numNonCritical,
                            hazardRating,
                            vioLump
                    ));
                }

            }
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }

        reader.close();
        return result;
    }

    // Parses the violations from data and create a violation object
    private static Violation parseVioChunk(String vioChunk) {
        String[] dataCols = vioChunk.split(",");
        Violation vio = new Violation(Integer.parseInt(dataCols[0]), dataCols[1], dataCols[2], dataCols[3]);
        return vio;
    }

    // Converts the date string into a calendar object
    private static Calendar calendarConverter(String value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CANADA);

        try {
            Date date = format.parse(value);
            Calendar calendar = Calendar.getInstance();

            assert date != null;

            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to parse date");
        }
    }
}
