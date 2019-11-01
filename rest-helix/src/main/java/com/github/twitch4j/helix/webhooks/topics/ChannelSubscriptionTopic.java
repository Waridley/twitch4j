package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.SubscriptionList;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ChannelSubscriptionTopic extends TwitchWebhookTopic<SubscriptionList> {
	
	private String broadcasterId, userId;
	
	public ChannelSubscriptionTopic(@NonNull String broadcasterId, String userId) {
		super("/subscriptions/events", SubscriptionList.class);
		this.broadcasterId = broadcasterId;
		this.userId = userId;
		queryParameters.put("broadcaster_id", broadcasterId);
		queryParameters.put("first", 1);
		queryParameters.put("user_id", userId);
	}
	
}
