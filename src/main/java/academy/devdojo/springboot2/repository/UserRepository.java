package academy.devdojo.springboot2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import academy.devdojo.springboot2.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    public User findByUsername(String username);
    
}
