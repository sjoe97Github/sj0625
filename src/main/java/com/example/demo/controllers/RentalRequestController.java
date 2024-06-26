package com.example.demo.controllers;

import com.example.demo.store.rentals.CheckoutService;
import com.example.demo.store.rentals.RentalAgreement;
import com.example.demo.json.serializers.RentalAgreementSerializer;
import com.example.demo.store.rentals.RentalRequest;
import com.example.demo.store.rentals.exceptions.RentalRequestException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

// This is a spring controller class that will be used to handle rental requests.
// It supports a post request to /rental/checkout which will take a rental request and return a rental agreement.
// The controller will use the CheckoutService to process the rental request.
//
@RestController
public class RentalRequestController {
    private final CheckoutService checkoutService;
    private final RentalAgreementSerializer rentalAgreementSerializer;

    @Autowired
    public RentalRequestController(CheckoutService checkoutService, RentalAgreementSerializer rentalAgreementSerializer) {
        this.checkoutService = checkoutService;
        this.rentalAgreementSerializer = rentalAgreementSerializer;
    }

    @PostMapping("/rental/checkout")
    public RentalAgreement checkoutJson(@RequestBody RentalRequest rentalRequest) throws RentalRequestException, JsonProcessingException {
        RentalAgreement rentalAgreement;
        rentalAgreement = checkoutService.checkout(rentalRequest);
        return rentalAgreement;
    }

    //
    // This is an experiment!  It's for my personal exploration and learning.
    // It works but it's not true JSON being returned
    //
    @PostMapping("/rental/checkout/as-string")
    public String checkoutRet(@RequestBody RentalRequest rentalRequest) throws RentalRequestException, JsonProcessingException {
        RentalAgreement rentalAgreement;
        rentalAgreement = checkoutService.checkout(rentalRequest);
        return rentalAgreementSerializer.toJson(rentalAgreement);
    }
}
