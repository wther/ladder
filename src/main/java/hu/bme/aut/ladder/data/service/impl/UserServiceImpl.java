package hu.bme.aut.ladder.data.service.impl;

import hu.bme.aut.ladder.data.entity.GameEntity;
import hu.bme.aut.ladder.data.entity.UserEntity;
import hu.bme.aut.ladder.data.repository.UserRepository;
import hu.bme.aut.ladder.data.service.UserService;
import hu.bme.aut.ladder.data.service.exception.UserActionNotAllowedException;
import java.util.List;
import java.util.Random;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
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
            
            // Make sure that user receives a unique name
            for(int i = 0; i < 200; i++){
                final String name = "Anonymous" + random.nextInt(100 + i);
            
                if(userRepository.findByNameIgnoreCase(name) == null){
                    user.setName(name);
                    break;
                }
            }
            
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
    public void setNameForUser(UserEntity user, String newName) throws UserActionNotAllowedException {
        
        if(StringUtils.isEmpty(newName)){
            throw new UserActionNotAllowedException("Empty name is not allowed for user");
        }
        
        final int lengthLimit = 25;        
        if(newName.length() > lengthLimit){
            throw new UserActionNotAllowedException("User name longer than " + lengthLimit + " is not allowed");
        }
        
        // Is this name taken?
        final UserEntity userInDb = userRepository.findByNameIgnoreCase(newName);
        if(userInDb != null && !userInDb.equals(user)){
            throw new UserActionNotAllowedException("Sorry, this name is already taken.");
        }
        
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserPage(UserEntity user) {
        
        if(user.getGame() == null){
            return LOBBY_PAGE;
        } else if(user.getGame().getGameState() == GameEntity.GameState.INITIALIZED){
            return ROOM_PAGE;
        } else {
            return GAME_PAGE;
        }        
    }
}
