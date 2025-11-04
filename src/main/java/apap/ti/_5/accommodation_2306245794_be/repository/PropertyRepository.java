package apap.ti._5.accommodation_2306245794_be.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    List<Property> findAllByOrderByUpdatedDateDesc();
}