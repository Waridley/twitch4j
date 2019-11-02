package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.ModeratorEventList;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * Notifies when a broadcaster adds or removes moderators.
 */
@Getter
public class ModeratorChangeTopic extends TwitchWebhookTopic<ModeratorEventList> {
    
    /**
     * @return The user ID of the broadcaster.
     */
	private String broadcasterId;
    
    /**
     * @return The user ID of the moderator added or removed.
     */
	private Optional<String> userId;
    
    /**
     * Notifies when a broadcaster adds or removes moderators.
     *
     * @param broadcasterId Required. Specifies the user ID of the broadcaster.
     * @param userId Optional. Specifies the user ID of the moderator added or removed.
     */
	public ModeratorChangeTopic(@NonNull String broadcasterId, String userId) {
		super(
		    "/moderation/moderators/events",
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
