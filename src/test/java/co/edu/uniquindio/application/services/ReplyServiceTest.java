package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.commentDTO.ReplyDTO;
import co.edu.uniquindio.application.exceptions.ForbiddenException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.ReplyMapper;
import co.edu.uniquindio.application.model.Comment;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Reply;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.ReplyRepository;
import co.edu.uniquindio.application.services.impl.ReplyServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {

    @Mock
    private ReplyRepository replyRepository;

    @Mock
    private ReplyMapper replyMapper;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ReplyServiceImpl replyService;

    @Test
    @DisplayName("Debe crear respuesta cuando el usuario actual es el anfitrion del alojamiento")
    void shouldCreateReplyWhenUserOwnsPlace() {
        Comment comment = comment("comment-1", "host-1");
        ReplyDTO dto = new ReplyDTO("Gracias por tu comentario");
        Reply mappedReply = new Reply();
        mappedReply.setReply("Gracias por tu comentario");

        when(commentRepository.findById("comment-1")).thenReturn(Optional.of(comment));
        when(replyRepository.findByCommentId("comment-1")).thenReturn(Optional.empty());
        when(replyMapper.toEntity(dto)).thenReturn(mappedReply);

        replyService.create("host-1", "comment-1", dto);

        assertEquals(comment, mappedReply.getComment());
        verify(replyRepository).save(mappedReply);
    }

    @Test
    @DisplayName("Debe rechazar respuesta si el comentario no existe")
    void shouldRejectReplyWhenCommentDoesNotExist() {
        when(commentRepository.findById("missing-comment")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> replyService.create("host-1", "missing-comment", new ReplyDTO("Respuesta")));

        verify(replyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar segunda respuesta al mismo comentario")
    void shouldRejectDuplicateReply() {
        Comment comment = comment("comment-1", "host-1");
        when(commentRepository.findById("comment-1")).thenReturn(Optional.of(comment));
        when(replyRepository.findByCommentId("comment-1")).thenReturn(Optional.of(new Reply()));

        assertThrows(ValueConflictException.class,
                () -> replyService.create("host-1", "comment-1", new ReplyDTO("Respuesta")));

        verify(replyRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar respuesta si el usuario actual no es el anfitrion")
    void shouldRejectReplyWhenUserDoesNotOwnPlace() {
        Comment comment = comment("comment-1", "host-1");
        when(commentRepository.findById("comment-1")).thenReturn(Optional.of(comment));
        when(replyRepository.findByCommentId("comment-1")).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class,
                () -> replyService.create("other-host", "comment-1", new ReplyDTO("Respuesta")));

        verify(replyRepository, never()).save(any());
    }

    private Comment comment(String commentId, String hostId) {
        User host = new User();
        host.setId(hostId);

        Place place = new Place();
        place.setUser(host);

        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPlace(place);
        return comment;
    }
}
