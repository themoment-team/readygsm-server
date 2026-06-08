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

    @Bean
    public FilterRegistrationBean<SessionRepositoryFilter<?>> springSessionRepositoryFilterRegistration(
            SessionRepositoryFilter<?> springSessionRepositoryFilter) {
        FilterRegistrationBean<SessionRepositoryFilter<?>> registration =
                new FilterRegistrationBean<>(springSessionRepositoryFilter);
        registration.setOrder(Integer.MIN_VALUE + 50);
        registration.setDispatcherTypes(EnumSet.of(
                DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC));
        return registration;
    }
}
