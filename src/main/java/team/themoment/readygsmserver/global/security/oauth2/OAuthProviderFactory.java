package team.themoment.readygsmserver.global.security.oauth2;

import org.springframework.stereotype.Component;
import team.themoment.readygsmserver.global.security.oauth2.provider.OAuthProvider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthProviderFactory {

    private final Map<String, OAuthProvider> providerMap;

    public OAuthProviderFactory(List<OAuthProvider> providers) {
        this.providerMap = providers.stream()
                .collect(Collectors.toMap(OAuthProvider::getProviderName, Function.identity()));
    }

    public OAuthProvider getProvider(String providerName) {
        OAuthProvider provider = providerMap.get(providerName.trim().toLowerCase());
        if (provider == null) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 제공자입니다: " + providerName);
        }
        return provider;
    }
}
