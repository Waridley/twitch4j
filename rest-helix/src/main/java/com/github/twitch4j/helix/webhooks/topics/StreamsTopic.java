package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.StreamList;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class StreamsTopic extends TwitchWebhookTopic<StreamList> {
	
	private String channelId;
	
	public StreamsTopic(@NonNull String channelId) {
		super("/streams", StreamList.class);
		this.channelId = channelId;
		queryParameters.put("user_id", channelId);
	}
	
}
