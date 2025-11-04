package apap.ti._5.accommodation_2306245794_be.repository;

import apap.ti._5.accommodation_2306245794_be.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {
}
