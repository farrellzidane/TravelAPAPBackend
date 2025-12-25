package apap.ti._5.accommodation_2306275600_be.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String userId = tokenProvider.getUserIdFromToken(jwt);
                String username = tokenProvider.getUsernameFromToken(jwt);
                String role = tokenProvider.getRoleFromToken(jwt);

                // Normalize role to match Spring Security format (ROLE_XXX)
                String normalizedRole = normalizeRole(role);
                
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(normalizedRole)
                );

                // Create UserPrincipal with userId, username, and role
                UserPrincipal userPrincipal = new UserPrincipal(userId, username, role);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                logger.debug("Set authentication for user: {} with role: {}", username, role);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from Cookie or Authorization header
     * Priority: Cookie first, then Authorization header
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        logger.debug("Attempting to extract JWT from request to: {}", request.getRequestURI());
        
        // First try to get JWT from cookie (httpOnly cookie for security)
        Cookie[] cookies = request.getCookies();
        logger.debug("Cookies available: {}", cookies != null ? cookies.length : 0);
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.debug("Cookie name: {}, value length: {}", cookie.getName(), cookie.getValue() != null ? cookie.getValue().length() : 0);
                if ("jwt".equals(cookie.getName())) {
                    logger.debug("Found JWT cookie!");
                    return cookie.getValue();
                }
            }
        }
        
        // Fallback to Authorization header for backward compatibility
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            logger.debug("Found JWT in Authorization header");
            return bearerToken.substring(7);
        }
        
        logger.debug("No JWT token found in request");
        return null;
    }

    /**
     * Normalizes role to Spring Security format
     * Handles variations: "Superadmin" -> "ROLE_SUPERADMIN"
     */
    private String normalizeRole(String role) {
        if (role == null) {
            return "ROLE_GUEST";
        }
        
        // Handle typo variant "Accomodation Owner" -> "ACCOMMODATION_OWNER"
        if (role.equalsIgnoreCase("Accomodation Owner") || 
            role.equalsIgnoreCase("Accommodation Owner")) {
            return "ROLE_ACCOMMODATION_OWNER";
        }
        
        // Convert to uppercase and replace spaces with underscores
        String normalized = role.toUpperCase().replace(" ", "_");
        
        // Add ROLE_ prefix if not present
        if (!normalized.startsWith("ROLE_")) {
            normalized = "ROLE_" + normalized;
        }
        
        return normalized;
    }
}
