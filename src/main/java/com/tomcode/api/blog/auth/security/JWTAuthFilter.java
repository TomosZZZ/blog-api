package com.tomcode.api.blog.auth.security;

import com.tomcode.api.blog.auth.service.JWTService;
import com.tomcode.api.blog.auth.service.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtService.isValid(token)) {
                String email = jwtService.getEmail(token);
                String role = jwtService.getRole(token); // 👈 NOWE

                var authority =
                        new SimpleGrantedAuthority("ROLE_" + role);

                var authToken = new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        List.of(authority)
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(req)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authToken);
            }
        }

        chain.doFilter(req, res);
    }
}
