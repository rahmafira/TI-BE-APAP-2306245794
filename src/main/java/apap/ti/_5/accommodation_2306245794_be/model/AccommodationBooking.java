package apap.ti._5.accommodation_2306245794_be.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accommodation_booking")
public class AccommodationBooking {
    @Id
    private String bookingID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", referencedColumnName = "roomId", nullable = false)
    private Room room;

    @Column(name = "check_in_date", nullable = false)
    private LocalDateTime checkInDate;

    @Column(name = "check_out_date", nullable = false)
    private LocalDateTime checkOutDate;

    @Column(name = "total_days", nullable = false)
    private int totalDays;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;
    
    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "customer_id", nullable = false)
    private UUID customerID;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email", nullable = false)
    private String customerEmail;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "is_breakfast", nullable = false)
    private boolean isBreakfast;

    @Column(name = "refund")
    private int refund;

    @Column(name = "extra_pay")
    private int extraPay;

    @Column(name = "capacity", nullable = false)
    private int capacity;
    
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}