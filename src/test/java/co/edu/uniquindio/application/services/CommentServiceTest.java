package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.commentDTO.CommentDTO;
import co.edu.uniquindio.application.dto.commentDTO.CreateCommentDTO;
import co.edu.uniquindio.application.exceptions.ResourceNotFoundException;
import co.edu.uniquindio.application.mappers.CommentMapper;
import co.edu.uniquindio.application.mappers.ListCommentsMapper;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.Comment;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.PlaceRepository;
import co.edu.uniquindio.application.repositories.CommentRepository;
import co.edu.uniquindio.application.repositories.UserRepository;
import co.edu.uniquindio.application.services.impl.CommentServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    /*
    @Mock CommentRepository commentRepository;
    @Mock
    PlaceRepository placeRepository;
    @Mock ListCommentsMapper listCommentsMapper;
    @Mock CommentMapper commentMapper;
    @Mock UserRepository userRepository;

    @InjectMocks CommentServiceImpl service;

    // -------- listComments()

    @Test
    @DisplayName("listComments(): vacío -> ResourceNotFoundException")
    void listCommentsEmptyThrowsNotFound() {
        when(commentRepository.findAllByPlaceId(eq("acc-1"), any(Pageable.class)))
                .thenReturn(Page.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.listComments("acc-1", 0));
    }

    /*@Test
    @DisplayName("listComments(): ok -> mapea y devuelve DTOs")
    void listCommentsMapsAndReturns() throws Exception {
        Comment entity = new Comment();
        when(commentRepository.findAllByPlaceId(eq("acc-1"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(entity)));

        LocalDateTime now = LocalDateTime.now();
        when(listCommentsMapper.toCommentDTO(any(Comment.class)))
                .thenReturn(new CommentDTO(
                        "Muy bien",   // comment
                        now,          // commentDate
                        5,            // rating
                        null          // user (se puede poner un UserCommentDTO si prefiere )
                ));

        var result = service.listComments("acc-1", 0);

        assertEquals(1, result.size());
        verify(listCommentsMapper, times(1)).ToCommentDTO(entity);
    }


    // -------- El test de createComment()ca
/*
    @Test
    @DisplayName("createComment(): alojamiento inexistente -> ResourceNotFound")
    void createCommentAccommodationNotFound() {
        when(placeRepository.findById("acc-1")).thenReturn(Optional.empty());

        var dto = new CreateCommentDTO("Buen lugar", 5, "u1");
        assertThrows(ResourceNotFoundException.class, () -> service.createComment("acc-1", dto));
    }

    @Test
    @DisplayName("createComment(): usuario inexistente -> ResourceNotFound")
    void createCommentUserNotFound() throws Exception {
        when(placeRepository.findById("acc-1")).thenReturn(Optional.of(new Place()));
        when(userRepository.findById("u1")).thenReturn(Optional.empty());

        var dto = new CreateCommentDTO("Buen lugar", 5, "u1");
        assertThrows(ResourceNotFoundException.class, () -> service.createComment("acc-1", dto));
    }*/
/*
    @Test
    @DisplayName("createComment(): ok -> mapea, setea relaciones y guarda")
    void createCommentSuccessSaves() throws Exception {
        Place acc = new Place(); acc.setId("acc-1");
        User user = new User(); user.setId("u1");

        when(placeRepository.findById("acc-1")).thenReturn(Optional.of(acc));
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(commentMapper.toEntity(any(CreateCommentDTO.class))).thenReturn(new Comment());

        var dto = new CreateCommentDTO("Excelente", 5, "u1");
        service.createComment("acc-1", dto);

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());

        Comment saved = captor.getValue();
        assertSame(acc, saved.getPlace());
        assertSame(user, saved.getUser());
    }*/
}
