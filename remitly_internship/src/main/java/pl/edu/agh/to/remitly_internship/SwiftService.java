package pl.edu.agh.to.remitly_internship;

import org.springframework.stereotype.Service;
import pl.edu.agh.to.remitly_internship.Dto.HeadquarterDto;
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
                .map(this::convertToSwiftCodeDto)
                .toList();
    }

    private SwiftCodeDto convertToSwiftCodeDto(SwiftCode swiftCode) {
        return new SwiftCodeDto(
                swiftCode.getAddress(),
                swiftCode.getBankName(),
                swiftCode.getCountryISO2Code(),
                swiftCode.isHeadquarter(),
                swiftCode.getSwiftCode()
        );
    }

    public HeadquarterDto getHeadquarter(String code){
        SwiftCode headquarterSwiftCode = getSwiftCode(code);
        List<SwiftCodeDto> branches = getBranches(headquarterSwiftCode.getSwiftCode());
        return new HeadquarterDto(
                headquarterSwiftCode.getAddress(),
                headquarterSwiftCode.getBankName(),
                headquarterSwiftCode.getCountryISO2Code(),
                headquarterSwiftCode.getCountry(),
                headquarterSwiftCode.isHeadquarter(),
                headquarterSwiftCode.getSwiftCode(),
                branches);
    }



}
