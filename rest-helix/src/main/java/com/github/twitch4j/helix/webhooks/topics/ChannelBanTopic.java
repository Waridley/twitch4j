package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.ModeratorEventList;
import com.sun.javafx.UnmodifiableArrayList;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

/**
 * Notifies when a broadcaster bans or un-bans people in their channel.
 */
@Getter
public class ChannelBanTopic extends TwitchWebhookTopic<ModeratorEventList> {
    
    public static final String PATH = "/moderation/banned/events";
    
    /**
     * @return The ID of the channel for which to monitor ban events.
     */
	private String broadcasterId;
    
    /**
     * @return The user ID of the moderator added or removed.
     */
    private Optional<String> userId;
    
    /**
     * Notifies when a broadcaster bans or un-bans people in their channel.
     *
     * @param broadcasterId Required. The ID of the channel for which to monitor ban events.
     * @param userId Optional. Specifies the user ID of the moderator added or removed.
     */
	public ChannelBanTopic(@NonNull String broadcasterId, String userId) {
		super(
            PATH,
            ModeratorEventList.class,
            Arrays.asList(
                new Pair<String, Object>("broadcaster_id", broadcasterId),
                new Pair<String, Object>("first", 1),
                new Pair<String, Object>("user_id", userId)
            )
        );
		this.broadcasterId = broadcasterId;
		this.userId = Optional.ofNullable(userId);
	}
	
}
