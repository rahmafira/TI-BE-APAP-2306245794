package apap.ti._5.accommodation_2306245794_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "room_type")
public class RoomType {
    @Id
    private String roomTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", referencedColumnName = "propertyId", nullable = false)
    private Property property;
    
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price", nullable = false)
    private int price;

    @Lob
    @Column(name = "description", columnDefinition="TEXT")
    private String description;

    @Column(name = "capacity", nullable = false)
    private int capacity;

    @Column(name = "facility")
    private String facility;

    @Column(name = "floor")
    private int floor;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Room> listRoom;

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