package com.mc.imageranker;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Transactional
@Service
public class SSUDS implements UserDetailsService {

    private UserRepository userRepository;

    public SSUDS(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepository.findByUsername(username);
            if(user == null){
                return null;
            }
            return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(), myAuthorities(user));
        } catch(Exception e){
            throw new UsernameNotFoundException("User Not found");
        }
    }

    private Set<GrantedAuthority> myAuthorities(User user){
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for(Role role: user.getRoles()){
            GrantedAuthority granted = new SimpleGrantedAuthority(role.getRole());
            authorities.add(granted);
        }
        return authorities;
    }
}
