package com.example.demo.json.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class StringAsPercentageSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        try {
            String percentageValue = Integer.parseInt(value) + "%";
            jsonGenerator.writeString(percentageValue);
        } catch (NumberFormatException e) {
            // If the value is not a number, just write it as is
            jsonGenerator.writeString(value);
        }
    }
}
