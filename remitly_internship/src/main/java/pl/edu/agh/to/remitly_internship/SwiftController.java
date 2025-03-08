package pl.edu.agh.to.remitly_internship;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.agh.to.remitly_internship.Dto.HeadquarterDto;

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
    public HeadquarterDto getSwiftCode(@RequestParam String swiftCode) {
        return swiftService.getHeadquarter(swiftCode);
    }


}
