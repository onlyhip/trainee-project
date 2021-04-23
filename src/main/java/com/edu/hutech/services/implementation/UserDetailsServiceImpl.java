package com.edu.hutech.services.implementation;

import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import com.edu.hutech.entities.ClassAdmin;
import com.edu.hutech.entities.Role;
import com.edu.hutech.repositories.ClassAdminRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService  {

    @Autowired
    private ClassAdminRepository classAdminRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // User user = userRepository.findByAccountClassAdmin(username);
        ClassAdmin admin = classAdminRepository.findByAccount(username);
        if(admin == null) { // if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        // Set<Role> roles = user.getRoles();
        Set<Role> roles = admin.getRoles();
        for (Role role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
        }

        return new org.springframework.security.core.userdetails.User(
                admin.getAccount(), admin.getPassword(), grantedAuthorities); // has change
    }

}
