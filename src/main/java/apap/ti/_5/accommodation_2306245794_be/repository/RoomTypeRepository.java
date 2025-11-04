package apap.ti._5.accommodation_2306245794_be.repository;

import apap.ti._5.accommodation_2306245794_be.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, String> {
}
