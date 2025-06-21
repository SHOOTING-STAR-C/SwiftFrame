package com.star.swiftlogin.service.impl;

import com.star.swiftlogin.domain.SwiftUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Override
    public SwiftUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return SwiftUserDetails.builder().username("test").phone("123456789").build();
    }
}
