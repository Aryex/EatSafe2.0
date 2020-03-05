package ca.cmpt276.restauranthealthinspection.model;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Represents the parser for parsing the CSV data.
 * <p>
 * The data is parsed using OpenCSV.
 */
public class Parser {
    // Parse data from CSV files
    public static void parseData(RestaurantManager manager, InputStreamReader inspectionDataReader, InputStreamReader restaurantDataReader) throws IOException, CsvValidationException {

        List<Inspection> inspections = parseInspections(inspectionDataReader);
        List<Restaurant> restaurants = parseRestaurants(restaurantDataReader);

        parseViolations(inspections);

        addRestaurantsToManager(manager, inspections, restaurants);
    }

    private static void parseViolations(List<Inspection> inspections) {
        for (Inspection ins : inspections) {
            String violump = ins.getViolLump();
            if (!violump.isEmpty()) {
                String[] vioChunks = violump.split("\\|");
                for (String vioChunk : vioChunks) {
                    //Log.d("vioChunk", "vioChunk: " + vioChunk);
                    ins.add(parseVioChunk(vioChunk));
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
        List<Restaurant> result = new ArrayList<Restaurant>();

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
    private static List<Inspection> parseInspections(InputStreamReader inspectionDataReader) throws IOException {
        //noinspection unchecked
        List<Inspection> result = new ArrayList<Inspection>();

        CSVReader reader = new CSVReader(inspectionDataReader);
        String[] dataCols;

        try {
            //skipping the headers
            reader.readNext();

            while (((dataCols = reader.readNext()) != null)) {
                String inspectionStrackingNum = dataCols[0];
                Calendar calendar = calendarConverter(dataCols[1]);
                String inspectionType = dataCols[2];
                int numCritical = Integer.parseInt(dataCols[3]);
                int numNonCritical = Integer.parseInt(dataCols[4]);
                String hazardRating = dataCols[5];
                String vioLump = dataCols[6];

                //Log.i("TAG", "parseInspections: " + inspectionCols[3]);
                result.add(new Inspection(
                        inspectionStrackingNum,
                        calendar,
                        inspectionType,
                        numCritical,
                        numNonCritical,
                        hazardRating,
                        vioLump
                ));
            }
        } catch (CsvValidationException | IOException e) {
            e.printStackTrace();
        }

        reader.close();
        return result;
    }

    // Parses the violations from data and generate a list containing the violations
    private static Violation parseVioChunk(String vioChunk) {
        //noinspection unchecked
        String[] dataCols = vioChunk.split(",");
        Violation violation = new Violation(Integer.parseInt(dataCols[0]), dataCols[1], dataCols[2], dataCols[3]);
        // Log.d("vioObt", vio.toString());
        return violation;
    }

    static Calendar calendarConverter(String value) {
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