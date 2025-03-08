package pl.edu.agh.to.remitly_internship;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SwiftCodeRepository extends JpaRepository<SwiftCode, Integer> {

    SwiftCode findBySwiftCode(String swiftCode);

    @Query("SELECT s FROM SwiftCode s WHERE s.isHeadquarter = false AND SUBSTRING(s.swiftCode, 1, 8) = SUBSTRING(:headquarterSwiftCode, 1, 8)")
    List<SwiftCode> findAllBranches(@Param("headquarterSwiftCode") String headquarterSwiftCode);

}
