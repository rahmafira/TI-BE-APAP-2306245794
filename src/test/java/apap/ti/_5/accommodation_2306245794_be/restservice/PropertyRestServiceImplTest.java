package apap.ti._5.accommodation_2306245794_be.restservice;

import apap.ti._5.accommodation_2306245794_be.model.Property;
import apap.ti._5.accommodation_2306245794_be.model.Room;
import apap.ti._5.accommodation_2306245794_be.model.RoomType;
import apap.ti._5.accommodation_2306245794_be.repository.AccommodationBookingRepository;
import apap.ti._5.accommodation_2306245794_be.repository.PropertyRepository;
import apap.ti._5.accommodation_2306245794_be.repository.RoomTypeRepository;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.AddRoomTypesRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.CreatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.property.UpdatePropertyRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.CreateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.request.room.UpdateRoomTypeRequestDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyDetailDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyHeaderDTO;
import apap.ti._5.accommodation_2306245794_be.restdto.response.property.PropertyResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyRestServiceImplTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private RoomTypeRepository roomTypeRepository;
    @Mock
    private AccommodationBookingRepository bookingRepository;
    
    @InjectMocks
    private PropertyRestServiceImpl propertyRestService;

    private Property property;
    private RoomType roomType;
    private Room room;
    private UUID ownerId;
    private CreateRoomTypeRequestDTO rtDtoStandard;
    private CreateRoomTypeRequestDTO rtDtoDeluxe;

    @BeforeEach
    void setUp() {
        ownerId = UUID.randomUUID();

        property = new Property();
        property.setPropertyId("HOT-ABCD-001");
        property.setPropertyName("Hotel Mock");
        property.setType(1);
        property.setActiveStatus(1);
        property.setTotalRoom(1);
        property.setOwnerId(ownerId);
        property.setListRoomType(new ArrayList<>());
        property.setListRoomType(new ArrayList<>());

        roomType = new RoomType();
        roomType.setRoomTypeId("RT-1");
        roomType.setName("Deluxe");
        roomType.setFloor(1);
        roomType.setCapacity(2);
        roomType.setPrice(1000000);
        roomType.setProperty(property);
        roomType.setListRoom(new ArrayList<>());

        room = new Room();
        room.setRoomId("ROOM-1");
        room.setRoomType(roomType);
        
        roomType.getListRoom().add(room);
        property.getListRoomType().add(roomType);
        
        rtDtoStandard = new CreateRoomTypeRequestDTO();
        rtDtoStandard.setName("Standard");
        rtDtoStandard.setFloor(2);
        rtDtoStandard.setNumberOfUnits(5);
        rtDtoStandard.setPrice(500000);
        rtDtoStandard.setCapacity(2);
        rtDtoStandard.setDescription("A nice room");
        rtDtoStandard.setFacility("TV, AC");
        
        rtDtoDeluxe = new CreateRoomTypeRequestDTO();
        rtDtoDeluxe.setName("Deluxe");
        rtDtoDeluxe.setFloor(1);
        rtDtoDeluxe.setNumberOfUnits(1);
    }

    @Test
    void whenGetAllProperties_shouldReturnListOfPropertyResponseDTO() {
        when(propertyRepository.findAllByOrderByUpdatedDateDesc()).thenReturn(List.of(property));
        List<PropertyResponseDTO> result = propertyRestService.getAllProperties();
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Hotel Mock", result.get(0).getPropertyName());
    }

    @Test
    void whenGetPropertyDetailById_withValidId_shouldReturnPropertyDetailDTO() {
        when(propertyRepository.findByIdWithRoomTypes("HOT-ABCD-001")).thenReturn(Optional.of(property));
        PropertyDetailDTO result = propertyRestService.getPropertyDetailById("HOT-ABCD-001");
        assertNotNull(result);
        assertEquals("Hotel Mock", result.getPropertyName());
        assertFalse(result.getListRoomType().isEmpty());
    }

    @Test
    void whenGetPropertyDetailById_withInvalidId_shouldThrowNotFound() {
        when(propertyRepository.findByIdWithRoomTypes(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.getPropertyDetailById("INVALID-ID");
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void whenGetPropertyHeader_withValidId_shouldReturnPropertyHeaderDTO() {
        when(propertyRepository.findById(property.getPropertyId())).thenReturn(Optional.of(property));
        PropertyHeaderDTO result = propertyRestService.getPropertyHeader(property.getPropertyId());
        assertNotNull(result);
        assertEquals(property.getPropertyName(), result.getPropertyName());
    }
    
    @Test
    void whenGetPropertyHeader_withInvalidId_shouldThrowNotFound() {
        when(propertyRepository.findById(anyString())).thenReturn(Optional.empty());
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.getPropertyHeader("INVALID-ID");
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
    
    @Test
    void whenGetPropertyByIdForUpdate_shouldReturnPropertyDetailDTO() {
        when(propertyRepository.findByIdWithRoomTypes(property.getPropertyId())).thenReturn(Optional.of(property));
        PropertyDetailDTO result = propertyRestService.getPropertyByIdForUpdate(property.getPropertyId());
        assertNotNull(result);
        assertEquals(property.getPropertyId(), result.getPropertyId());
    }

    @Test
    void whenCreateProperty_withValidData_shouldCreateAndReturnProperty() {
        CreatePropertyRequestDTO createDto = new CreatePropertyRequestDTO();
        createDto.setPropertyName("New Hotel");
        createDto.setType(1);
        createDto.setOwnerId(ownerId);
        createDto.setListRoomType(List.of(rtDtoStandard));
        createDto.setAddress("123 Mockingbird Lane");
        createDto.setProvince(1);
        createDto.setDescription("A new mock hotel");
        createDto.setOwnerName("Mock Owner");

        when(propertyRepository.count()).thenReturn(0L);
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> {
            Property p = invocation.getArgument(0);
            p.setPropertyId("HOT-ABCD-001");
            return p;
        });

        Property result = propertyRestService.createProperty(createDto);

        assertNotNull(result);
        assertEquals("New Hotel", result.getPropertyName());
        assertEquals(5, result.getTotalRoom());
        assertFalse(result.getListRoomType().isEmpty());
    }

    @Test
    void whenCreateProperty_withDuplicateRoomTypes_shouldThrowBadRequest() {
        CreatePropertyRequestDTO createDto = new CreatePropertyRequestDTO();
        createDto.setListRoomType(List.of(rtDtoDeluxe, rtDtoDeluxe));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.createProperty(createDto);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Duplicate combination of room type and floor found in the request"));
    }

    @Test
    void whenUpdateProperty_withValidData_shouldUpdateAndReturnProperty() {
        UpdateRoomTypeRequestDTO rtUpdateDto = new UpdateRoomTypeRequestDTO();
        rtUpdateDto.setRoomTypeId("RT-1");
        rtUpdateDto.setPrice(1200000);
        rtUpdateDto.setCapacity(3);
        rtUpdateDto.setDescription("Updated description");
        rtUpdateDto.setFacility("TV, AC, Mini Bar");
        
        UpdatePropertyRequestDTO updateDto = new UpdatePropertyRequestDTO();
        updateDto.setPropertyId("HOT-ABCD-001");
        updateDto.setPropertyName("Hotel Mock Updated");
        updateDto.setListRoomType(List.of(rtUpdateDto));
        updateDto.setAddress("456 Update Street");
        updateDto.setDescription("An updated property");
        
        when(propertyRepository.findById("HOT-ABCD-001")).thenReturn(Optional.of(property));
        when(roomTypeRepository.findById("RT-1")).thenReturn(Optional.of(roomType));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        
        Property result = propertyRestService.updateProperty(updateDto);
        
        assertEquals("Hotel Mock Updated", result.getPropertyName());
        assertEquals(1200000, roomType.getPrice());
        verify(roomTypeRepository, times(1)).save(roomType);
        verify(propertyRepository, times(1)).save(property);
    }
    
    @Test
    void whenUpdateProperty_withMismatchedRoomType_shouldThrowBadRequest() {
        UpdateRoomTypeRequestDTO rtUpdateDto = new UpdateRoomTypeRequestDTO();
        rtUpdateDto.setRoomTypeId("RT-2");
    
        UpdatePropertyRequestDTO updateDto = new UpdatePropertyRequestDTO();
        updateDto.setPropertyId("HOT-ABCD-001");
        updateDto.setListRoomType(List.of(rtUpdateDto));
        
        Property otherProperty = new Property();
        otherProperty.setPropertyId("OTHER-PROP");
        RoomType otherRoomType = new RoomType();
        otherRoomType.setRoomTypeId("RT-2");
        otherRoomType.setProperty(otherProperty);
        
        when(propertyRepository.findById("HOT-ABCD-001")).thenReturn(Optional.of(property));
        when(roomTypeRepository.findById("RT-2")).thenReturn(Optional.of(otherRoomType));
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.updateProperty(updateDto);
        });
        
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("RoomType does not belong to this property", exception.getReason());
    }

    @Test
    void whenUpdateProperty_withInvalidPropertyId_shouldThrowNotFound() {
        UpdatePropertyRequestDTO updateDto = new UpdatePropertyRequestDTO();
        updateDto.setPropertyId("INVALID-ID");
        
        when(propertyRepository.findById("INVALID-ID")).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.updateProperty(updateDto);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void whenUpdateProperty_withInvalidRoomTypeId_shouldThrowNotFound() {
        UpdateRoomTypeRequestDTO rtUpdateDto = new UpdateRoomTypeRequestDTO();
        rtUpdateDto.setRoomTypeId("INVALID-RT");
    
        UpdatePropertyRequestDTO updateDto = new UpdatePropertyRequestDTO();
        updateDto.setPropertyId("HOT-ABCD-001");
        updateDto.setListRoomType(List.of(rtUpdateDto));
        
        when(propertyRepository.findById("HOT-ABCD-001")).thenReturn(Optional.of(property));
        when(roomTypeRepository.findById("INVALID-RT")).thenReturn(Optional.empty());
        
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.updateProperty(updateDto);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("RoomType with ID INVALID-RT not found"));
    }

    @Test
    void whenSoftDeleteProperty_withNoFutureBookings_shouldSucceed() {
        when(propertyRepository.findByIdWithRoomTypes("HOT-ABCD-001")).thenReturn(Optional.of(property));
        when(bookingRepository.countByRoomRoomTypePropertyPropertyIdAndCheckInDateGreaterThanEqual(anyString(), any(LocalDateTime.class))).thenReturn(0L);

        propertyRestService.softDeleteProperty("HOT-ABCD-001");

        assertEquals(0, property.getActiveStatus());
        assertEquals(0, room.getActiveRoom());
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void whenSoftDeleteProperty_withFutureBookings_shouldThrowBadRequest() {
        when(propertyRepository.findByIdWithRoomTypes("HOT-ABCD-001")).thenReturn(Optional.of(property));
        when(bookingRepository.countByRoomRoomTypePropertyPropertyIdAndCheckInDateGreaterThanEqual(anyString(), any(LocalDateTime.class))).thenReturn(2L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.softDeleteProperty("HOT-ABCD-001");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Cannot deactivate property. There are 2 upcoming bookings."));
        verify(propertyRepository, never()).save(any(Property.class));
    }

    @Test
    void whenSoftDeleteProperty_withInvalidId_shouldThrowNotFound() {
        when(propertyRepository.findByIdWithRoomTypes("INVALID-ID")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.softDeleteProperty("INVALID-ID");
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void getPropertyPrefix_ShouldReturnHOTForType1() {
        String prefix = propertyRestService.getPropertyPrefix(1);
        assertEquals("HOT", prefix);
    }
    
    @Test
    void getPropertyPrefix_ShouldReturnVILForType2() {
        String prefix = propertyRestService.getPropertyPrefix(2);
        assertEquals("VIL", prefix);
    }

    @Test
    void getPropertyPrefix_ShouldReturnAPTForType3() {
        String prefix = propertyRestService.getPropertyPrefix(3);
        assertEquals("APT", prefix);
    }

    @Test
    void getPropertyPrefix_ShouldThrowExceptionForInvalidType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            propertyRestService.getPropertyPrefix(99);
        });
        assertEquals("Invalid property type", exception.getMessage());
    }

    @Test
    void addRoomTypesToProperty_ShouldSuccessfullyAddNewRoomTypes() {
        AddRoomTypesRequestDTO validAddRoomTypesDTO = new AddRoomTypesRequestDTO();
        validAddRoomTypesDTO.setPropertyId(property.getPropertyId());
        validAddRoomTypesDTO.setNewRoomTypes(Collections.singletonList(rtDtoStandard));
        
        when(propertyRepository.findByIdWithRoomTypes(property.getPropertyId()))
                .thenReturn(Optional.of(property));
        when(propertyRepository.save(any(Property.class))).thenReturn(property);
        
        propertyRestService.addRoomTypesToProperty(validAddRoomTypesDTO);

        assertEquals(2, property.getListRoomType().size()); 
        assertEquals(1 + 5, property.getTotalRoom()); 
        
        verify(propertyRepository, times(1)).save(property);
    }

    @Test
    void addRoomTypesToProperty_ShouldThrowNotFound_WhenPropertyNotFound() {
        AddRoomTypesRequestDTO invalidDto = new AddRoomTypesRequestDTO();
        invalidDto.setPropertyId("INVALID-ID");
        
        when(propertyRepository.findByIdWithRoomTypes(anyString())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.addRoomTypesToProperty(invalidDto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Property not found", exception.getReason());
    }

    @Test
    void addRoomTypesToProperty_ShouldThrowBadRequest_WhenDuplicateExistingKey() {
        rtDtoDeluxe.setName(roomType.getName());
        rtDtoDeluxe.setFloor(roomType.getFloor());
        
        AddRoomTypesRequestDTO dtoWithDuplicate = new AddRoomTypesRequestDTO();
        dtoWithDuplicate.setPropertyId(property.getPropertyId());
        dtoWithDuplicate.setNewRoomTypes(Collections.singletonList(rtDtoDeluxe));
        
        when(propertyRepository.findByIdWithRoomTypes(property.getPropertyId()))
                .thenReturn(Optional.of(property));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.addRoomTypesToProperty(dtoWithDuplicate);
        });

        String expectedKey = "Deluxe-1";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Duplicate combination of room type and floor already exists for this property: " + expectedKey));
    }

    @Test
    void addRoomTypesToProperty_ShouldThrowBadRequest_WhenDuplicateRequestKey() {
        CreateRoomTypeRequestDTO rt1 = new CreateRoomTypeRequestDTO();
        rt1.setName("Same");
        rt1.setFloor(3);
        
        CreateRoomTypeRequestDTO rt2 = new CreateRoomTypeRequestDTO();
        rt2.setName("Same");
        rt2.setFloor(3);
        
        AddRoomTypesRequestDTO dtoWithDuplicate = new AddRoomTypesRequestDTO();
        dtoWithDuplicate.setPropertyId(property.getPropertyId());
        dtoWithDuplicate.setNewRoomTypes(Arrays.asList(rt1, rt2));
        
        when(propertyRepository.findByIdWithRoomTypes(property.getPropertyId()))
                .thenReturn(Optional.of(property));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            propertyRestService.addRoomTypesToProperty(dtoWithDuplicate);
        });

        String expectedKey = "Same-3";
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Duplicate combination of room type and floor found in the request: " + expectedKey));
    }
}