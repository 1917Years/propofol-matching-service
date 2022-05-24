package propofol.matchingservice.api.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import propofol.matchingservice.api.common.resolver.JwtResolver;
import propofol.matchingservice.api.common.resolver.TokenResolver;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TokenResolver tokenResolver;
    private final JwtResolver jwtResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {

        resolvers.add(tokenResolver);
        resolvers.add(jwtResolver);
    }
}
