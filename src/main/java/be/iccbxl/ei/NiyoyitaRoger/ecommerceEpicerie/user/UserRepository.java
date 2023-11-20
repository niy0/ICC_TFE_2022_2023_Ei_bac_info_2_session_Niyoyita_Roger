package be.iccbxl.ei.NiyoyitaRoger.ecommerceEpicerie.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM user u WHERE u.email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

}
