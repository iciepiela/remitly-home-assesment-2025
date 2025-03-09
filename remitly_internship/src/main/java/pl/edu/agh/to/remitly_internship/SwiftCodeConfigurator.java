package pl.edu.agh.to.remitly_internship;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Configuration
public class SwiftCodeConfigurator {

    @Bean
    CommandLineRunner commendLineRunner(SwiftCodeRepository swiftCodeRepository){
        return args -> {
            if (swiftCodeRepository.count() == 0) {
                List<SwiftCode> swiftCodes = readSwiftCodesFromExcel("Interns_2025_SWIFT_CODES.xlsx");
                swiftCodeRepository.saveAll(swiftCodes);
            }
        };
    }

    private List<SwiftCode> readSwiftCodesFromExcel(String filename) {
        List<SwiftCode> swiftCodes = new ArrayList<>();

        try {
            InputStream inputStream = new ClassPathResource(filename).getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                SwiftCode swiftCode = parseExcelRow(row);
                if (swiftCode != null) {
                    swiftCodes.add(swiftCode);
                }
            }

            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return swiftCodes;
    }

    private SwiftCode parseExcelRow(Row row) {
        try {
            String countryISO2Code = getCellValue(row.getCell(0)).toUpperCase();
            String swiftCode = getCellValue(row.getCell(1));
            String bankName = getCellValue(row.getCell(3));
            String address = getCellValue(row.getCell(4));
            String country = getCellValue(row.getCell(6)).toUpperCase();

            if (swiftCode.isEmpty()) {
                throw new IllegalArgumentException("Missing required SWIFT code at row: " + row.getRowNum());
            } else if (swiftCode.length()!=11) {
                throw new IllegalArgumentException("Invalid SWIFT code length: " + swiftCode.length() + " at row: " + row.getRowNum());
            } else if (countryISO2Code.isEmpty()) {
                throw new IllegalArgumentException("Missing required countryISO2 code at row: " + row.getRowNum());
            }
            return new SwiftCode(countryISO2Code, swiftCode, bankName, address, country,isHeadquarter(swiftCode));
        } catch (IllegalArgumentException e) {
            System.err.println("Validation error in row " + row.getRowNum() + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error processing row " + row.getRowNum() + ": " + e.getMessage());
            e.printStackTrace();
        }
        //TODO
        return null;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue();
                default -> "";
            };
        } catch (Exception e) {
            System.err.println("Error reading cell value: " + e.getMessage());
            return "";
        }
    }

    private boolean isHeadquarter(String swiftCode) {
        return swiftCode != null && swiftCode.endsWith("XXX");
    }

}
