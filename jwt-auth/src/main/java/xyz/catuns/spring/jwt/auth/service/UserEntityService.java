package xyz.catuns.spring.jwt.auth.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import xyz.catuns.spring.jwt.domain.entity.UserEntity;
import xyz.catuns.spring.jwt.domain.repository.UserEntityRepository;
import xyz.catuns.spring.jwt.auth.exception.EmailNotFoundException;

@Slf4j
public class UserEntityService<E extends UserEntity> implements UserDetailsService {

    protected final UserEntityRepository<E> userEntityRepository;

    public UserEntityService(UserEntityRepository<E> userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
        log.debug("initializing UserEntityService()");
    }

    /**
     * @param email the `email` identifying the user
     * @return UserDetails
     * @throws EmailNotFoundException if email is not found
     */
    @Override
    public UserEntity loadUserByUsername(String email) throws EmailNotFoundException {
        return userEntityRepository.findByEmail(email).orElseThrow(
                () -> new EmailNotFoundException(email)
        );
    }
}
