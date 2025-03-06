package com.queiros.pipeline_jenkins.service;

import com.queiros.pipeline_jenkins.entity.UserEntity;
import com.queiros.pipeline_jenkins.exceptions.UserNotFoundException;
import com.queiros.pipeline_jenkins.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public UserEntity createUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    public List<UserEntity> listUsers() {

        List<UserEntity> usersList = userRepository.findAll();

        return usersList;
    }

    public Optional<UserEntity> updateUser(UserEntity userEntity, Long id) throws UserNotFoundException {

        Optional<UserEntity> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        UserEntity updatedUser = existingUser.get();

        updatedUser.setName(userEntity.getName());
        updatedUser.setEmail(userEntity.getEmail());
        updatedUser.setPassword(userEntity.getPassword());

        userRepository.save(updatedUser);

        return Optional.of(updatedUser);

    }

    public Optional<UserEntity> deleteUser(Long id) throws UserNotFoundException {

        Optional<UserEntity> existingUser = userRepository.findById(id);

        if (existingUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        userRepository.deleteById(id);

        return existingUser;

    }

}
