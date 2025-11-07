package apap.ti._5.accommodation_2306245794_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "room")
public class Room {
    @Id
    private String roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", referencedColumnName = "roomTypeId", nullable = false)
    private RoomType roomType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "availability_status", nullable = false)
    private int availabilityStatus;

    @Column(name = "active_room", nullable = false)
    private int activeRoom;

    @Column(name = "maintenance_start")
    private LocalDateTime maintenanceStart;

    @Column(name = "maintenance_end")
    private LocalDateTime maintenanceEnd;

    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccommodationBooking> listBooking;
    
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    
    @PrePersist
    protected void onCreate() {
        if (this.createdDate == null) this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}