package pl.edu.agh.to.remitly_internship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import pl.edu.agh.to.remitly_internship.Dto.CountryDto;
import pl.edu.agh.to.remitly_internship.Dto.SwiftCodeDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SwiftCodeServiceTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @InjectMocks
    private SwiftCodeService swiftCodeService;

    private SwiftCode sampleSwiftCode;
    private SwiftCode sampleBranchSwiftCode;
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

        sampleBranchSwiftCode = new SwiftCode(
                "US",
                "ABCDUS33BRN",
                "Sample Bank Branch",
                "456 Main Street, Boston",
                "UNITED STATES",
                false
        );

        sampleSwiftCodeDto = new SwiftCodeDto(
                "123 Wall Street, New York",
                "Sample Bank",
                "US",
                "UNITED STATES",
                true,
                "ABCDUS33XXX",
                null
        );
    }

    @Test
    void getRecords_shouldReturnAllSwiftCodes() {
        // Arrange
        List<SwiftCode> expectedCodes = Arrays.asList(sampleSwiftCode, sampleBranchSwiftCode);
        when(swiftCodeRepository.findAll()).thenReturn(expectedCodes);

        // Act
        List<SwiftCode> result = swiftCodeService.getRecords();

        // Assert
        assertEquals(expectedCodes, result);
        verify(swiftCodeRepository).findAll();
    }

    @Test
    void getSwiftCodeWithBranches_validHeadquarterCode_shouldReturnWithBranches() {
        // Arrange
        String swiftCode = "ABCDUS33XXX";
        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(sampleSwiftCode));
        when(swiftCodeRepository.findAllBranches(swiftCode)).thenReturn(Collections.singletonList(sampleBranchSwiftCode));

        // Act
        SwiftCodeDto result = swiftCodeService.getSwiftCodeWithBranches(swiftCode);

        // Assert
        assertNotNull(result);
        assertEquals(swiftCode, result.swiftCode());
        assertEquals("Sample Bank", result.bankName());
        assertTrue(result.isHeadquarter());
        assertNotNull(result.branches());
        assertEquals(1, result.branches().size());
        assertEquals("Sample Bank Branch", result.branches().get(0).bankName());
        verify(swiftCodeRepository).findBySwiftCode(swiftCode);
        verify(swiftCodeRepository).findAllBranches(swiftCode);
    }

    @Test
    void getSwiftCodeWithBranches_validBranchCode_shouldReturnWithoutBranches() {
        // Arrange
        String swiftCode = "ABCDUS33BRN";
        when(swiftCodeRepository.findBySwiftCode(swiftCode)).thenReturn(Optional.of(sampleBranchSwiftCode));

        // Act
        SwiftCodeDto result = swiftCodeService.getSwiftCodeWithBranches(swiftCode);

        // Assert
        assertNotNull(result);
        assertEquals(swiftCode, result.swiftCode());
        assertEquals("Sample Bank Branch", result.bankName());
        assertFalse(result.isHeadquarter());
        assertNull(result.branches());
        verify(swiftCodeRepository).findBySwiftCode(swiftCode);
        verify(swiftCodeRepository, never()).findAllBranches(any());
    }

    @Test
    void getSwiftCodeWithBranches_invalidFormat_shouldThrowException() {
        // Arrange
        String invalidCode = "INVALID";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.getSwiftCodeWithBranches(invalidCode)
        );
        assertTrue(exception.getMessage().contains("Invalid SWIFT code format"));
        verify(swiftCodeRepository, never()).findBySwiftCode(any());
    }

    @Test
    void getSwiftCodeWithBranches_nonExistentCode_shouldThrowException() {
        // Arrange
        String nonExistentCode = "ABCDUS33XXX";
        when(swiftCodeRepository.findBySwiftCode(nonExistentCode)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> swiftCodeService.getSwiftCodeWithBranches(nonExistentCode)
        );
        assertTrue(exception.getMessage().contains("SWIFT code not found in database"));
        verify(swiftCodeRepository).findBySwiftCode(nonExistentCode);
    }

    @Test
    void getCountrySwiftCodes_validCountryCode_shouldReturnCodes() {
        // Arrange
        String countryCode = "US";
        List<SwiftCode> expectedCodes = Arrays.asList(sampleSwiftCode, sampleBranchSwiftCode);
        when(swiftCodeRepository.existsByCountryISO2Code(countryCode)).thenReturn(true);
        when(swiftCodeRepository.findSwiftCodeByCountryISO2Code(countryCode)).thenReturn(expectedCodes);
        when(swiftCodeRepository.findCountryNameByCountryISO2Code(countryCode)).thenReturn(Optional.of("UNITED STATES"));

        // Act
        CountryDto result = swiftCodeService.getCountrySwiftCodes(countryCode);

        // Assert
        assertNotNull(result);
        assertEquals(countryCode, result.countryISO2());
        assertEquals("UNITED STATES", result.countryName());
        assertEquals(2, result.swiftCodes().size());
        verify(swiftCodeRepository).existsByCountryISO2Code(countryCode);
        verify(swiftCodeRepository).findSwiftCodeByCountryISO2Code(countryCode);
        verify(swiftCodeRepository).findCountryNameByCountryISO2Code(countryCode);
    }

    @Test
    void getCountrySwiftCodes_nullCountryCode_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.getCountrySwiftCodes(null)
        );
        assertTrue(exception.getMessage().contains("Country ISO code cannot be null"));
        verify(swiftCodeRepository, never()).existsByCountryISO2Code(any());
    }

    @Test
    void getCountrySwiftCodes_nonExistentCountryCode_shouldThrowException() {
        // Arrange
        String nonExistentCode = "ZZ";
        when(swiftCodeRepository.existsByCountryISO2Code(nonExistentCode)).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> swiftCodeService.getCountrySwiftCodes(nonExistentCode)
        );
        assertTrue(exception.getMessage().contains("Country ISO code not found"));
        verify(swiftCodeRepository).existsByCountryISO2Code(nonExistentCode);
    }

    @Test
    void getCountrySwiftCodes_countryNameNotFound_shouldThrowException() {
        // Arrange
        String countryCode = "US";
        when(swiftCodeRepository.existsByCountryISO2Code(countryCode)).thenReturn(true);
        when(swiftCodeRepository.findSwiftCodeByCountryISO2Code(countryCode)).thenReturn(Arrays.asList(sampleSwiftCode));
        when(swiftCodeRepository.findCountryNameByCountryISO2Code(countryCode)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> swiftCodeService.getCountrySwiftCodes(countryCode)
        );
        assertTrue(exception.getMessage().contains("Country name that match"));
        verify(swiftCodeRepository).existsByCountryISO2Code(countryCode);
        verify(swiftCodeRepository).findSwiftCodeByCountryISO2Code(countryCode);
        verify(swiftCodeRepository).findCountryNameByCountryISO2Code(countryCode);
    }

    @Test
    void addSwiftCode_validCode_shouldSaveAndReturn() {
        // Arrange
        when(swiftCodeRepository.existsBySwiftCode("ABCDUS33XXX")).thenReturn(false);
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenReturn(sampleSwiftCode);

        // Act
        SwiftCode result = swiftCodeService.addSwiftCode(sampleSwiftCodeDto);

        // Assert
        assertNotNull(result);
        assertEquals("ABCDUS33XXX", result.getSwiftCode());
        verify(swiftCodeRepository).existsBySwiftCode("ABCDUS33XXX");
        verify(swiftCodeRepository).save(any(SwiftCode.class));
    }

    @Test
    void addSwiftCode_nullDto_shouldThrowException() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.addSwiftCode(null)
        );
        assertTrue(exception.getMessage().contains("New SWIFT code cannot be null"));
        verify(swiftCodeRepository, never()).save(any());
    }

    @Test
    void addSwiftCode_invalidFormat_shouldThrowException() {
        // Arrange
        SwiftCodeDto invalidDto = new SwiftCodeDto(
                "Address",
                "Bank",
                "US",
                "UNITED STATES",
                true,
                "INVALID",
                null
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.addSwiftCode(invalidDto)
        );
        assertTrue(exception.getMessage().contains("Invalid SWIFT code format"));
        verify(swiftCodeRepository, never()).save(any());
    }

    @Test
    void addSwiftCode_existingCode_shouldThrowException() {
        // Arrange
        when(swiftCodeRepository.existsBySwiftCode("ABCDUS33XXX")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.addSwiftCode(sampleSwiftCodeDto)
        );
        assertTrue(exception.getMessage().contains("SWIFT code already exists"));
        verify(swiftCodeRepository).existsBySwiftCode("ABCDUS33XXX");
        verify(swiftCodeRepository, never()).save(any());
    }

    @Test
    void addSwiftCode_nullCountryCode_shouldThrowException() {
        // Arrange
        SwiftCodeDto invalidDto = new SwiftCodeDto(
                "Address",
                "Bank",
                null,
                "UNITED STATES",
                true,
                "ABCDUS33XXX",
                null
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.addSwiftCode(invalidDto)
        );
        assertTrue(exception.getMessage().contains("Country ISO code cannot be empty"));
        verify(swiftCodeRepository, never()).save(any());
    }

    @Test
    void addSwiftCode_databaseError_shouldThrowException() {
        // Arrange
        when(swiftCodeRepository.existsBySwiftCode("ABCDUS33XXX")).thenReturn(false);
        when(swiftCodeRepository.save(any(SwiftCode.class))).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        DatabaseException exception = assertThrows(
                DatabaseException.class,
                () -> swiftCodeService.addSwiftCode(sampleSwiftCodeDto)
        );
        assertTrue(exception.getMessage().contains("Failed to save SWIFT code"));
        verify(swiftCodeRepository).existsBySwiftCode("ABCDUS33XXX");
        verify(swiftCodeRepository).save(any(SwiftCode.class));
    }

    @Test
    void deleteSwiftCode_existingCode_shouldDelete() {
        // Arrange
        String swiftCode = "ABCDUS33XXX";
        when(swiftCodeRepository.existsBySwiftCode(swiftCode)).thenReturn(true);
        doNothing().when(swiftCodeRepository).deleteBySwiftCode(swiftCode);

        // Act
        swiftCodeService.deleteSwiftCode(swiftCode);

        // Assert
        verify(swiftCodeRepository).existsBySwiftCode(swiftCode);
        verify(swiftCodeRepository).deleteBySwiftCode(swiftCode);
    }

    @Test
    void deleteSwiftCode_invalidFormat_shouldThrowException() {
        // Arrange
        String invalidCode = "INVALID";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> swiftCodeService.deleteSwiftCode(invalidCode)
        );
        assertTrue(exception.getMessage().contains("Invalid SWIFT code format"));
        verify(swiftCodeRepository, never()).deleteBySwiftCode(any());
    }

    @Test
    void deleteSwiftCode_nonExistentCode_shouldThrowException() {
        // Arrange
        String nonExistentCode = "ABCDUS33XXX";
        when(swiftCodeRepository.existsBySwiftCode(nonExistentCode)).thenReturn(false);

        // Act & Assert
        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> swiftCodeService.deleteSwiftCode(nonExistentCode)
        );
        assertTrue(exception.getMessage().contains("SWIFT code not found"));
        verify(swiftCodeRepository).existsBySwiftCode(nonExistentCode);
        verify(swiftCodeRepository, never()).deleteBySwiftCode(any());
    }

    @Test
    void deleteSwiftCode_databaseError_shouldThrowException() {
        // Arrange
        String swiftCode = "ABCDUS33XXX";
        when(swiftCodeRepository.existsBySwiftCode(swiftCode)).thenReturn(true);
        doThrow(new DataAccessException("Database error") {}).when(swiftCodeRepository).deleteBySwiftCode(swiftCode);

        // Act & Assert
        DatabaseException exception = assertThrows(
                DatabaseException.class,
                () -> swiftCodeService.deleteSwiftCode(swiftCode)
        );
        assertTrue(exception.getMessage().contains("Failed to delete SWIFT code"));
        verify(swiftCodeRepository).existsBySwiftCode(swiftCode);
        verify(swiftCodeRepository).deleteBySwiftCode(swiftCode);
    }
}