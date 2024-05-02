package com.dacm.hexagonal.infrastructure.startup;

import com.dacm.hexagonal.application.port.out.BookingRepository;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.infrastructure.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Component responsible for verifying and updating the status of bookings and their associated spaces
 * immediately after the Spring application context is fully refreshed. This ensures that all spaces are correctly
 * marked as available if their bookings have ended, maintaining system integrity and data accuracy from the start.
 */
@Component
public class BookingVerificationStartup {

    private final BookingRepository bookingRepository;
    private final SpaceRepository spaceRepository;

    /**
     * Constructor to inject the booking and space repositories. Using @Autowired annotation
     * ensures that Spring's dependency injection mechanism provides the necessary repository instances.
     *
     * @param bookingRepository Repository for managing booking operations.
     * @param spaceRepository   Repository for managing space operations.
     */
    @Autowired
    public BookingVerificationStartup(BookingRepository bookingRepository, SpaceRepository spaceRepository) {
        this.bookingRepository = bookingRepository;
        this.spaceRepository = spaceRepository;
    }

    /**
     * Event listener method triggered on the 'ContextRefreshedEvent' indicating that the Spring application
     * context has been completely initialized or refreshed. This event is typically triggered after all beans
     * are created and the application is fully set up.
     * <p>
     * The method calls verifyAndUpdateBookings to perform the initial check and update on booking statuses.
     */
    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Logic to verify bookings and update states
        verifyAndUpdateBookings();
    }

    /**
     * Private method to verify and update the statuses of bookings and spaces. It checks all bookings,
     * and if the booking has ended (end time is before the current time) and the space is still marked
     * as unavailable, it updates the space to available.
     * <p>
     * This method helps in keeping the system's data consistent by ensuring that spaces are not wrongfully
     * marked as unavailable due to past bookings that are no longer active.
     */
    private void verifyAndUpdateBookings() {
        List<BookingEntity> allBookings = bookingRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        allBookings.forEach(booking -> {
            if (booking.getEndTime().isBefore(now) && !booking.getSpace().isAvailable()) {
                SpaceEntity space = booking.getSpace();
                space.setAvailable(true);
                spaceRepository.save(space);
            }
        });
    }
}
