package com.example.springsecurity

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.filter.OrderedCharacterEncodingFilter
import org.springframework.boot.web.filter.OrderedHiddenHttpMethodFilter
import org.springframework.boot.web.filter.OrderedHttpPutFormContentFilter
import org.springframework.boot.web.filter.OrderedRequestContextFilter
import org.springframework.security.web.FilterChainProxy
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter
import org.springframework.test.context.junit4.SpringRunner

import javax.servlet.Filter

import static org.junit.Assert.assertEquals

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilterChainIT {

    @Autowired
    FilterChainProxy filterChainProxy;

    @Autowired
    List<Filter> filters;

    @Test
    void 'test main filter chain'() {
        assertEquals(5, filters.size());

        assertEquals(OrderedCharacterEncodingFilter, filters[0].getClass())
        assertEquals(OrderedHiddenHttpMethodFilter, filters[1].getClass())
        assertEquals(OrderedHttpPutFormContentFilter, filters[2].getClass())
        assertEquals(OrderedRequestContextFilter, filters[3].getClass())

        assertEquals("springSecurityFilterChain", filters[4].filterName)
    }

    @Test
    void 'test security filter chain order'() {
        assertEquals(2, filterChainProxy.getFilterChains().size());

        def chain = filterChainProxy.getFilterChains().get(1);

        assertEquals(chain.filters.size(), 11)

        assertEquals(WebAsyncManagerIntegrationFilter, chain.filters[0].getClass())
        assertEquals(SecurityContextPersistenceFilter, chain.filters[1].getClass())
    }

    @Test
    void 'test ignored patterns'() {
        def chain = filterChainProxy.getFilterChains().get(0);

        assertEquals("/css/**", chain.requestMatcher.requestMatchers[0].pattern);
        assertEquals("/js/**", chain.requestMatcher.requestMatchers[1].pattern);
        assertEquals("/images/**", chain.requestMatcher.requestMatchers[2].pattern);
    }
}
