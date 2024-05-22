package com.Douane.backendPFE.security.services;
import com.Douane.backendPFE.models.user.UserModel;
import com.Douane.backendPFE.repositories.userR.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserModel user=userRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found with email "+username));

        return UserDetailsImpl.build(user);
    }


}
