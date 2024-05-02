package com.dacm.hexagonal.infrastructure.scheduler;

import com.dacm.hexagonal.application.port.out.BookingRepository;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.infrastructure.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled class to verify and update the status of bookings and associated spaces.
 * This class is responsible for executing a scheduled task that checks all bookings
 * stored in the database every hour. If a booking has ended and the space remains marked as unavailable,
 * it updates the status of the space to available and changes the booking's status to inactive.
 */
@Component
public class BookingVerificationScheduler {

    private final BookingRepository bookingRepository;
    private final SpaceRepository spaceRepository;

    /**
     * Constructor to inject dependencies for booking and space repositories.
     *
     * @param bookingRepository Repository for managing booking operations.
     * @param spaceRepository   Repository for managing space operations.
     */
    @Autowired
    public BookingVerificationScheduler(BookingRepository bookingRepository, SpaceRepository spaceRepository) {
        this.bookingRepository = bookingRepository;
        this.spaceRepository = spaceRepository;
    }

    /**
     * Scheduled task to check and update booking statuses. This method is triggered every hour.
     * It fetches all bookings from the database, checks if the booking end time has passed
     * and the space is still marked as unavailable, and if so, updates the space to available
     * and sets the booking as inactive.
     *
     * @Scheduled annotation marks this method to run at a fixed rate of one hour, ensuring
     * that bookings are regularly checked and updated without manual intervention.
     */
    @Scheduled(fixedRate = 3600000) // Ejecutar cada hora
    public void verifyAndUpdateBookings() {
        List<BookingEntity> allBookings = bookingRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        allBookings.forEach(booking -> {
            if (booking.getEndTime().isBefore(now) && !booking.getSpace().isAvailable()) {
                SpaceEntity space = booking.getSpace();
                space.setAvailable(true);
                booking.setActive(false);
                bookingRepository.save(booking);
                spaceRepository.save(space);
            }
        });
    }
}