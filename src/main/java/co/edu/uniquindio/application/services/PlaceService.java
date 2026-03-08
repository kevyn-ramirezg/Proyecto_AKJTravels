package co.edu.uniquindio.application.services;

import co.edu.uniquindio.application.dto.placeDTO.*;
import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.enums.Services;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PlaceService {

    String create(String id, CreatePlaceDTO createPlaceDTO) throws Exception;
    void edit(String id, EditPlaceDTO editPlaceDTO) throws Exception;
    void delete(String id) throws Exception;
    List<PlaceDTO> search(ListPlaceDTO listPlaceDTO, int page) throws Exception;
    List<Services> listAllServices(String id) throws Exception;
    PlaceStatsDTO stats(String placeId, LocalDate from, LocalDate to) throws Exception;
    List<PlaceDTO> listAllPlacesHost(String id, int page) throws Exception;
    PlaceDetailDTO get(String id) throws Exception;

    @Transactional
    Place setImages(String placeId, List<String> urls) throws Exception;
}