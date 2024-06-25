package com.example.demo.store.rentals;
import com.example.demo.data.DemoTestData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for RentalDemo tests.
 *  This class provides a common setup for all RentalDemo tests.
 *
 *  It reads the test data from the demoTestData.json file.
 *
 *  IMPORTANT -
 *      Currently, this class only loads the test data used by multiple tests.
 *      TODO - Consider building out this class to provide additional common setup for all tests
 */
public abstract class RentalDemoTestBase {

    protected static DemoTestData demoTestData;

    protected static void setupTestData() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = RentalDemoTestBase.class.getResourceAsStream("/demoTestData.json");
        demoTestData = objectMapper.readValue(inputStream, DemoTestData.class);
    }

}