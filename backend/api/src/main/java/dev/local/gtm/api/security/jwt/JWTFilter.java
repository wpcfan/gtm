package dev.local.gtm.api.security.jwt;

import dev.local.gtm.api.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends GenericFilterBean {

    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        val httpServletRequest = (HttpServletRequest) servletRequest;
        val jwt = getToken(httpServletRequest);
        if (StringUtils.hasText(jwt) && this.tokenProvider.validateToken(jwt)) {
            val authentication = this.tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String getToken(HttpServletRequest request){
        val bearerToken = request.getHeader(appProperties.getSecurity().getAuthorization().getHeader());
        val prefix = appProperties.getSecurity().getJwt().getTokenPrefix();
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(prefix)) {
            return bearerToken.substring(prefix.length(), bearerToken.length());
        }
        val jwt = request.getParameter(appProperties.getSecurity().getAuthorization().getHeader());
        if (StringUtils.hasText(jwt)) {
            return jwt;
        }
        return null;
    }
}
