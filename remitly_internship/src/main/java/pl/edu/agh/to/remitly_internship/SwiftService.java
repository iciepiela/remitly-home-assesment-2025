package pl.edu.agh.to.remitly_internship;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.remitly_internship.Dto.CountryDto;
import pl.edu.agh.to.remitly_internship.Dto.SwiftCodeDto;

import java.util.List;

@Service
public class SwiftService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftService(SwiftCodeRepository swiftCodeRepository) {
        this.swiftCodeRepository = swiftCodeRepository;
    }

    public List<SwiftCode> getRecords(){
        return swiftCodeRepository.findAll();
    }

    public SwiftCode getSwiftCode(String code){
        return swiftCodeRepository.findBySwiftCode(code);
    }

    public List<SwiftCodeDto> getBranches(String code) {
        return swiftCodeRepository.findAllBranches(code)
                .stream()
                .map(this::convertBranchToSwiftCodeDto)
                .toList();
    }

    private SwiftCodeDto convertBranchToSwiftCodeDto(SwiftCode swiftCode) {
        return new SwiftCodeDto(
                swiftCode.getAddress(),
                swiftCode.getBankName(),
                swiftCode.getCountryISO2Code(),
                null,
                swiftCode.isHeadquarter(),
                swiftCode.getSwiftCode(),
                null
        );
    }

    public SwiftCodeDto getSwiftCodeWithBranches(String swiftCode){
        SwiftCode swiftCodeRecord = getSwiftCode(swiftCode);
        if (swiftCodeRecord.isHeadquarter()) {
            List<SwiftCodeDto> branches = getBranches(swiftCodeRecord.getSwiftCode());
            return new SwiftCodeDto(
                    swiftCodeRecord.getAddress(),
                    swiftCodeRecord.getBankName(),
                    swiftCodeRecord.getCountryISO2Code(),
                    swiftCodeRecord.getCountry(),
                    swiftCodeRecord.isHeadquarter(),
                    swiftCodeRecord.getSwiftCode(),
                    branches);
        }
        else {
            return new SwiftCodeDto(
                    swiftCodeRecord.getAddress(),
                    swiftCodeRecord.getBankName(),
                    swiftCodeRecord.getCountryISO2Code(),
                    swiftCodeRecord.getCountry(),
                    swiftCodeRecord.isHeadquarter(),
                    swiftCodeRecord.getSwiftCode(),
                    null
            );
        }
    }

    public CountryDto getCountrySwiftCodes(String countryISO2code) {
        List<SwiftCodeDto> countrySwiftCodes = swiftCodeRepository
                .findSwiftCodeByCountryISO2Code(countryISO2code)
                .stream()
                .map(this::convertBranchToSwiftCodeDto)
                .toList();

        return new CountryDto(
                countryISO2code,
                "A",
                countrySwiftCodes
        );


    }


    public SwiftCode addSwiftCode(SwiftCodeDto swiftCodeDto) {
        SwiftCode swiftCode = new SwiftCode(
                swiftCodeDto.countryISO2(),
                swiftCodeDto.swiftCode(),
                swiftCodeDto.bankName(),
                swiftCodeDto.address(),
                swiftCodeDto.countryName(),
                swiftCodeDto.isHeadquarter()
        );

        return swiftCodeRepository.save(swiftCode);
    }

    public void deleteSwiftCode(String swiftCode) {
        swiftCodeRepository.deleteBySwiftCode(swiftCode);
    }
}
