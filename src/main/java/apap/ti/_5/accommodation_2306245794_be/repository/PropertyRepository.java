package apap.ti._5.accommodation_2306245794_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import apap.ti._5.accommodation_2306245794_be.model.Property;
import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    List<Property> findAllByOrderByUpdatedDateDesc();

    @Query("SELECT p FROM Property p LEFT JOIN FETCH p.listRoomType WHERE p.propertyId = :id")
    Optional<Property> findByIdWithRoomTypes(String id);

    List<Property> findByActiveStatus(int status);
}