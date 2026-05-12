package co.edu.uniquindio.application.scheduler;

import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler que actualiza automáticamente el estado de las reservas vencidas.
 *
 * Reglas:
 * - CONFIRMED con checkOut anterior a la fecha/hora actual -> COMPLETED.
 * - PENDING con checkIn anterior o igual a la fecha/hora actual -> REJECTED.
 *
 * Se ejecuta cada 30 minutos.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingStatusScheduler {

    private final BookingRepository bookingRepository;

    /**
     * Actualiza automáticamente los estados de reservas vencidas.
     * Se ejecuta cada 30 minutos (1800000 ms).
     */
    @Scheduled(fixedDelay = 1800000)
    @Transactional
    public void updateExpiredBookingsStatuses() {
        try {
            LocalDateTime now = LocalDateTime.now();

            int completedCount = updateConfirmedBookingsToCompleted(now);
            int rejectedCount = updatePendingBookingsToRejected(now);

            if (completedCount == 0 && rejectedCount == 0) {
                log.debug("No hay bookings vencidas para actualizar");
                return;
            }

            log.info(
                    "Actualización automática de bookings finalizada. COMPLETED: {}, REJECTED por vencimiento: {}",
                    completedCount,
                    rejectedCount
            );
        } catch (Exception e) {
            log.error("Error al actualizar bookings vencidas: {}", e.getMessage(), e);
        }
    }

    private int updateConfirmedBookingsToCompleted(LocalDateTime now) {
        List<Booking> expiredConfirmedBookings = bookingRepository.findByBookingStateAndCheckOutBefore(
                BookingState.CONFIRMED,
                now
        );

        if (expiredConfirmedBookings.isEmpty()) {
            return 0;
        }

        expiredConfirmedBookings.forEach(booking -> {
            booking.setBookingState(BookingState.COMPLETED);
            log.debug("Booking {} actualizada de CONFIRMED a COMPLETED", booking.getId());
        });

        bookingRepository.saveAll(expiredConfirmedBookings);
        return expiredConfirmedBookings.size();
    }

    private int updatePendingBookingsToRejected(LocalDateTime now) {
        List<Booking> expiredPendingBookings = bookingRepository.findByBookingStateAndCheckInLessThanEqual(
                BookingState.PENDING,
                now
        );

        if (expiredPendingBookings.isEmpty()) {
            return 0;
        }

        expiredPendingBookings.forEach(booking -> {
            booking.setBookingState(BookingState.REJECTED);
            log.debug("Booking {} actualizada de PENDING a REJECTED por vencimiento", booking.getId());
        });

        bookingRepository.saveAll(expiredPendingBookings);
        return expiredPendingBookings.size();
    }

    /**
     * Método alternativo que se puede ejecutar on-demand.
     * Útil para testing o actualizaciones manuales.
     */
    @Transactional
    public void updateExpiredBookingsStatusesNow() {
        try {
            LocalDateTime now = LocalDateTime.now();

            int completedCount = updateConfirmedBookingsToCompleted(now);
            int rejectedCount = updatePendingBookingsToRejected(now);

            log.info(
                    "Actualización on-demand finalizada. COMPLETED: {}, REJECTED por vencimiento: {}",
                    completedCount,
                    rejectedCount
            );
        } catch (Exception e) {
            log.error("Error en actualización on-demand: {}", e.getMessage(), e);
        }
    }
}