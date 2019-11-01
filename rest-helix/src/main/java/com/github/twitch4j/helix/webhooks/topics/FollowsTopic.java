package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.FollowList;
import lombok.Getter;

@Getter
public class FollowsTopic extends TwitchWebhookTopic<FollowList> {
	
	private String fromId, toId;
	
	public FollowsTopic(String fromId, String toId) {
		super("/users/follows", FollowList.class);
		this.fromId = fromId;
		this.toId = toId;
		queryParameters.put("first", 1);
		queryParameters.put("from_id", fromId);
		queryParameters.put("to_id", toId);
	}
	
}
