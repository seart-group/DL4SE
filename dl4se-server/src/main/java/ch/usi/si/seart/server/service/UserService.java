package ch.usi.si.seart.server.service;

import ch.usi.si.seart.exception.UserNotFoundException;
import ch.usi.si.seart.model.user.Role;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.User_;
import ch.usi.si.seart.repository.UserRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

public interface UserService {

    User create(User user);
    User update(User user);
    void delete(User user);
    Page<User> getAll(Specification<User> specification, Pageable pageable);
    User getWithId(Long id);
    User getWithUid(String uid);
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
        public User update(User user) {
            return userRepository.save(user);
        }

        @Override
        public void delete(User user) {
            userRepository.delete(user);
        }

        @Override
        public Page<User> getAll(Specification<User> specification, Pageable pageable) {
            return userRepository.findAll(specification, pageable);
        }

        @Override
        public User getWithId(Long id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(User_.id, id));
        }

        @Override
        public User getWithUid(String uid) {
            return userRepository.findByUid(uid)
                    .orElseThrow(() -> new UserNotFoundException(User_.uid, uid));
        }

        @Override
        public User getWithEmail(String email) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException(User_.email, email));
        }
    }
}
