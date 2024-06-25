package com.example.demo.store.rentals;

import com.example.demo.entities.Tool;
import com.example.demo.services.ToolService;
import com.example.demo.store.rentals.exceptions.RentalRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutService {
    private ToolService toolService;    // using ctor injection for the service

    public CheckoutService(ToolService toolService) {
        this.toolService = toolService;
    }

    public RentalAgreement checkout(RentalRequest rentalRequest) throws RentalRequestException {
        RentalAgreement rentalAgreement =  validateRequestAndInitializeRentalAgreement(rentalRequest, toolService);

        rentalAgreement.finalizeAgreement();

        return rentalAgreement;
    }

    // This method is used to create the initial rental agreement.
    // The primary purpose of this method provides testability of request validation, tool retrieval,
    // and basic agreement creation.
    static RentalAgreement validateRequestAndInitializeRentalAgreement(RentalRequest rentalRequest, ToolService toolService) throws RentalRequestException {
        Tool tool = validateRentalRequestAndReturnToolInstance(rentalRequest, toolService);

        //  The request is valid and the tool has been retrieved.
        RentalAgreement rentalAgreement =  new RentalAgreement();
        return rentalAgreement.setTool(tool)
                .setRequestedRentalDays(rentalRequest.getNumberOfRentalDays())
                .setCheckoutDate(rentalRequest.getCheckoutDate())
                .setDiscountPercent(rentalRequest.getDiscount());
    }

    // The primary purpose of this method provides testability of request validation, tool retrieval,
    static Tool validateRentalRequestAndReturnToolInstance(RentalRequest rentalRequest, ToolService toolService) throws RentalRequestException {
        // validate checkout date format

        if (rentalRequest.getNumberOfRentalDays() < 1) {
            throw new RentalRequestException(
                    String.format("Rental days must be greater than 0! Given number of days = %d", rentalRequest.getNumberOfRentalDays())
            );
        }

        String requestDiscount = rentalRequest.getDiscount();
        if (requestDiscount == null || requestDiscount.isEmpty()) {
            throw new RentalRequestException(
                    String.format("Discount percent is required!, Given discount percent = %s", requestDiscount)
            );
        }

        int discount = Integer.parseInt(requestDiscount);
        if (discount < 0 || discount > 100) {
            throw new RentalRequestException(
                    String.format("Discount percent must be between 0 and 100! Given discount percent = %d", discount)
            );
        }

        return validateToolCodeAndReturnToolInstance(rentalRequest.getToolCode(), toolService);
    }

    static Tool validateToolCodeAndReturnToolInstance(String toolCode, ToolService toolService) throws RentalRequestException {
        if (toolCode == null || toolCode.isEmpty()) {
            throw new RentalRequestException(
                    String.format("Tool code is required!, Given tool code = %s", toolCode)
            );
        }

        List<Tool> toolList = toolService.findByCode(toolCode);
        if (toolList.isEmpty()) {
            throw new RentalRequestException(
                    String.format("No tool found for tool code = %s", toolCode)
            );
        }

        //  In theory the list should only contain one tool.
        //      However, the code is written to handle the case where there are multiple tools with the same code.
        //      The code assumes only one tool by simply returning the first element of the list and ignoring the rest.
        //
        //  If the DB Schema is defined in such a way that there could only be a one-to-one mapping between tools and
        //  tool codes, then the "one tool" assumption is valid; however, even if the schema is defined this way, it would
        //  probably still be safer to check the size of the list and throw an exception if the size is greater than 1.
        //
        //  A tradeoff is made here; checking for more than one tool would be overkill for this simple, demo application.
        //
        return toolList.get(0);
    }
}
