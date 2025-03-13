package com.queiros.pipeline_jenkins.service;

import com.queiros.pipeline_jenkins.entity.UserEntity;
import com.queiros.pipeline_jenkins.exceptions.UserNotFoundException;
import com.queiros.pipeline_jenkins.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Test User");
        userEntity.setEmail("test@example.com");
        userEntity.setPassword("12345");
    }

    @Test
    @DisplayName("Should create and return a user successfully.")
    void test_Create_User_Sucess() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        UserEntity result = userService.createUser(userEntity);

        verify(userRepository, times(1)).save(any(UserEntity.class));

        Assertions.assertEquals(userEntity.getId(), result.getId());
        Assertions.assertEquals(userEntity.getName(), result.getName());
        Assertions.assertEquals(userEntity.getEmail(), result.getEmail());
        Assertions.assertEquals(userEntity.getPassword(), result.getPassword());

    }

    @Test
    @DisplayName("Should throw an exception when user creation fails")
    void test_Create_User_Fail() {

        doThrow(new RuntimeException("An unexpected error ocurred")).when(userRepository).save(any(UserEntity.class));

        Assertions.assertThrows(RuntimeException.class, () -> userService.createUser(userEntity));
    }

    @Test
    @DisplayName("Should return a list of users when users exists")
    void test_Return_ListUsers_Sucess() {

        List<UserEntity> mockUsersList = Arrays.asList(
                new UserEntity(1L, "User One", "user1@example.com", "password1"),
                new UserEntity(2L, "User Two", "user2@example.com", "password2")
        );

        when(userRepository.findAll()).thenReturn(mockUsersList);

        List<UserEntity> result = userService.listUsers();

        Assertions.assertFalse(result.isEmpty(), "User list cannot be empty");
        Assertions.assertEquals(2, result.size(), "There should be exactly 2 users.");

        Assertions.assertEquals("User One", result.get(0).getName());
        Assertions.assertEquals("User Two", result.get(1).getName());

        verify(userRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("Should returne a error of list empty")
    void test_Return_Empty_List_When_No_User_Exist() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserEntity> result = userService.listUsers();

        Assertions.assertTrue(result.isEmpty());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a updated user when user exists")
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {

        Long userId = 1L;
        UserEntity updatedUser = new UserEntity(userId, "Updated Name", "updated@example.com", "newpassword");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);

        Optional<UserEntity> result = userService.updateUser(updatedUser, userId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("Updated Name", result.get().getName());
        Assertions.assertEquals("updated@example.com", result.get().getEmail());
        Assertions.assertEquals("newpassword", result.get().getPassword());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(UserEntity.class));

    }

    @Test
    @DisplayName("Should throw exception UserNotFoundException when ID not found")
    void updatedUser_shouldThrowException_WhenUserNotFound() {

        Long userId = 99L;
        UserEntity updatedUser = new UserEntity(userId, "Updated Name", "updated@example.com", "newpassword");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.updateUser(updatedUser, userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Should delete user when ID exists")
    void deleteUser_shouldDeleteUserWhenIdExists() throws UserNotFoundException {

        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        Optional<UserEntity> result = userService.deleteUser(userId);

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(userEntity, result.get());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when ID does not exist")
    void shouldThrowExceptionWhenUserNotFound() {

        Long userId = 99L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(anyLong());
    }
}