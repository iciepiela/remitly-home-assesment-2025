package pl.edu.agh.to.remitly_internship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwiftCodeConfiguratorTest {

    @Mock
    private SwiftCodeRepository swiftCodeRepository;

    @InjectMocks
    private SwiftCodeConfigurator swiftCodeConfigurator;

    @Test
    void commandLineRunner_ShouldLoadSwiftCodes_WhenRepositoryIsEmpty() throws Exception {
        // Arrange
        when(swiftCodeRepository.count()).thenReturn(0L);

        SwiftCode swiftCode = new SwiftCode("PL", "TESTBANKXXX", "Test Bank", "Test Address", "POLAND", true);
        List<SwiftCode> swiftCodes = List.of(swiftCode);

        SwiftCodeConfigurator spyConfigurator = Mockito.spy(swiftCodeConfigurator);
        doReturn(swiftCodes).when(spyConfigurator).readSwiftCodesFromExcel(anyString());

        // Act
        spyConfigurator.commendLineRunner(swiftCodeRepository).run();

        // Assert
        verify(swiftCodeRepository, times(1)).saveAll(swiftCodes);
    }

    @Test
    void commandLineRunner_ShouldNotLoadSwiftCodes_WhenRepositoryIsNotEmpty() throws Exception {
        // Arrange
        when(swiftCodeRepository.count()).thenReturn(10L);

        // Act
        swiftCodeConfigurator.commendLineRunner(swiftCodeRepository).run();

        // Assert
        verify(swiftCodeRepository, never()).saveAll(Mockito.anyList());
    }

    @Test
    void commandLineRunner_ShouldHandleException_WhenReadSwiftCodesFails() throws Exception {
        // Arrange
        SwiftCodeConfigurator spyConfigurator = Mockito.spy(swiftCodeConfigurator);
        doThrow(new RuntimeException("Error reading file")).when(spyConfigurator).readSwiftCodesFromExcel(anyString());

        // Act
        spyConfigurator.commendLineRunner(swiftCodeRepository).run();

        // Assert
        verify(swiftCodeRepository, times(1)).count();
    }
}
