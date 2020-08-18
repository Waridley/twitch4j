package com.github.twitch4j.kraken;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.twitch4j.common.config.ProxyConfig;
import com.github.twitch4j.common.config.Twitch4JGlobal;
import com.github.twitch4j.common.feign.Twitch4jFeignSlf4jLogger;
import com.github.twitch4j.common.feign.interceptor.TwitchClientIdInterceptor;
import com.netflix.config.ConfigurationManager;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.hystrix.HystrixFeign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TwitchKrakenBuilder {

    /**
     * Client Id
     */
    @With
    private String clientId = Twitch4JGlobal.clientId;

    /**
     * Client Secret
     */
    @With
    private String clientSecret = Twitch4JGlobal.clientSecret;

    /**
     * User Agent
     */
    @With
    private String userAgent = Twitch4JGlobal.userAgent;

    /**
     * HTTP Request Queue Size
     */
    @With
    private Integer requestQueueSize = -1;

    /**
     * BaseUrl
     */
    private String baseUrl = "https://api.twitch.tv/kraken";

    /**
     * Default Timeout
     */
    @With
    private Integer timeout = 5000;

    @With
    private Integer uploadTimeout = 4 * 60 * 1000;

    /**
     * ProxyConfiguration
     */
    @With
    private ProxyConfig proxyConfig = null;

    /**
     * Initialize the builder
     *
     * @return Twitch Kraken Builder
     */
    public static TwitchKrakenBuilder builder() {
        return new TwitchKrakenBuilder();
    }

    /**
     * Twitch API Client (Kraken)
     *
     * @return TwitchKraken
     */
    public TwitchKraken build() {
        log.debug("Kraken: Initializing Module ...");

        // Hystrix
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.execution.isolation.thread.timeoutInMilliseconds", timeout);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.default.requestCache.enabled", false);
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.maxQueueSize", getRequestQueueSize());
        ConfigurationManager.getConfigInstance().setProperty("hystrix.threadpool.default.queueSizeRejectionThreshold", getRequestQueueSize());

        // Hystrix: Timeout modification for file uploads
        ConfigurationManager.getConfigInstance().setProperty("hystrix.command.TwitchKraken#uploadVideoPart(URI,String,String,int,byte[]).execution.isolation.thread.timeoutInMilliseconds", uploadTimeout);

        // Jackson ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        // - Modules
        mapper.findAndRegisterModules();

        // Create HttpClient with proxy
        okhttp3.OkHttpClient.Builder clientBuilder = new okhttp3.OkHttpClient.Builder();
        if (proxyConfig != null)
            proxyConfig.apply(clientBuilder);

        // Build
        TwitchKraken client = HystrixFeign.builder()
            .client(new OkHttpClient(clientBuilder.build()))
            .encoder(new JacksonEncoder(mapper))
            .decoder(new JacksonDecoder(mapper))
            .logger(new Twitch4jFeignSlf4jLogger(TwitchKraken.class))
            .logLevel(Logger.Level.FULL)
            .errorDecoder(new TwitchKrakenErrorDecoder(new JacksonDecoder()))
            .requestInterceptor(new TwitchClientIdInterceptor(this.clientId, this.userAgent))
            .options(new Request.Options(timeout / 3, TimeUnit.MILLISECONDS, timeout, TimeUnit.MILLISECONDS, true))
            .retryer(new Retryer.Default(500, timeout, 2))
            .target(TwitchKraken.class, baseUrl);

        return client;
    }
}
