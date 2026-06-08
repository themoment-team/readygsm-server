package team.themoment.readygsmserver.global.config;

import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.SessionRepositoryFilter;

import java.util.EnumSet;

@Configuration
@EnableRedisHttpSession
public class HttpSessionConfig {

    private static final int SESSION_FILTER_ORDER = Integer.MIN_VALUE + 50;

    @Bean
    public FilterRegistrationBean<SessionRepositoryFilter<?>> springSessionRepositoryFilterRegistration(
            SessionRepositoryFilter<?> springSessionRepositoryFilter) {
        FilterRegistrationBean<SessionRepositoryFilter<?>> registration =
                new FilterRegistrationBean<>(springSessionRepositoryFilter);
        registration.setOrder(SESSION_FILTER_ORDER);
        registration.setDispatcherTypes(EnumSet.of(
                DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC));
        return registration;
    }
}
