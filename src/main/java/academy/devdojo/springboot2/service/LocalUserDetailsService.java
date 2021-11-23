package academy.devdojo.springboot2.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import academy.devdojo.springboot2.domain.User;
import academy.devdojo.springboot2.repository.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LocalUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User findByUsername = this.repository.findByUsername(username);
        return Optional.ofNullable(findByUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    
}
