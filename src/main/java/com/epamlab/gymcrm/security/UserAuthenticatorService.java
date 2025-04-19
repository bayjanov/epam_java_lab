package com.epamlab.gymcrm.security;

import com.epamlab.gymcrm.trainee.repository.TraineeRepository;
import com.epamlab.gymcrm.trainer.repository.TrainerRepository;
import com.epamlab.gymcrm.user.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
        import org.springframework.stereotype.Service;

@Service
public class UserAuthenticatorService implements UserDetailsService {

    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Autowired
    public UserAuthenticatorService(TraineeRepository traineeRepository,
                                    TrainerRepository trainerRepository) {
        this.traineeRepository = traineeRepository;
        this.trainerRepository = trainerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = traineeRepository.findByUsername(username)
                .map(t -> (User) t)
                .orElseGet(() -> trainerRepository.findByUsername(username)
                        .map(t -> (User) t)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isActive(), true, true, true,
                java.util.Collections.emptyList() // no authorities yet, as role-based access not implmented
        );
    }
}
