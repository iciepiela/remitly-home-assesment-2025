package pl.edu.agh.to.remitly_internship;


import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Integer> {

    Optional<SwiftCode> findBySwiftCode(String swiftCode);

    @Query("SELECT s FROM SwiftCode s WHERE s.isHeadquarter = false AND SUBSTRING(s.swiftCode, 1, 8) = SUBSTRING(:headquarterSwiftCode, 1, 8)")
    List<SwiftCode> findAllBranches(@Param("headquarterSwiftCode") String headquarterSwiftCode);

    List<SwiftCode> findSwiftCodeByCountryISO2Code(String countryISO2Code);

    @Transactional
    void deleteBySwiftCode(String swiftCode);

    boolean existsBySwiftCode(String swiftCode);

    boolean existsByCountryISO2Code(String countryISO2Code);

    @Query("SELECT s.country FROM SwiftCode s WHERE s.countryISO2Code = :countryISO2Code")
    Optional<String> findCountryNameByCountryISO2Code(@Param("countryISO2Code") String countryISO2Code);}
