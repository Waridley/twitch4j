package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.FollowList;
import javafx.util.Pair;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * Notifies when a follows event occurs.
 */
@Getter
public class FollowsTopic extends TwitchWebhookTopic<FollowList> {
    
    /**
     * @return The user who starts following someone.
     */
	private Optional<String> fromId;
    
    /**
     * @return The user who has a new follower.
     */
	private Optional<String> toId;
    
    /**
     * Notifies when a follows event occurs.
     * At least one of fromId and toId is required.
     *
     * @param fromId Optional. Specifies the user who starts following someone.
     * @param toId Optional. Specifies the user who has a new follower.
     */
	public FollowsTopic(String fromId, String toId) {
		super(
		    "/users/follows",
            FollowList.class,
            Arrays.asList(
                new Pair<String, Object>("first", 1),
                new Pair<String, Object>("from_id", fromId),
                new Pair<String, Object>("to_id", toId)
            )
        );
		if(fromId == null && toId == null) throw new NullPointerException("At least one of fromId and toId is required.");
		this.fromId = Optional.ofNullable(fromId);
		this.toId = Optional.ofNullable(toId);
	}
	
}
