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
import co.edu.uniquindio.application.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PlaceRepository placeRepository;
    private final ListCommentsMapper listCommentsMapper;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<CommentDTO> listComments(String id, int page) throws Exception {

        Pageable pageable = PageRequest.of(page, 10);

        Page<Comment> list = commentRepository.findAllByPlaceId(id, pageable);

        // ✅ Si no hay comentarios, devuelves vacío (no es 404)
        return list.stream()
                .map(listCommentsMapper::ToCommentDTO)
                .toList();
    }

    @Override
    @Transactional
    public void createComment(String placeId, String userId, CreateCommentDTO createCommentDTO) throws Exception {

        Booking booking = bookingRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la reserva"));

        if (booking.getBookingState() != BookingState.COMPLETED) {
            throw new ForbiddenException("No puedes comentar si tu reserva aún no ha finalizado");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el usuario"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("No puedes comentar una reserva que no te pertenece");
        }

        if (commentRepository.existsByBookingId(placeId)) {
            throw new ForbiddenException("Ya realizaste un comentario para esta reserva");
        }

        Comment comment = commentMapper.toEntity(createCommentDTO);
        comment.setBooking(booking);
        comment.setPlace(booking.getPlace());
        comment.setUser(user);

        commentRepository.save(comment);

        Double averageRating = commentRepository.findAverageRatingByPlaceId(booking.getPlace().getId(), null, null);
        booking.getPlace().setAverageRatings(averageRating != null ? averageRating : 0.0);

        placeRepository.save(booking.getPlace());

    }

}
