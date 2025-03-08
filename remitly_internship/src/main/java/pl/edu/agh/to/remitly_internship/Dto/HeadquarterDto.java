package pl.edu.agh.to.remitly_internship.Dto;

import java.util.List;

public record HeadquarterDto (
    String address,
    String bankName,
    String countryISO2,
    String countryName,
    boolean isHeadquarter,
    String swiftCode,
    List<SwiftCodeDto> branches
    ){
}
