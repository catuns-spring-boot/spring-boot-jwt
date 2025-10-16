package xyz.catuns.spring.jwt.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

public class OrderedSecurityFilterChain implements SecurityFilterChain, Ordered {

    private final SecurityFilterChain delegate;
    private final int order;

    public OrderedSecurityFilterChain(SecurityFilterChain delegate, int order) {
        this.delegate = delegate;
        this.order = order;
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return delegate.matches(request);
    }

    @Override
    public List<Filter> getFilters() {
        return delegate.getFilters();
    }

    @Override
    public int getOrder() {
        return order;
    }
}