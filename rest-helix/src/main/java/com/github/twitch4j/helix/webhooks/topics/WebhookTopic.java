package com.github.twitch4j.helix.webhooks.topics;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class WebhookTopic {
	
	protected final String baseUrl;
	protected final String path;
	protected final Map<String, Object> queryParameters;
	
	private String url = null;
	
	private String buildUrl() {
		StringBuilder urlBuilder = new StringBuilder(baseUrl)
				.append(path);
		
		if(queryParameters != null) {
			boolean first = true;
			for(Map.Entry<String, Object> entry : queryParameters.entrySet()) {
				if(entry.getValue() != null) {
					urlBuilder
							.append(first ? "?" : "&")
							.append(entry.getKey())
							.append("=")
							.append(entry.getValue().toString());
					first = false;
				}
			}
		}
		
		return urlBuilder.toString();
	}
	
	@Override
	public String toString() {
		if(url == null) url = buildUrl();
		return url;
	}
}
