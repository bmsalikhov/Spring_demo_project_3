package ru.syn.demo3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.syn.demo3.models.User;
import ru.syn.demo3.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(required = false) String title) {
        try {
            List<User> users = new ArrayList<>();
            if (title == null) {
                users.addAll(userRepository.findAll());
            } else {
                var example = new User();
                example.setName(title);
                users.addAll(userRepository.findAll(Example.of(example)));
            }
            if (users.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Optional<User>> getUserById(@PathVariable("id") int id) {
        var userData = userRepository.findById(id);
        return userData.map(user -> new ResponseEntity<>(userData, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NO_CONTENT));
    }

    @PostMapping("/users/create")
    public boolean createUser(@RequestBody User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @PutMapping("/users/update/{id}")
    public boolean updateUserName(@PathVariable("id") int id, @RequestBody Map<String, String> body) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            User user = userData.get();
            user.setName(body.get("newName"));
            user.setContacts(userData.get().getContacts());
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    @DeleteMapping("/users/delete/{id}")
    public boolean deleteUserById(@PathVariable("id") int id) {
        try {
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
