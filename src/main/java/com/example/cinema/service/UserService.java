package com.example.cinema.service;

import com.example.cinema.exceptions.RoleNotFoundException;
import com.example.cinema.exceptions.UserNotFoundException;
import com.example.cinema.model.entities.Role;
import com.example.cinema.model.entities.User;
import com.example.cinema.persistence.RoleRepository;
import com.example.cinema.persistence.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public void saveClient(User user){
        Role role = roleRepository.findByRole("CLIENT").orElseThrow(() ->  new RoleNotFoundException("CLIENT"));
        user.addRole(role);

        userRepository.save(user);
    }

    public void saveAdmin(User user){
        Role role = roleRepository.findByRole("CLIENT").orElseThrow(() ->  new RoleNotFoundException("CLIENT"));
        user.addRole(role);

        role = roleRepository.findByRole("ADMIN").orElseThrow(() ->  new RoleNotFoundException("ADMIN"));
        user.addRole(role);

        userRepository.save(user);
    }

    public void saveRole(Role role){
        roleRepository.save(role);
    }

    public void addRoleToUser(long userId, long roleId){
        User user = userRepository.findById(userId).orElseThrow(() ->  new UserNotFoundException(userId));
        Role role = roleRepository.findById(roleId).orElseThrow(() ->  new RoleNotFoundException(roleId));

        user.addRole(role);
        userRepository.save(user);
    }

    public void deleteUser(long id){
        getUser(id).ifPresent(userRepository::delete);
    }

    public void deleteUser(String email){
        getUser(email).ifPresent(userRepository::delete);
    }

    public Optional<User> getUser(long id){
        return userRepository.findById(id);
    }

    public Optional<User> getUser(String email){
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
}
