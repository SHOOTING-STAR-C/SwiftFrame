package com.star.swiftAi.client;

import com.star.swiftAi.enums.AiProviderEnum;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * AI客户端管理器
 * 统一管理多个AI客户端实例
 */
@RequiredArgsConstructor
public class AiClientManager {
    private final Map<String, AiClient> clients;
    private final AiProviderEnum defaultProvider;

    public AiClient getClient() {
        return getClient(defaultProvider.getCode());
    }

    public AiClient getClient(String providerCode) {
        return Optional.ofNullable(clients.get(providerCode.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Provider not found: " + providerCode));
    }

    public AiClient getClient(AiProviderEnum provider) {
        return getClient(provider.getCode());
    }

    public boolean hasClient(String providerCode) {
        return clients.containsKey(providerCode.toLowerCase());
    }

    public Map<String, AiClient> getAllClients() {
        return new HashMap<>(clients);
    }
}
