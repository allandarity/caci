package uk.co.erlski.caci.service;

import java.util.UUID;
import uk.co.erlski.caci.entity.model.User;

public interface UserService {

    User findUserById(UUID id);

    User createUser();

}
