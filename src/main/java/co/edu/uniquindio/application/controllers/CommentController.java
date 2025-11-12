package co.edu.uniquindio.application.controllers;

import co.edu.uniquindio.application.dto.commentDTO.ReplyDTO;
import co.edu.uniquindio.application.dto.ResponseDTO;
import co.edu.uniquindio.application.services.CurrentUserService;
import co.edu.uniquindio.application.services.ReplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final ReplyService replyService;
    private final CurrentUserService currentUserService;

  @PostMapping("/{commentId}/reply/{userId}")
  public ResponseEntity<ResponseDTO<String>> reply(
    @PathVariable String commentId,
    @PathVariable String userId,
    @Valid @RequestBody ReplyDTO replyDTO
  ) throws Exception {
    // Usa exactamente la misma llamada al servicio que ya tienes
    replyService.create(userId, commentId, replyDTO);
    return ResponseEntity.ok(new ResponseDTO<>(false, "respuesta a comentario exitosa"));
  }
  @Deprecated
  @PostMapping("/{commentId}/reply/{idUser}")
  public ResponseEntity<ResponseDTO<String>> replyLegacy(
    @PathVariable String commentId,
    @PathVariable("idUser") String userId,
    @Valid @RequestBody ReplyDTO replyDTO) throws Exception {
    return reply(commentId, userId, replyDTO);
  }
}
