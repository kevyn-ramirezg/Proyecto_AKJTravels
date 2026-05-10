package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.commentDTO.CommentDTO;
import co.edu.uniquindio.application.dto.userDTO.UserCommentDTO;
import co.edu.uniquindio.application.model.Comment;
import co.edu.uniquindio.application.model.Reply;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.repositories.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListCommentsMapper {

  private final ReplyRepository replyRepository;

  public CommentDTO ToCommentDTO(Comment comment) {
    if (comment == null) {
      return null;
    }

    return new CommentDTO(
            comment.getId(),
            comment.getComment(),
            comment.getCreatedAt(),
            comment.getRating(),
            mapUser(comment.getUser()),
            loadReply(comment.getId())
    );
  }

  private UserCommentDTO mapUser(User user) {
    if (user == null) {
      return null;
    }

    return new UserCommentDTO(user.getName(), user.getPhotoUrl());
  }

  private String loadReply(String commentId) {
    if (commentId == null || commentId.isBlank()) {
      return null;
    }

    return replyRepository.findByCommentId(commentId)
            .map(Reply::getReply)
            .orElse(null);
  }
}