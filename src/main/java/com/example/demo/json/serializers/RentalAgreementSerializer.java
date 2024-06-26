package com.example.demo.json.serializers;

import com.example.demo.store.rentals.RentalAgreement;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * This class is a Spring bean used to serialize a RentalAgreement object to a JSON string.
 * IMPORTANT - This is an experimental custom serializer that facilitates the serialization of RentalAgreement objects
 *             using the toString method of the object as the JSON representation. (It's not true JSON)
 */
public class RentalAgreementSerializer {
    ObjectMapper objectMapper;

    // constructor initialization
    public RentalAgreementSerializer() {
        objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(RentalAgreement.class, new CustomToStringJsonSerializer());
        objectMapper.registerModule(module);
    }

    public String toJson(RentalAgreement rentalAgreement) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(rentalAgreement);
        return json;
    }
}
