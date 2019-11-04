package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.ExtensionTransactionList;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;

/**
 * Sends a notification when a new transaction is created for an extension.
 */
@Getter
public class ExtensionTransactionsTopic extends TwitchWebhookTopic<ExtensionTransactionList> {
    
    public static final String PATH = "/extensions/transactions";
    
    /**
     * @return The ID of the extension to listen to for transactions.
     */
	private final String extensionId;
    
    /**
     * Sends a notification when a new transaction is created for an extension.
     *
     * @param extensionId Required. The ID of the extension to listen to for transactions.
     */
	public ExtensionTransactionsTopic(@NonNull String extensionId) {
		super(
		    PATH,
            ExtensionTransactionList.class,
            Arrays.asList(
                new Pair<String, Object>("extension_id", extensionId),
                new Pair<String, Object>("first", 1)
            )
        );
		this.extensionId = extensionId;
	}
	
}
