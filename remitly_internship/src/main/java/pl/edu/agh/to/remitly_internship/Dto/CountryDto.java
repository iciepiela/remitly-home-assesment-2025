package pl.edu.agh.to.remitly_internship.Dto;

import java.util.List;

public record CountryDto (
        String countryISO2,
        String countryName,
        List<SwiftCodeDto> swiftCodes
        ){
}
