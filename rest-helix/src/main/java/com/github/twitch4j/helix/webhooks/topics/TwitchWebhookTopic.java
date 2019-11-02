package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.common.builder.TwitchAPIBuilder;
import javafx.collections.transformation.SortedList;
import javafx.util.Pair;
import lombok.EqualsAndHashCode;
import lombok.Getter;

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
    
}
