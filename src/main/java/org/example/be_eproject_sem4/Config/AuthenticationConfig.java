// package org.example.be_eproject_sem4.Config;

// import org.example.be_eproject_sem4.Security.CustomUserDetailsPasswordService;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.ProviderManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.crypto.password.PasswordEncoder;

// @Configuration
// public class AuthenticationConfig {

//     @Bean
//     public AuthenticationManager authenticationManager(
//             UserDetailsService userDetailsService,  // Tham số 1: UserDetailsService
//             PasswordEncoder passwordEncoder,        // Tham số 2: PasswordEncoder
//             CustomUserDetailsPasswordService passwordService) {  // Tham số 3: PasswordService (nếu có)

//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
//         authProvider.setUserDetailsPasswordService(passwordService);
//         authProvider.setPasswordEncoder(passwordEncoder);

//         return new ProviderManager(authProvider);
//     }
// }