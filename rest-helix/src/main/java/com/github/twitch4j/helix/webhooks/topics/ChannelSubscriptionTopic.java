package com.github.twitch4j.helix.webhooks.topics;

import com.github.twitch4j.helix.domain.SubscriptionList;
import javafx.util.Pair;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Optional;

/**
 * This webhook notifies you when:
 * <ul>
 *     <li>A payment has been processed for a subscription or unsubscription.
 *     <li>A user who is subscribed to a broadcaster notifies the broadcaster of their subscription in the chat.
 * </ul>
 */
@Getter
public class ChannelSubscriptionTopic extends TwitchWebhookTopic<SubscriptionList> {
    
    public static final String PATH = "/subscriptions/events";
    
    /**
     * @return The user ID of the broadcaster.
     */
	private String broadcasterId;
    
     /**
     * @return The ID of the subscribed user.
     */
	private Optional<String> userId;
    
    /**
     * This webhook notifies you when:
     * <ul>
     *     <li>A payment has been processed for a subscription or unsubscription.
     *     <li>A user who is subscribed to a broadcaster notifies the broadcaster of their subscription in the chat.
     * </ul>
     *
     * @param broadcasterId Required. User ID of the broadcaster. Must match the User ID in the Bearer token.
     * @param userId Optional. ID of the subscribed user. Currently only one user_id at a time can be queried.
     */
	public ChannelSubscriptionTopic(@NonNull String broadcasterId, String userId) {
		super(
		    PATH,
            SubscriptionList.class,
            Arrays.asList(
                new Pair<String, Object>("broadcaster_id", broadcasterId),
                new Pair<String, Object>("first", 1),
                new Pair<String, Object>("user_id", userId)
		    )
        );
		this.broadcasterId = broadcasterId;
		this.userId = Optional.ofNullable(userId);
  
	}
	
}
