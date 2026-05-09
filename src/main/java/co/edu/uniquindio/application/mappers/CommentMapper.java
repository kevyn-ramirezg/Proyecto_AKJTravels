package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.commentDTO.CreateCommentDTO;
import co.edu.uniquindio.application.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface CommentMapper {

    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(source = "comment", target = "comment")
    @Mapping(source = "rating", target = "rating")
    Comment toEntity(CreateCommentDTO createCommentDTO);

}