package com.iphayao.demo.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Cacheable(value = "user", key = "#id", unless = "#result==null")
    public Optional<User> retrieveUserById(Long id) {
        log.info("Retrieve User ID: {}", id);
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    @CachePut(value = "user", key = "#id")
    public Optional<User> updateUserById(Long id, User user) {
        Optional<User> userOpt = userRepository.findById(id);
        if(!userOpt.isPresent()) {
            return Optional.empty();
        }
        user.setId(id);
        return Optional.of(userRepository.save(user));
    }

    @CacheEvict(value = "user", allEntries = true)
    public boolean deleteUser(Long id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }
}
