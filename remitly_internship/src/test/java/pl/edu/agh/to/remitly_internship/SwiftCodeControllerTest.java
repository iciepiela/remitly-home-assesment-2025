package pl.edu.agh.to.remitly_internship;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.agh.to.remitly_internship.Dto.CountryDto;
import pl.edu.agh.to.remitly_internship.Dto.SwiftCodeDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SwiftCodeController.class)
public class SwiftCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SwiftCodeService swiftCodeService;

    private SwiftCode sampleSwiftCode;
    private SwiftCodeDto sampleSwiftCodeDto;

    @BeforeEach
    void setUp() {
        sampleSwiftCode = new SwiftCode(
                "US",
                "ABCDUS33XXX",
                "Sample Bank",
                "123 Wall Street, New York",
                "UNITED STATES",
                true
        );

        SwiftCodeDto branchDto = new SwiftCodeDto(
                "456 Main Street, Boston",
                "Sample Bank Branch",
                "US",
                "UNITED STATES",
                false,
                "ABCDUS33BRN",
                null
        );

        sampleSwiftCodeDto = new SwiftCodeDto(
                "123 Wall Street, New York",
                "Sample Bank",
                "US",
                "UNITED STATES",
                true,
                "ABCDUS33XXX",
                Collections.singletonList(branchDto)
        );

        List<SwiftCodeDto> swiftCodeDtos = Arrays.asList(sampleSwiftCodeDto, branchDto);
        CountryDto sampleCountryDto = new CountryDto("US", "UNITED STATES", swiftCodeDtos);
    }

    @Test
    void getSwiftCodes_shouldReturnAllCodes() throws Exception {
        // Arrange
        List<SwiftCode> swiftCodes = Collections.singletonList(sampleSwiftCode);
        when(swiftCodeService.getRecords()).thenReturn(swiftCodes);

        // Act & Assert
        mockMvc.perform(get("/v1/swift-codes/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].swiftCode", is("ABCDUS33XXX")))
                .andExpect(jsonPath("$[0].bankName", is("Sample Bank")))
                .andExpect(jsonPath("$[0].countryISO2Code", is("US")))
                .andExpect(jsonPath("$[0].headquarter", is(true)));

        verify(swiftCodeService).getRecords();
    }

    @Test
    void getSwiftCode_validCode_shouldReturnSwiftCodeWithBranches() throws Exception {
        // Arrange
        String swiftCode = "ABCDUS33XXX";
        when(swiftCodeService.getSwiftCodeWithBranches(swiftCode)).thenReturn(sampleSwiftCodeDto);

        // Act & Assert
        mockMvc.perform(get("/v1/swift-codes/")
                        .param("swiftCode", swiftCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.swiftCode", is(swiftCode)))
                .andExpect(jsonPath("$.bankName", is("Sample Bank")))
                .andExpect(jsonPath("$.countryISO2", is("US")))
                .andExpect(jsonPath("$.isHeadquarter", is(true)))
                .andExpect(jsonPath("$.branches", hasSize(1)))
                .andExpect(jsonPath("$.branches[0].swiftCode", is("ABCDUS33BRN")));

        verify(swiftCodeService).getSwiftCodeWithBranches(swiftCode);
    }

    @Test
    void getSwiftCode_invalidCode_shouldReturnError() throws Exception {
        // Arrange
        String invalidCode = "INVALID";
        when(swiftCodeService.getSwiftCodeWithBranches(invalidCode))
                .thenThrow(new IllegalArgumentException("Invalid SWIFT code format: " + invalidCode));

        // Act & Assert
        mockMvc.perform(get("/v1/swift-codes/")
                        .param("swiftCode", invalidCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Invalid SWIFT code format")));

        verify(swiftCodeService).getSwiftCodeWithBranches(invalidCode);
    }

    @Test
    void getSwiftCode_nonExistentCode_shouldReturnError() throws Exception {
        // Arrange
        String nonExistentCode = "ABCDUS33XXX";
        when(swiftCodeService.getSwiftCodeWithBranches(nonExistentCode))
                .thenThrow(new NoSuchElementException("SWIFT code not found in database: " + nonExistentCode));

        // Act & Assert
        mockMvc.perform(get("/v1/swift-codes/")
                        .param("swiftCode", nonExistentCode))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("SWIFT code not found in database")));

        verify(swiftCodeService).getSwiftCodeWithBranches(nonExistentCode);
    }


    @Test
    void getCountrySwiftCodes_nonExistentCountry_shouldReturnError() throws Exception {
        // Arrange
        String nonExistentCountry = "ZZ";
        when(swiftCodeService.getCountrySwiftCodes(nonExistentCountry))
                .thenThrow(new NoSuchElementException("Country ISO code not found: " + nonExistentCountry));

        // Act & Assert
        mockMvc.perform(get("/v1/swift-codes/country/")
                        .param("countryISO2code", nonExistentCountry))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Country ISO code not found")));

        verify(swiftCodeService).getCountrySwiftCodes(nonExistentCountry);
    }

    @Test
    void addSwiftCode_validData_shouldReturnSuccess() throws Exception {
        // Arrange
        when(swiftCodeService.addSwiftCode(any(SwiftCodeDto.class))).thenReturn(sampleSwiftCode);

        // Act & Assert
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSwiftCodeDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("New SWIFT code entry added")))
                .andExpect(jsonPath("$.message", containsString("ABCDUS33XXX")));

        verify(swiftCodeService).addSwiftCode(any(SwiftCodeDto.class));
    }

    @Test
    void addSwiftCode_invalidData_shouldReturnError() throws Exception {
        // Arrange
        SwiftCodeDto invalidDto = new SwiftCodeDto(
                "Address",
                "Bank Name",
                null, // Missing country code
                "Country",
                true,
                "ABCDUS33XXX",
                null
        );

        when(swiftCodeService.addSwiftCode(any(SwiftCodeDto.class)))
                .thenThrow(new IllegalArgumentException("Country ISO code cannot be empty: null"));

        // Act & Assert
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("Country ISO code cannot be empty")));

        verify(swiftCodeService).addSwiftCode(any(SwiftCodeDto.class));
    }

    @Test
    void addSwiftCode_existingCode_shouldReturnError() throws Exception {
        // Arrange
        when(swiftCodeService.addSwiftCode(any(SwiftCodeDto.class)))
                .thenThrow(new IllegalArgumentException("SWIFT code already exists: ABCDUS33XXX"));

        // Act & Assert
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSwiftCodeDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("SWIFT code already exists")));

        verify(swiftCodeService).addSwiftCode(any(SwiftCodeDto.class));
    }

    @Test
    void addSwiftCode_databaseError_shouldReturnError() throws Exception {
        // Arrange
        when(swiftCodeService.addSwiftCode(any(SwiftCodeDto.class)))
                .thenThrow(new DatabaseException("Failed to save SWIFT code", new RuntimeException()));

        // Act & Assert
        mockMvc.perform(post("/v1/swift-codes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleSwiftCodeDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message", containsString("Failed to save SWIFT code")));

        verify(swiftCodeService).addSwiftCode(any(SwiftCodeDto.class));
    }

    @Test
    void deleteSwiftCode_validData_shouldReturnSuccess() throws Exception {

    }
}