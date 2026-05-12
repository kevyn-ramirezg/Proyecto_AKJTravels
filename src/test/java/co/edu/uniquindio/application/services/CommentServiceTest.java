package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.commentDTO.CreateCommentDTO;
import co.edu.uniquindio.application.exceptions.ForbiddenException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.mappers.CommentMapper;
import co.edu.uniquindio.application.mappers.ListCommentsMapper;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Comment;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.CommentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private ListCommentsMapper listCommentsMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("Debe crear comentario para una reserva completada del usuario")
    void shouldCreateCommentForCompletedOwnBooking() throws Exception {
        User guest = user("user-1");
        Place place = place("place-1");
        Booking booking = booking("booking-1", guest, place, BookingState.COMPLETED, LocalDateTime.now().minusDays(1));
        CreateCommentDTO dto = new CreateCommentDTO("Excelente alojamiento", 5);
        Comment mappedComment = new Comment();
        mappedComment.setComment("Excelente alojamiento");
        mappedComment.setRating(5);

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(guest));
        when(commentRepository.existsByBookingId("booking-1")).thenReturn(false);
        when(commentMapper.toEntity(dto)).thenReturn(mappedComment);
        when(commentRepository.findAverageRatingByPlaceId("place-1", null, null)).thenReturn(4.5);

        commentService.createComment("booking-1", "user-1", dto);

        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(booking, commentCaptor.getValue().getBooking());
        assertEquals(place, commentCaptor.getValue().getPlace());
        assertEquals(guest, commentCaptor.getValue().getUser());
        assertEquals(4.5, place.getAverageRatings());
        verify(placeRepository).save(place);
    }

    @Test
    @DisplayName("Debe completar automaticamente una reserva confirmada vencida antes de comentar")
    void shouldCompleteExpiredConfirmedBookingBeforeCommenting() throws Exception {
        User guest = user("user-1");
        Place place = place("place-1");
        Booking booking = booking("booking-1", guest, place, BookingState.CONFIRMED, LocalDateTime.now().minusDays(1));
        CreateCommentDTO dto = new CreateCommentDTO("Muy bueno", 4);
        Comment mappedComment = new Comment();

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(guest));
        when(commentRepository.existsByBookingId("booking-1")).thenReturn(false);
        when(commentMapper.toEntity(dto)).thenReturn(mappedComment);
        when(commentRepository.findAverageRatingByPlaceId("place-1", null, null)).thenReturn(4.0);

        commentService.createComment("booking-1", "user-1", dto);

        assertEquals(BookingState.COMPLETED, booking.getBookingState());
        verify(bookingRepository).save(booking);
        verify(bookingRepository).flush();
        verify(commentRepository).save(mappedComment);
    }

    @Test
    @DisplayName("Debe rechazar comentario si la reserva no ha finalizado")
    void shouldRejectCommentWhenBookingIsNotCompleted() {
        User guest = user("user-1");
        Place place = place("place-1");
        Booking booking = booking("booking-1", guest, place, BookingState.CONFIRMED, LocalDateTime.now().plusDays(1));

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class,
                () -> commentService.createComment("booking-1", "user-1", new CreateCommentDTO("Texto", 5)));

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar comentario si la reserva pertenece a otro usuario")
    void shouldRejectCommentWhenBookingBelongsToAnotherUser() {
        User bookingOwner = user("user-1");
        User currentUser = user("user-2");
        Place place = place("place-1");
        Booking booking = booking("booking-1", bookingOwner, place, BookingState.COMPLETED, LocalDateTime.now().minusDays(1));

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(userRepository.findById("user-2")).thenReturn(Optional.of(currentUser));

        assertThrows(ForbiddenException.class,
                () -> commentService.createComment("booking-1", "user-2", new CreateCommentDTO("Texto", 5)));

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar comentario duplicado para una misma reserva")
    void shouldRejectDuplicateCommentForBooking() {
        User guest = user("user-1");
        Place place = place("place-1");
        Booking booking = booking("booking-1", guest, place, BookingState.COMPLETED, LocalDateTime.now().minusDays(1));

        when(bookingRepository.findById("booking-1")).thenReturn(Optional.of(booking));
        when(userRepository.findById("user-1")).thenReturn(Optional.of(guest));
        when(commentRepository.existsByBookingId("booking-1")).thenReturn(true);

        assertThrows(ForbiddenException.class,
                () -> commentService.createComment("booking-1", "user-1", new CreateCommentDTO("Texto", 5)));

        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si la reserva no existe")
    void shouldThrowWhenBookingDoesNotExist() {
        when(bookingRepository.findById("missing-booking")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.createComment("missing-booking", "user-1", new CreateCommentDTO("Texto", 5)));
    }

    private User user(String id) {
        User user = new User();
        user.setId(id);
        user.setName("Usuario");
        return user;
    }

    private Place place(String id) {
        Place place = new Place();
        place.setId(id);
        place.setAverageRatings(0.0);
        return place;
    }

    private Booking booking(String id, User user, Place place, BookingState state, LocalDateTime checkOut) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setUser(user);
        booking.setPlace(place);
        booking.setBookingState(state);
        booking.setCheckIn(checkOut.minusDays(2));
        booking.setCheckOut(checkOut);
        booking.setGuest_number(2);
        booking.setCreatedAt(LocalDateTime.now().minusDays(3));
        return booking;
    }
}
