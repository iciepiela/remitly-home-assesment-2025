package pl.edu.agh.to.remitly_internship.Dto;

public record SwiftCodeDto(
        String address,
        String bankName,
        String countryISO2,
        boolean isHeadquarter,
        String swiftCode) {
}
