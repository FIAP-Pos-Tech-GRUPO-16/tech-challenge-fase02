package br.com.fiap.techchallengefase02.repository;

import br.com.fiap.techchallengefase02.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Page<User> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Optional<User> findByLogin(String login);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}
