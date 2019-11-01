package com.github.twitch4j.helix.webhooks.topics;

import lombok.Getter;

import java.util.TreeMap;

public abstract class TwitchWebhookTopic<T> extends WebhookTopic {
	
	private static final String BASE_URL = "https://api.twitch.tv/helix";
	
	@Getter
	private Class<T> type;
	
	public TwitchWebhookTopic(String path, Class<T> type) {
		super(BASE_URL, path, new TreeMap<>());
		this.type = type;
	}
	
}
