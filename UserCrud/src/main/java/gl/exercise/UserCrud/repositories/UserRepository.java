package gl.exercise.UserCrud.repositories;

import gl.exercise.UserCrud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> getUserByToken(String token);

    @Query("SELECT u FROM User AS u WHERE u.externalId = :externalId AND u.isActive = True")
    Optional<User> findByExternalIdAndIsActive(@Param("externalId") String externalId);
}
