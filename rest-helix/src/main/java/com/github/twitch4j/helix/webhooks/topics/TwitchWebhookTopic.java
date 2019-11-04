package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.FollowList;
import com.github.twitch4j.helix.webhooks.domain.WebhookNotification;
import com.github.twitch4j.helix.webhooks.domain.WebhookRequest;
import javafx.collections.transformation.SortedList;
import javafx.util.Pair;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@EqualsAndHashCode
public abstract class TwitchWebhookTopic<T> {
	
    // Helix base URL
	private static final String BASE_URL = "https://api.twitch.tv/helix";
    
    // The topic URL, returned by toString()
	private final String url;
    
    /**
     * @return The data class that notifications for this topic deserialize to
     */
	@Getter
	private Class<T> type;
    
    /**
     * Create a new topic starting with "https://api.twitch.tv/helix"
     *
     * @param path The path to the specific Helix API endpoint..
     * @param type The data class that notifications for this topic deserialize to.
     * @param queryParameters A list of the query parameters for this topic URL.
     *                        Will be sorted alphabetically, so performance will be higher if it is already sorted.
     */
	public TwitchWebhookTopic(String path, Class<T> type, List<Pair<String, Object>> queryParameters) {
	    this(BASE_URL, path, type, queryParameters);
    }
    
    /**
     * Override the base URL in case Twitch ever changes it or creates a new endpoint with a different URL.
     *
     * @param baseUrl The base URL of the endpoint.
     * @param path The path to the specific API endpoint.
     * @param type The data class that notifications for this topic deserialize to.
     * @param queryParameters A list of the query parameters for this topic URL.
     *                        Will be sorted alphabetically, so performance will be higher if it is already sorted.
     */
	public TwitchWebhookTopic(String baseUrl, String path, Class<T> type, List<Pair<String, Object>> queryParameters) {
		this.type = type;
		
		// Parameters must be in alphabetical order
		Collections.sort(queryParameters, new Comparator<Pair<String, Object>>() {
            @Override
            public int compare(Pair<String, Object> p1, Pair<String, Object> p2) {
                return p1.getKey().compareTo(p2.getKey());
            }
        });
		
        this.url = baseUrl + path + buildQuery(queryParameters);
	}
    
    /**
     * Create a new topic from an existing URL
     *
     * @param url The URL representing this topic.
     * @param type The data class that notifications for this topic deserialize to.
     */
	public TwitchWebhookTopic(String url, Class<T> type) {
	    this.url = url;
	    this.type = type;
    }
	
	// Generate the query string from the sorted list of parameters
    private String buildQuery(Iterable<Pair<String, Object>> params) {
        StringBuilder urlBuilder = new StringBuilder();
        
        if(params != null) {
            boolean first = true;
            for(Pair<String, Object> param : params) {
                if(param.getValue() != null) {
                    urlBuilder
                        .append(first ? "?" : "&")
                        .append(param.getKey())
                        .append("=")
                        .append(param.getValue().toString());
                    first = false;
                }
            }
        }
        
        return urlBuilder.toString();
    }
    
    /**
     * @return The URL associated with this topic
     */
    @Override
    public String toString() {
        return url;
    }
    
    public static TwitchWebhookTopic fromUrl(String url) throws URISyntaxException {
        if(url.startsWith(BASE_URL)) {
            URI uri = new URI(url);
            String[] splitQuery = uri.getRawQuery().split("&");
            List<Pair<String, String>> params = new ArrayList<>(splitQuery.length);
            for(String s : splitQuery) {
                String[] splitParam = s.split("=");
                params.add(new Pair<String, String>(splitParam[0], splitParam[1]));
            }
            switch(uri.getPath().replaceFirst("/helix", "")) {
                case(ChannelBanTopic.PATH): {
                    String broadcasterId = params.stream().filter(p -> "broadcaster_id".equalsIgnoreCase(p.getKey())).findAny().get().getValue();
                    String userId = params.stream().filter(p -> "user_id".equalsIgnoreCase(p.getKey())).findAny().orElse(null).getValue();
                    return new ChannelBanTopic(broadcasterId, userId);
                }
                case(ChannelSubscriptionTopic.PATH): {
                    String broadcasterId = params.stream().filter(p -> "broadcaster_id".equalsIgnoreCase(p.getKey())).findAny().get().getValue();
                    String userId = params.stream().filter(p -> "user_id".equalsIgnoreCase(p.getKey())).findAny().orElse(null).getValue();
                    return new ChannelSubscriptionTopic(broadcasterId, userId);
                }
                case(ExtensionTransactionsTopic.PATH): {
                    String extensionId = params.stream().filter(p -> "extension_id".equalsIgnoreCase(p.getKey())).findAny().get().getValue();
                    return new ExtensionTransactionsTopic(extensionId);
                }
                case(FollowsTopic.PATH): {
                    String fromId = params.stream().filter(p -> "from_id".equalsIgnoreCase(p.getKey())).findAny().orElse(null).getValue();
                    String toId = params.stream().filter(p -> "to_id".equalsIgnoreCase(p.getKey())).findAny().orElse(null).getValue();
                    return new FollowsTopic(fromId, toId);
                }
                case(ModeratorChangeTopic.PATH): {
                    String broadcasterId = params.stream().filter(p -> "broadcaster_id".equalsIgnoreCase(p.getKey())).findAny().get().getValue();
                    String userId = params.stream().filter(p -> "user_id".equalsIgnoreCase(p.getKey())).findAny().orElse(null).getValue();
                    return new ModeratorChangeTopic(broadcasterId, userId);
                }
                case(StreamsTopic.PATH): {
                    String userId = params.stream().filter(p -> "user_id".equalsIgnoreCase(p.getKey())).findAny().get().getValue();
                    return new StreamsTopic(userId);
                }
                case(UsersTopic.PATH): {
                    String userId = params.stream().filter(p -> "user_id".equalsIgnoreCase(p.getKey())).findAny().get().getValue();
                    return new StreamsTopic(userId);
                }
            }
        }
        return new UnknownTopic(url);
    }
    
    public static class UnknownTopic extends TwitchWebhookTopic<WebhookNotification> {
    
        public UnknownTopic(String url) {
            super(url, WebhookNotification.class);
        }
    }
    
}
