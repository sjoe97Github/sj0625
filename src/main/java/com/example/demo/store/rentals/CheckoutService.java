package com.example.demo.store.rentals;

import com.example.demo.entities.Tool;
import com.example.demo.services.ToolService;
import com.example.demo.store.rentals.exceptions.RentalRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckoutManager {
    private ToolService toolService;
    private Tool tool;

    public CheckoutManager(ToolService toolService) {
        this.toolService = toolService;
    }

    public Tool getTool() {
        return this.tool;
    }

    public RentalAgreement checkout(RentalRequest rentalRequest) throws RentalRequestException {
        validateRentalRequest(rentalRequest);
        validateAndSetTool(rentalRequest);

        //  The request is valid and the tool has been retrieved.  Move on to building the agreement.
        RentalAgreement rentalAgreement =  new RentalAgreement();
        rentalAgreement.setTool(tool)
                .setRequestedRentalDays(rentalRequest.getNumberOfRentalDays())
                .setCheckoutDate(rentalRequest.getCheckoutDate())
                .setDiscountPercent(rentalRequest.getDiscount())
                .finalizeAgreement();

        return rentalAgreement;
    }

    private void validateRentalRequest(RentalRequest rentalRequest) throws RentalRequestException {
        // validate checkout date format

        if (rentalRequest.getNumberOfRentalDays() < 1) {
            throw new RentalRequestException(
                    String.format("Rental days must be greater than 0! Given number of days = %d", rentalRequest.getNumberOfRentalDays())
            );
        }

        int discount = rentalRequest.getDiscount();
        if (discount < 0 || discount > 100) {
            throw new RentalRequestException(
                    String.format("Discount percent must be between 0 and 100! Given discount percent = %d", discount)
            );
        }
    }

    private void validateAndSetTool(RentalRequest rentalRequest) throws RentalRequestException {
        String toolCode = rentalRequest.getToolCode();
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
        //  This code assumes only one tool by simply returning the first element of the list and ignoring the rest.
        //  If the DB Schema is defined in such a way that there could only be a one-to-one mapping between tools and
        //  tool codes, then this assumption is valid; however, even if the schema is defined this way, it is still
        //  safer to check the size of the list and throw an exception if the size is greater than 1.
        //
        //  A tradeoff is made here that checking for more than one tool would be overkill for this simple application.
        //
        this.tool = toolList.get(0);
    }
}
