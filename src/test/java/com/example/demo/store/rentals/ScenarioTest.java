package com.example.demo.store.rentals;

import com.example.demo.data.ScenarioRequest;
import com.example.demo.entities.Tool;
import com.example.demo.services.ToolService;
import com.example.demo.store.rentals.exceptions.RentalRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ScenarioTest extends RentalDemoTestBase {
    static ToolService mockToolService = Mockito.mock(ToolService.class);
    static CheckoutService checkoutService = new CheckoutService(mockToolService);

    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setup() throws IOException {
        RentalDemoTestBase.setupTestData();
    }

    // Provide a stream of rental requests for the parameterized scenario test method defined below.
    static Stream<ScenarioRequest> provideRentalRequests() {
        return demoTestData.getScenarios().stream();
    }

    @DisplayName("Parameterized Scenario Test with Rental Requests obtained from DemoTestData")
    @ParameterizedTest(name = "Test Scenario: {index}")
    @MethodSource("provideRentalRequests")
    public void testSenario(ScenarioRequest senarioRequest) throws JsonProcessingException {
        //
        // This parameterized test method will be invoked once for each ScenarioRequest object in the demoTestData.
        //
        // Validation notes:
        //   - ScenarioRequest contains an expectedFinalCharge field that represents the expected final charge
        //     after the checkout process is completed, for any given scenario.
        //   - If the expectedFinalCharge is negative, the scenario is expected to throw an exception. For example,
        //     the first test scenario defines a RentalRequest with a discount of greater than 100%, which is invalid; so
        //     for this scenario the expectedFinalCharge field is set to a negative value.
        //   - If the expectedFinalCharge is positive, the scenario is expected to complete successfully, and the
        //     rental agreement's final charge is expected to equal the expectedFinalCharge contained in the scenario.
        //     (Note - The expectedFinalCharge values for each scenario where calculated by hand according to the
        //     specification's calculation rules, obviously taking into consideration the number of weekdays, weekends,
        //     and holidays in the rental period.)
        //

        // The mock ToolService returns the tool from the demoTestData tools list by specifying the answer (result)
        // that should be returned for any given invocation during checkout validation.
        Mockito.when(mockToolService.findByCode(Mockito.anyString())).thenAnswer(invocation -> {
            String toolCode = invocation.getArgument(0);
            Tool theTool = demoTestData.getTools().stream()
                    .filter(tool -> tool.getTool_code().equals(toolCode))
                    .findFirst()
                    .orElse(null);
            return theTool == null ? new ArrayList<Tool>() : List.of(theTool);
        });

        if (senarioRequest.getExpectedFinalCharge().compareTo(BigDecimal.ZERO) < 0) {
            assertThrows(RentalRequestException.class, () -> checkoutService.checkout(senarioRequest.getRentalRequest()));
        } else {
            RentalAgreement finalizedAgreement = assertDoesNotThrow(() -> checkoutService.checkout(senarioRequest.getRentalRequest()));
            assertEquals(senarioRequest.getExpectedFinalCharge(), finalizedAgreement.getFinalCharge());
            //  Crude way to output the final agreement for each scenario.
            //  TODO - Consider a more sophisticated way to output the final agreement for each scenario, leveraging
            //         the capabilities of Junit/Jupiter
            System.out.println("Formatted, final rental agreement for [" + senarioRequest.getScenarioName() + "]: \n" + finalizedAgreement);
        }

    }
}
