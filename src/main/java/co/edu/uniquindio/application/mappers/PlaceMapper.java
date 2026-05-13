package co.edu.uniquindio.application.mappers;

import co.edu.uniquindio.application.dto.placeDTO.CreatePlaceDTO;
import co.edu.uniquindio.application.dto.placeDTO.EditPlaceDTO;
import co.edu.uniquindio.application.model.Place;
import org.mapstruct.*;
//mappers
@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)public interface PlaceMapper {

    //convierte de dto a entidad y viceversa, crea automaticamente:
    @Mapping(target = "location.country", source = "country")
    @Mapping(target = "location.department", source = "department")
    @Mapping(target = "location.city", source = "city")
    @Mapping(target = "location.neighborhood", source = "neighborhood")
    @Mapping(target = "location.street", source = "street")
    @Mapping(target = "location.postalCode", source = "postalCode")
    @Mapping(target = "location.coordinates.latitude", source = "latitude")
    @Mapping(target = "location.coordinates.longitude", source = "longitude")
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "state", constant = "ACTIVE")
    @Mapping(target = "totalRatings", constant = "0")
    @Mapping(target = "averageRatings", constant = "0")
    @Mapping(target = "comments", expression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)




    Place toEntity(CreatePlaceDTO createPlaceDTO);
    CreatePlaceDTO toCreatePlaceDTO(Place place);
    void editPlaceFromDto(EditPlaceDTO editPlaceDTO, @MappingTarget Place place);

}
