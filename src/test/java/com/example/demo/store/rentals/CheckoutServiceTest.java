package com.example.demo.store.rentals;

import com.example.demo.entities.RentalCost;
import com.example.demo.entities.Tool;
import com.example.demo.services.ToolService;
import com.example.demo.store.rentals.exceptions.RentalRequestException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the CheckoutManager class.
 *
 * Currently, these tests are really only verifying Request validate executed from with the CheckoutService.
 *      A much more comprehensive set of RentalRequests
 *      are performed in the RentalAgreementTest and the ScenarioTest classes.
 */
public class CheckoutServiceTest {
    // Using mockito define the mock Took and RentalCost entities, and the ToolService
    // and RentalCostService services.
    // Define the CheckoutManager object.
    static Tool mockTool = Mockito.mock(Tool.class);
    static RentalCost mockRentalCost = Mockito.mock(RentalCost.class);
    static ToolService mockToolService = Mockito.mock(ToolService.class);
    static CheckoutService checkoutService = new CheckoutService(mockToolService);

    @Test
    void testWithInvalidRentalDayCount() {
    }

    @Test
    void testWithInvalidDiscount() {
        List<Tool> tools = new ArrayList<>();
        tools.add(mockTool);

        // when ToolService is called to find a tool by code, return null.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(tools);

        // Create a RentalRequest object with an invalid tool code.
        RentalRequest rentalRequest = new RentalRequest();
        // It doesn't matter what code we specify since the mock ToolService will return a list containing the mock Tool.
        rentalRequest.setToolCode("invalid_tool_code")
                .setCheckoutDate("2021-09-01")
                .setNumberOfRentalDays(5)
                .setDiscount("101");

        // The checkout method should throw a RentalRequestException.
        assertThrows(RentalRequestException.class, () -> checkoutService.checkout(rentalRequest));
    }

    @Test
    void testWithInvalidToolCode() {
        // when ToolService is called to find a tool by code, return null.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenReturn(new ArrayList<>());

        // Create a RentalRequest object with an invalid tool code.
        RentalRequest rentalRequest = new RentalRequest();
        // It doesn't matter what code we specify since the mock ToolService will return null; nor
        // does it matter what the other fields are set to.
        rentalRequest.setToolCode("invalid_tool_code")
                .setCheckoutDate("2021-09-01")
                .setNumberOfRentalDays(5)
                .setDiscount("10");

        // The checkout method should throw a RentalRequestException.
        assertThrows(RentalRequestException.class, () -> checkoutService.checkout(rentalRequest));
    }

    @Test
    void testWithNullAndEmptyToolCodes() {
        // Create a RentalRequest object with an invalid tool code.
        RentalRequest rentalRequest = new RentalRequest();
        // Test with empty code first, does it matter what the other fields are set to.
        rentalRequest.setToolCode("")
                .setCheckoutDate("2021-09-01")
                .setNumberOfRentalDays(5)
                .setDiscount("10");

        // The checkout method should throw a RentalRequestException.
        assertThrows(RentalRequestException.class, () -> checkoutService.checkout(rentalRequest));

        // Now test with null code.
        rentalRequest.setToolCode(null);
        assertThrows(RentalRequestException.class, () -> checkoutService.checkout(rentalRequest));
    }
}