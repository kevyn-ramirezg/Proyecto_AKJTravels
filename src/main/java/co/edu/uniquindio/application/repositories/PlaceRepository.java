package co.edu.uniquindio.application.repositories;

import co.edu.uniquindio.application.model.Place;
import co.edu.uniquindio.application.model.User;
import co.edu.uniquindio.application.model.enums.State;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaceRepository extends JpaRepository<Place, String>, JpaSpecificationExecutor<Place> {

    @Query("select p from Place p where p.user.id = :idUser")
    Page<Place> getPlaces(String idUser, Pageable pageable);

    List<Place> findByState(State state);

    @Query("SELECT a.user FROM Place a WHERE a.id = :placeId")
    Optional<User> findUserByPlaceId(@Param("placeId") String placeId);
}
