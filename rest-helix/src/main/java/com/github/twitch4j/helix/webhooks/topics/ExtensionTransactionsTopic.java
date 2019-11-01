package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.ExtensionTransactionList;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ExtensionTransactionsTopic extends TwitchWebhookTopic<ExtensionTransactionList> {
	
	private final String extensionId;
	
	public ExtensionTransactionsTopic(@NonNull String extensionId) {
		super("/extensions/transactions", ExtensionTransactionList.class);
		this.extensionId = extensionId;
		queryParameters.put("extension_id", extensionId);
		queryParameters.put("first", 1);
	}
	
}
