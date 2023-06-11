package com.javatechie.crud.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javatechie.crud.example.entity.Product;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CSVUtils.class);

    private static final ObjectMapper mapper = new ObjectMapper();


    //Test function
    public static void main(String args[]) {

        Product p1 = new Product(1, "p1", 5, 2.3);
        Product p2 = new Product(2, "p2", 10, 2.4);
        Product p3 = new Product(3, "p3", 15, 3.3);

        List<Product> productList = Arrays.asList(p1, p2, p3);

        ByteArrayInputStream bis = beanToCSV(productList);
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> generateHeaders(Class<?> clazz) {
        List<String> headers = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            headers.add(field.getName());
        }
        int idx = headers.indexOf("SERIALIZABLE");
        if (idx != -1) {
            headers.remove(idx);
        }
        return headers;
    }

    public static ByteArrayInputStream productBeanToCSV(List<Product> products) {

        String[] headers = generateHeaders(Product.class).toArray(new String[0]);

        final CSVFormat format = CSVFormat.DEFAULT.builder().setQuoteMode(QuoteMode.MINIMAL).setHeader(headers).build();

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (Product product : products) {
                List<String> data = Arrays.asList(
                        String.valueOf(product.getId()),
                        product.getName(),
                        String.valueOf(product.getQuantity()),
                        String.valueOf(product.getPrice())
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            LOGGER.error("fail to import data to CSV file: " + e.getMessage());
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }

    }


    /**
     * Generic function to convert bean data to CSV
     *
     * @param beanData List of object to convert into CSV
     * @return ByteArrayInputStream
     */
    public static <T> ByteArrayInputStream beanToCSV(List<? extends T> beanData) {

        String[] headers = generateHeaders(beanData.get(0).getClass()).toArray(new String[0]);

        final CSVFormat format = CSVFormat.DEFAULT.builder().setQuoteMode(QuoteMode.MINIMAL).setHeader(headers).build();

        Field[] fields = beanData.get(0).getClass().getDeclaredFields();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
                CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {
            for (T object : beanData) {
                List<String> data = new ArrayList<>();
                for (Field f : fields) {
                    if ( !f.getName().equalsIgnoreCase("serializable")) {
                        f.setAccessible(true);
                        Object value = f.get(object);
                        data.add(value.toString());
                    }
                }
                csvPrinter.printRecord(data);
            }
            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            LOGGER.error("fail to import data to CSV file: " + e.getMessage());
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

}
