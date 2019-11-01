package com.github.twitch4j.helix.webhooks;

import com.github.twitch4j.common.events.TwitchEvent;
import com.github.twitch4j.helix.webhooks.topics.TwitchWebhookTopic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TwitchWebhookEvent<T> extends TwitchEvent {
	
	private T payload;
	
	private TwitchWebhookTopic<T> topic;
	
}
