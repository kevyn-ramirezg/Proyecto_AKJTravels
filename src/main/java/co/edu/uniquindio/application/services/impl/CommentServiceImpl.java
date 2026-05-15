package co.edu.uniquindio.application.services.impl;

import co.edu.uniquindio.application.dto.commentDTO.CommentDTO;
import co.edu.uniquindio.application.dto.commentDTO.CreateCommentDTO;
import co.edu.uniquindio.application.exceptions.ForbiddenException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.mappers.CommentMapper;
import co.edu.uniquindio.application.mappers.ListCommentsMapper;
import co.edu.uniquindio.application.model.Booking;
import co.edu.uniquindio.application.model.Comment;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.BookingState;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.BookingRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.BookingService;
import co.edu.uniquindio.application.services.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PlaceRepository placeRepository;
    private final ListCommentsMapper listCommentsMapper;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> listComments(String id, int page) throws Exception {

        Pageable pageable = PageRequest.of(page, 10);

        Page<Comment> list = commentRepository.findAllByPlaceId(id, pageable);

        return list.stream()
                .map(listCommentsMapper::ToCommentDTO)
                .toList();
    }

    @Override
    @Transactional
    public void createComment(String bookingId, String userId, CreateCommentDTO createCommentDTO) throws Exception {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva"));

        if (booking.getBookingState() == BookingState.CONFIRMED
                && booking.getCheckOut().isBefore(LocalDateTime.now())) {
            log.info("Actualizando booking {} a COMPLETED automáticamente", bookingId);
            booking.setBookingState(BookingState.COMPLETED);
            bookingRepository.save(booking);
            bookingRepository.flush();
        }

        if (booking.getBookingState() != BookingState.COMPLETED) {
            throw new ForbiddenException("No puedes comentar si tu reserva aún no ha finalizado");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("No puedes comentar una reserva que no te pertenece");
        }

        if (commentRepository.existsByBookingId(bookingId)) {
            throw new ForbiddenException("Ya realizaste un comentario para esta reserva");
        }

        Comment comment = commentMapper.toEntity(createCommentDTO);
        comment.setBooking(booking);
        comment.setPlace(booking.getPlace());
        comment.setUser(user);

        commentRepository.save(comment);

        Double averageRating = commentRepository.avgRatingByPlaceId(booking.getPlace().getId());
        booking.getPlace().setAverageRatings(averageRating != null ? averageRating : 0.0);

        placeRepository.save(booking.getPlace());
    }

}
