package apap.ti._5.accommodation_2306245794_be;

import apap.ti._5.accommodation_2306245794_be.model.AccommodationBooking;
import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.model.Room;
import apap.ti._5.accommodation_2306245794_be.model.RoomType;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootApplication
public class Accommodation2306245794BeApplication {

    public static void main(String[] args) {
        SpringApplication.run(Accommodation2306245794BeApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner createDummyData(PropertyRepository propertyRepository, AccommodationBookingRepository bookingRepository) {
        return args -> {
            if (propertyRepository.count() > 0) {
                System.out.println("Database already contains data. Skipping dummy data generation.");
                return;
            }

            System.out.println("Generating dummy data...");
            Faker faker = new Faker(new Locale("id_ID"));
            List<Room> allRooms = new ArrayList<>();

            System.out.println("Generating dummy properties and rooms...");
            for (int i = 0; i < 5; i++) {
                Property property = new Property();
                int propertyType = faker.number().numberBetween(1, 4);
                String typePrefix = "";
                switch (propertyType) {
                    case 1: typePrefix = "HOT"; property.setPropertyName(faker.company().name() + " Hotel"); break;
                    case 2: typePrefix = "VIL"; property.setPropertyName("Villa " + faker.pokemon().name()); break;
                    case 3: typePrefix = "APT"; property.setPropertyName("Apartemen " + faker.address().streetName()); break;
                }
                property.setPropertyId(String.format("%s-%04d-%03d", typePrefix, faker.number().numberBetween(1,100), i+1));
                property.setType(propertyType);
                property.setAddress(faker.address().fullAddress());
                property.setProvince(faker.number().numberBetween(1, 34));
                property.setDescription(faker.lorem().paragraph(2));
                boolean isActive = faker.bool().bool();
            	property.setActiveStatus(isActive ? 1 : 0);
                property.setIncome(0); 
                property.setOwnerName(faker.name().fullName());
                property.setOwnerId(UUID.randomUUID());
                property.setListRoomType(new ArrayList<>());

                int totalRooms = 0;
                int numberOfRoomTypes = faker.number().numberBetween(2, 4);
                for (int j = 0; j < numberOfRoomTypes; j++) {
                    RoomType roomType = new RoomType();
                    roomType.setRoomTypeId(UUID.randomUUID().toString());
                    roomType.setName(faker.options().option("Standard Room", "Deluxe Room", "Suite", "Family Room"));
                    roomType.setPrice(faker.number().numberBetween(500, 2000) * 1000);
                    roomType.setDescription("A comfortable room with great facilities.");
                    roomType.setCapacity(faker.number().numberBetween(1, 5));
                    roomType.setFacility("AC, TV, Wi-Fi");
                    roomType.setFloor(faker.number().numberBetween(1, 10));
                    roomType.setListRoom(new ArrayList<>());
                    roomType.setProperty(property);

                    int numberOfRooms = faker.number().numberBetween(5, 15);
                    for (int k = 0; k < numberOfRooms; k++) {
                        Room room = new Room();
                        room.setRoomId(UUID.randomUUID().toString());
                        room.setName(String.format("%d%02d", roomType.getFloor(), k + 1));
                        room.setActiveRoom(1);
                        room.setAvailabilityStatus(1);
                        room.setRoomType(roomType);
                        roomType.getListRoom().add(room);
                        allRooms.add(room); 
                    }
                    totalRooms += numberOfRooms;
                    property.getListRoomType().add(roomType);
                }
                property.setTotalRoom(totalRooms);
                propertyRepository.save(property);
            }

            System.out.println("Generating dummy bookings...");
            List<AccommodationBooking> bookings = new ArrayList<>();
            for (int i = 0; i < 15; i++) { 
                AccommodationBooking booking = new AccommodationBooking();
                booking.setBookingID("BOOK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                
                Room randomRoom = allRooms.get(faker.number().numberBetween(0, allRooms.size()));
                booking.setRoom(randomRoom);

                LocalDateTime checkIn = LocalDateTime.now().plusDays(faker.number().numberBetween(1, 30));
                int totalDays = faker.number().numberBetween(1, 5);

                booking.setCheckInDate(checkIn);
                booking.setCheckOutDate(checkIn.plusDays(totalDays));
                booking.setTotalDays(totalDays);
                int totalPrice = randomRoom.getRoomType().getPrice() * totalDays;
                booking.setTotalPrice(totalPrice);
                booking.setStatus(faker.number().numberBetween(0, 5));
                booking.setCustomerID(UUID.randomUUID());
                booking.setCustomerName(faker.name().fullName());
                booking.setCustomerEmail(faker.internet().emailAddress());
                booking.setCustomerPhone(faker.phoneNumber().phoneNumber());
                booking.setBreakfast(faker.bool().bool());
                booking.setCapacity(randomRoom.getRoomType().getCapacity());

                bookings.add(booking);

                if (booking.getStatus() == 1) {
                    Property propertyOfBooking = randomRoom.getRoomType().getProperty();
                    propertyOfBooking.setIncome(propertyOfBooking.getIncome() + totalPrice);
                    propertyRepository.save(propertyOfBooking);
                }
            }
            bookingRepository.saveAll(bookings);

            System.out.println("Dummy data generation complete.");
        };
    }
}