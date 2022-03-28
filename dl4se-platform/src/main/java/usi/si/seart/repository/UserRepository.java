package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
