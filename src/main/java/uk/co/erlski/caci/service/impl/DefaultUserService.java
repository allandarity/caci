package uk.co.erlski.caci.service.impl;

import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.erlski.caci.entity.model.User;
import uk.co.erlski.caci.entity.repository.UserRepository;
import uk.co.erlski.caci.service.UserService;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findUserById(UUID id) {
        final var repo = userRepository.findById(id);
        return repo.orElse(null);
    }

    @Override
    @Transactional
    public User createUser() {
        return userRepository.saveAndFlush(new User());
    }

}
