package pl.edu.agh.to.remitly_internship;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class SwiftCode {

    @Id
    @GeneratedValue
    private int id;


    private String countryISO2Code;
    private String swiftCode;
    private String bankName;
    private String address;
    private String country;
    private boolean isHeadquarter;


    public SwiftCode(String countryISO2Code, String swiftCode, String bankName, String address, String country, boolean isHeadquarter) {
        this.countryISO2Code = countryISO2Code;
        this.swiftCode = swiftCode;
        this.bankName = bankName;
        this.address = address;
        this.country = country;
        this.isHeadquarter = isHeadquarter;
    }


    public SwiftCode() {

    }

    public int getId() {
        return id;
    }

    public String getCountryISO2Code() {
        return countryISO2Code;
    }
    public String getSwiftCode() {
        return swiftCode;
    }
    public String getBankName() {
        return bankName;
    }
    public String getAddress() {
        return address;
    }
    public String getCountry() {
        return country;
    }
    public boolean isHeadquarter() {
        return isHeadquarter;
    }
}
