package com.queiros.pipeline_jenkins.controller;


import com.queiros.pipeline_jenkins.entity.UserEntity;
import com.queiros.pipeline_jenkins.exceptions.UserNotFoundException;
import com.queiros.pipeline_jenkins.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")

    public ResponseEntity<?> createUser(@RequestBody UserEntity userEntity) {

        try {
            UserEntity createdUser = userService.createUser(userEntity);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error ocurred.");
        }

    }

    @GetMapping
    public ResponseEntity<?> listUsers() {

        try {
            List<UserEntity> usersList = userService.listUsers();

            if (usersList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("User list is empty");
            }

            return ResponseEntity.status(HttpStatus.OK).body(usersList);

        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error fetching user list");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestBody UserEntity userEntity, @PathVariable Long id) {

        try {
            Optional<UserEntity> updatedUser = userService.updateUser(userEntity, id);
            return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error ocurred.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {

        try {
            Optional<UserEntity> deletedUser = userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted sucessfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

}
