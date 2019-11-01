package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.UserList;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class UsersTopic extends TwitchWebhookTopic<UserList> {
	
	private String userId;
	
	public UsersTopic(@NonNull String userId) {
		super("/users", UserList.class);
		this.userId = userId;
		queryParameters.put("id", userId);
	}
	
}
