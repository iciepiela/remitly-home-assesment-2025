package pl.edu.agh.to.remitly_internship;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.agh.to.remitly_internship.Dto.CountryDto;
import pl.edu.agh.to.remitly_internship.Dto.ResponseDto;
import pl.edu.agh.to.remitly_internship.Dto.SwiftCodeDto;

import java.util.List;

@RestController
@RequestMapping("/v1/swift-codes")
public class SwiftController {


    private final SwiftService swiftService;

    public SwiftController(SwiftService swiftService) {
        this.swiftService = swiftService;
    }

    @GetMapping("/all")
    public List<SwiftCode> getSwiftCodes() {
        return swiftService.getRecords();
    }

    @GetMapping(value = "/",params = {"swiftCode"})
    public SwiftCodeDto getSwiftCode(@RequestParam String swiftCode) {
        return swiftService.getSwiftCodeWithBranches(swiftCode);
    }

    @GetMapping(value = "/country/",params = {"countryISO2code"})
    public CountryDto getCountrySwiftCodes(@RequestParam String countryISO2code) {
        return swiftService.getCountrySwiftCodes(countryISO2code);
    }

    @PostMapping(value = "")
    public ResponseEntity<ResponseDto> addSwiftCode(@RequestBody SwiftCodeDto swiftCodeDto) {
        SwiftCode savedSwiftCode = swiftService.addSwiftCode(swiftCodeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseDto("dd"));
    }

    @DeleteMapping(value = "/",params = {"swiftCode"})
    public ResponseEntity<ResponseDto> deleteSwiftCodeBySwift(@RequestParam String swiftCode) {
        swiftService.deleteSwiftCode(swiftCode);
        return ResponseEntity.ok(new ResponseDto("vvv"));
    }


}
