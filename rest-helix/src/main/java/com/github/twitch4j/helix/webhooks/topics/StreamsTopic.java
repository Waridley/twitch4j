package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.StreamList;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;

/**
 * Notifies when a stream changes; e.g., stream goes online or offline, the stream title changes, or the game changes.
 */
@Getter
public class StreamsTopic extends TwitchWebhookTopic<StreamList> {
    
    public static final String PATH = "/streams";
    
    /**
     * @return The user whose stream is monitored.
     */
	private String channelId;
    
    /**
     * Notifies when a stream changes; e.g., stream goes online or offline, the stream title changes, or the game changes.
     *
     * @param userId Specifies the user whose stream is monitored.
     */
	public StreamsTopic(@NonNull String userId) {
		super(
		    PATH,
            StreamList.class,
            Collections.singletonList(new Pair<String, Object>("user_id", userId))
        );
		this.channelId = userId;
	}
	
}
