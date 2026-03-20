package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.commentDTO.ReplyDTO;
import co.edu.uniquindio.application.exceptions.ForbiddenException;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.exceptions.ValueConflictException;
import co.edu.uniquindio.application.mappers.ReplyMapper;
import co.edu.uniquindio.application.model.*;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.HostRepository;
import co.edu.uniquindio.application.repositories.ReplyRepository;
import co.edu.uniquindio.application.services.impl.ReplyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReplyServiceTest {
    /*
    @Mock
    private ReplyRepository replyRepository;
    @Mock
    private ReplyMapper replyMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private HostRepository hostRepository;

    @InjectMocks
    private ReplyServiceImpl replyService;

    private ReplyDTO replyDTO;
    private Comment comment;
    private Place place;
    private User owner;

    @BeforeEach
    void setup() {
        replyDTO = new ReplyDTO("Gracias por tu comentario"); // o el constructor real
        owner = new User();
        owner.setId("host123");

        place = new Place();
        place.setUser(owner);

        comment = new Comment();
        comment.setId("comment123");
        comment.setPlace(place);
    }
    // El comentario no existe
    @Test
    void create_WhenCommentNotFound_ShouldThrowResourceNotFound() {
        when(commentRepository.findById("comment123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> replyService.create("host123", "comment123", replyDTO));

        verify(commentRepository).findById("comment123");
        verifyNoMoreInteractions(replyRepository);
    }
    // Ya existe una respuesta al comentario
    @Test
    void create_WhenReplyAlreadyExists_ShouldThrowValueConflict() {
        when(commentRepository.findById("comment123")).thenReturn(Optional.of(comment));
        when(replyRepository.findByCommentId("comment123"))
                .thenReturn(Optional.of(new Reply()));

        assertThrows(ValueConflictException.class,
                () -> replyService.create("host123", "comment123", replyDTO));

        verify(replyRepository).findByCommentId("comment123");
        verify(replyRepository, never()).save(any());
    }
    // El usuario no es dueño del alojamiento
    @Test
    void create_WhenUserIsNotOwner_ShouldThrowForbiddenException() {
        User anotherUser = new User();
        anotherUser.setId("user999");
        place.setUser(anotherUser);

        when(commentRepository.findById("comment123")).thenReturn(Optional.of(comment));
        when(replyRepository.findByCommentId("comment123")).thenReturn(Optional.empty());

        assertThrows(ForbiddenException.class,
                () -> replyService.create("host123", "comment123", replyDTO));

        verify(replyRepository, never()).save(any());
    }
    //Todo correcto → se guarda la respuesta
    @Test
    void create_WhenValid_ShouldSaveReplySuccessfully() {
        Reply replyEntity = new Reply();

        when(commentRepository.findById("comment123")).thenReturn(Optional.of(comment));
        when(replyRepository.findByCommentId("comment123")).thenReturn(Optional.empty());
        when(replyMapper.toEntity(replyDTO)).thenReturn(replyEntity);

        replyService.create("host123", "comment123", replyDTO);

        verify(replyRepository).save(replyEntity);
        assertEquals(comment, replyEntity.getComment());
    }*/
}
