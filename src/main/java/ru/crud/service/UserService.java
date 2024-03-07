package ru.crud.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.crud.model.Role;
import ru.crud.model.User;
import ru.crud.repository.UserRepository;


import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        return new User(user.getUsername(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities());
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public Collection<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void addUser(User user) {
        userRepository.findByUsername(user.getUsername()).ifPresentOrElse((r) -> {}, () -> {
            makeUserIfNot(user);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        });
    }

    @Transactional
    public void updateUser(User newUser) {
        User oldUser = userRepository.findById(newUser.getId()).orElseThrow(RuntimeException::new);
        oldUser.setUsername(newUser.getUsername());
        oldUser.setAge(newUser.getAge());
        oldUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        oldUser.setRoles(newUser.getRoles());
        makeUserIfNot(oldUser);
    }

    @Transactional
    public void deleteUser(String username) {
        userRepository.findByUsername(username).ifPresent((this::flushUser));
    }

    @PostConstruct
    public void initUsers() {
        Role user = roleService.getRole("ROLE_USER");
        Role admin = roleService.getRole("ROLE_ADMIN");
        userRepository.save(new User("admin", "admin", "admin@mail.ru",
                passwordEncoder.encode("admin"), 19, List.of(user, admin)));
        userRepository.save(new User("user", "user", "user@mail.ru",
                passwordEncoder.encode("user"), 19, List.of(user)));
    }


    private void flushUser(User user) {
        user.setRoles(null);
        userRepository.delete(user);
    }

    private void makeUserIfNot(User oldUser) {
        if(!oldUser.getStringRoles().contains(roleService.getRole("ROLE_USER").toString())) {
            oldUser.addRole(roleService.getRole("ROLE_USER"));
        }
    }
}
