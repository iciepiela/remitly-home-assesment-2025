package pl.edu.agh.to.remitly_internship;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import pl.edu.agh.to.remitly_internship.Dto.CountryDto;
import pl.edu.agh.to.remitly_internship.Dto.SwiftCodeDto;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SwiftCodeService {
    private final SwiftCodeRepository swiftCodeRepository;

    public SwiftCodeService(SwiftCodeRepository swiftCodeRepository) {
        this.swiftCodeRepository = swiftCodeRepository;
    }

    public List<SwiftCode> getRecords(){
        return swiftCodeRepository.findAll();
    }

    private boolean isSwiftCodeValid(String swiftCode){
        return swiftCode != null && swiftCode.trim().length() == 11;
    }

    private SwiftCode getSwiftCode(String swiftCode) {
        if (!isSwiftCodeValid(swiftCode)) {
            throw new IllegalArgumentException("Invalid SWIFT code format: " + swiftCode);
        }

        return swiftCodeRepository.findBySwiftCode(swiftCode)
                .orElseThrow(() -> new NoSuchElementException("SWIFT code not found in database: " + swiftCode));
    }


    private List<SwiftCodeDto> getBranches(String code) {
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

    public SwiftCodeDto getSwiftCodeWithBranches(String swiftCode) {
        SwiftCode swiftCodeRecord = getSwiftCode(swiftCode);

        List<SwiftCodeDto> branches = swiftCodeRecord.isHeadquarter() ? getBranches(swiftCodeRecord.getSwiftCode()) : null;

        return new SwiftCodeDto(
                swiftCodeRecord.getAddress(),
                swiftCodeRecord.getBankName(),
                swiftCodeRecord.getCountryISO2Code(),
                swiftCodeRecord.getCountry(),
                swiftCodeRecord.isHeadquarter(),
                swiftCodeRecord.getSwiftCode(),
                branches
        );
    }


    public CountryDto getCountrySwiftCodes(String countryISO2code) {
        if (countryISO2code == null) {
            throw new IllegalArgumentException("Country ISO code cannot be null");
        }
        String upperCaseCountryISO2code = countryISO2code.trim().toUpperCase();
        if(!swiftCodeRepository.existsByCountryISO2Code(upperCaseCountryISO2code)){
            throw new NoSuchElementException("Country ISO code not found: " + upperCaseCountryISO2code);
        }
        List<SwiftCodeDto> countrySwiftCodes = swiftCodeRepository
                .findSwiftCodeByCountryISO2Code(upperCaseCountryISO2code)
                .stream()
                .map(this::convertBranchToSwiftCodeDto)
                .toList();

        String countryName = swiftCodeRepository.findCountryNameByCountryISO2Code(upperCaseCountryISO2code)
                .orElseThrow(() -> new NoSuchElementException("Country name that match "+upperCaseCountryISO2code+" not found in database"));

        return new CountryDto(
                upperCaseCountryISO2code,
                countryName,
                countrySwiftCodes
        );


    }

    public SwiftCode addSwiftCode(SwiftCodeDto swiftCodeDto) {
        if (swiftCodeDto == null) {
            throw new IllegalArgumentException("New SWIFT code cannot be null");
        }
        if(!isSwiftCodeValid(swiftCodeDto.swiftCode())){
            throw new IllegalArgumentException("Invalid SWIFT code format: " + swiftCodeDto.swiftCode());
        }
        if(swiftCodeRepository.existsBySwiftCode(swiftCodeDto.swiftCode())){
            throw new IllegalArgumentException("SWIFT code already exists: " + swiftCodeDto.swiftCode());
        }
        if(swiftCodeDto.countryISO2() == null || swiftCodeDto.countryISO2().trim().isEmpty()){
            throw new IllegalArgumentException("Country ISO code cannot be empty: " + swiftCodeDto.countryISO2());
        }
        SwiftCode swiftCode = new SwiftCode(
                swiftCodeDto.countryISO2().toUpperCase(),
                swiftCodeDto.swiftCode(),
                swiftCodeDto.bankName(),
                swiftCodeDto.address(),
                swiftCodeDto.countryName().toUpperCase(),
                swiftCodeDto.isHeadquarter()
        );

        try {
            return swiftCodeRepository.save(swiftCode);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to save SWIFT code: " + swiftCodeDto.swiftCode(), e);
        }
    }

    public void deleteSwiftCode(String swiftCode) {
        if (!isSwiftCodeValid(swiftCode)) {
            throw new IllegalArgumentException("Invalid SWIFT code format: " + swiftCode);
        }

        if (!swiftCodeRepository.existsBySwiftCode(swiftCode)) {
            throw new NoSuchElementException("SWIFT code not found: " + swiftCode);
        }

        try {
            swiftCodeRepository.deleteBySwiftCode(swiftCode);
        } catch (DataAccessException e) {
            throw new DatabaseException("Failed to delete SWIFT code: " + swiftCode, e);
        }    }
}
