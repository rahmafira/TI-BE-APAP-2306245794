package apap.ti._5.accommodation_2306245794_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "property")
public class Property {
    @Id
    private String propertyId;

    @Column(name = "property_name", nullable = false)
    private String propertyName;

    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "province", nullable = false)
    private int province;

    @Lob
    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "total_room", nullable = false)
    private int totalRoom;

    @Column(name = "active_status", nullable = false)
    private int activeStatus;

    @Column(name = "income")
    private int income;

    @OneToMany(mappedBy = "property", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RoomType> listRoomType;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

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