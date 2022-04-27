package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import usi.si.seart.exception.UserNotFoundException;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.UserRepository;

import java.util.List;

public interface UserService {

    User create(User user);
    void update(User user);
    List<User> getAll(Integer page, Integer pageSize, String column);
    User getWithId(Long id);
    User getWithEmail(String email);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class UserServiceImpl implements UserService {

        PasswordEncoder passwordEncoder;
        UserRepository userRepository;

        @Override
        public User create(User user) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.USER);
            return userRepository.save(user);
        }

        @Override
        public void update(User user) {
            userRepository.save(user);
        }

        @Override
        public List<User> getAll(Integer page, Integer pageSize, String column) {
            Sort sort = Sort.by(column).ascending();
            Pageable pageable = PageRequest.of(page, pageSize, sort);
            return userRepository.findAll(pageable).getContent();
        }

        @Override
        public User getWithId(Long id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("id", id));
        }

        @Override
        public User getWithEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("email", email));
        }
    }
}
