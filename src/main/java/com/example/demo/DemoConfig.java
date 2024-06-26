package com.example.demo;

import com.example.demo.json.serializers.RentalAgreementSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfig {
    // simple configuration class solely for making custom JSON serializer, RentalAgreementSerializer, available
    // as a Spring bean.
    @Bean
    public RentalAgreementSerializer rentalAgreementSerializer() {
        return new RentalAgreementSerializer();
    }
}
