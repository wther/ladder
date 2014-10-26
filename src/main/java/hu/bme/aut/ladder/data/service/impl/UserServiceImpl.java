package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.BoardRepository;
import hu.bme.aut.ladder.data.repository.UserRepository;
import hu.bme.aut.ladder.data.service.UserService;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link UserService} interface.
 * 
 * @author Barnabas
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {
        
    /**
     * User repository
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public UserEntity findOrCreateUser(String sessionId) {
        
        UserEntity userInDB = userRepository.findBySessionId(sessionId);
        
        if(userInDB == null){
            UserEntity user = new UserEntity();
            user.setSessionId(sessionId);
            user.setName("Anonymous");
            userRepository.save(user);
            return user;
        } else {
            return userInDB;
        }
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }
}
