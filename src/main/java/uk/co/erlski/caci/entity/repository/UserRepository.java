package uk.co.erlski.caci.entity.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.co.erlski.caci.entity.model.User;

public interface UserRepository extends JpaRepository<User, UUID> {

}
