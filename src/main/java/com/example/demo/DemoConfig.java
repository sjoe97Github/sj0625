package com.example.demo;

import com.example.demo.json.serializers.RentalAgreementSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoConfig {
    // very rudimentary configuration class
    @Bean
    public RentalAgreementSerializer rentalAgreementSerializer() {
        return new RentalAgreementSerializer();
    }
}
