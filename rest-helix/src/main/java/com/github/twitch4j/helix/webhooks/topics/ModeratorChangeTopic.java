package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.ModeratorEventList;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ModeratorChangeTopic extends TwitchWebhookTopic<ModeratorEventList> {
	
	private String broadcasterId, userId;
	
	public ModeratorChangeTopic(@NonNull String broadcasterId, String userId) {
		super("/moderation/moderators/events", ModeratorEventList.class);
		this.broadcasterId = broadcasterId;
		this.userId = userId;
		queryParameters.put("broadcaster_id", broadcasterId);
		queryParameters.put("first", 1);
		queryParameters.put("user_id", userId);
	}
	
}
