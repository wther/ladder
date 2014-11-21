package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.UserRepository;
import hu.bme.aut.ladder.data.service.UserService;
import java.util.List;
import java.util.Random;
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
            
            Random random = new Random();
            user.setName("Anonymous" + random.nextInt(100));
            userRepository.save(user);
            return user;
        } else {
            return userInDB;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setNameForUser(UserEntity user, String newName) {
        user.setName(newName);
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUserReady(UserEntity user, boolean isReady) {
        user.setReady(isReady);
        userRepository.save(user);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }
}
