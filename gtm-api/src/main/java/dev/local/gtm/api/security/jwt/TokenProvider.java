package dev.local.gtm.api.security.jwt;

import dev.local.gtm.api.config.AppProperties;
import dev.local.gtm.api.security.AuthoritiesConstants;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Jwt Token 的工具类
 *
 * @author Peng Wang (wpcfan@gmail.com)
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";

    private String secretKey;

    private String refreshSecretKey;

    private long tokenValidityInMilliseconds;

    private long refreshTokenValidityInMilliseconds;

    private final AppProperties appProperties;

    @PostConstruct
    public void init() {
        this.secretKey = appProperties.getSecurity().getJwt().getSecret();
        this.refreshSecretKey = appProperties.getSecurity().getJwt().getRefershSecret();
        this.tokenValidityInMilliseconds = 1000 * appProperties.getSecurity().getJwt().getTokenValidityInSeconds();
        this.refreshTokenValidityInMilliseconds = 30 * 24 * 1000
                * appProperties.getSecurity().getJwt().getRefreshTokenValidityInSeconds();
    }

    public String createDevAdminToken() {
        val authorities = Arrays.asList(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER).stream()
                .collect(Collectors.joining(","));
        val now = (new Date()).getTime();
        val validity = new Date(now + this.tokenValidityInMilliseconds);
        return Jwts.builder()
            .setSubject("admin")
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(validity)
            .compact();
    }

    public String createToken(Authentication authentication) {
        val authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        val now = (new Date()).getTime();
        val validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .setExpiration(validity).compact();
    }

    public String createRefreshToken(Authentication authentication) {
        val authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        val now = (new Date()).getTime();
        val validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            .signWith(SignatureAlgorithm.HS512, refreshSecretKey)
            .setExpiration(validity).compact();
    }

    public Authentication getAuthentication(String token) {
        val claims = Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody();
        val authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        val principal = new User(claims.getSubject(), "", authorities);
        log.debug("授权对象：{}", principal);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public Authentication getAuthenticationFromRefreshToken(String token) {
        val claims = Jwts.parser()
            .setSigningKey(refreshSecretKey)
            .parseClaimsJws(token)
            .getBody();
        val authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        val principal = new User(claims.getSubject(), "", authorities);
        log.debug("授权对象：{}", principal);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("非法 JWT 签名");
            log.trace("非法 JWT 签名的 trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("非法 JWT token.");
            log.trace("非法 JWT token 的 trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("过期 JWT token");
            log.trace("过期 JWT token 的 trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("系统不支持的 JWT token");
            log.trace("系统不支持的 JWT token 的 trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token 压缩处理不正确");
            log.trace("JWT token 压缩处理不正确的 trace: {}", e);
        }
        return false;
    }

    public boolean validateRefreshToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(refreshSecretKey).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            log.info("非法 JWT 签名");
            log.trace("非法 JWT 签名的 trace: {}", e);
        } catch (MalformedJwtException e) {
            log.info("非法 JWT token.");
            log.trace("非法 JWT token 的 trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("过期 JWT token");
            log.trace("过期 JWT token 的 trace: {}", e);
        } catch (UnsupportedJwtException e) {
            log.info("系统不支持的 JWT token");
            log.trace("系统不支持的 JWT token 的 trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token 压缩处理不正确");
            log.trace("JWT token 压缩处理不正确的 trace: {}", e);
        }
        return false;
    }
}
