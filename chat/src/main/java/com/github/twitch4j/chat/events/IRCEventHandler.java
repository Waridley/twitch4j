package com.github.twitch4j.chat.events;

import com.github.philippheuer.events4j.EventManager;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.chat.events.channel.*;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.common.events.user.PrivateMessageEvent;
import lombok.Getter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * IRC Event Handler
 *
 * Listens for any irc triggered events and created the corresponding events for the EventDispatcher.
 */
@Getter
public class IRCEventHandler {
    
    /**
     * Twitch Client
     */
    private final TwitchChat twitchChat;
    
    /**
     * Event Manager
     */
    private final EventManager eventManager;
    
    /**
     * Constructor
     *
     * @param twitchChat The Twitch Chat instance
     */
    public IRCEventHandler(TwitchChat twitchChat) {
        this.twitchChat = twitchChat;
        this.eventManager = twitchChat.getEventManager();
        
        eventManager.onEvent(IRCMessageEvent.class).subscribe(this::onRawEvent);
        
    }
    
    
    private void onRawEvent(IRCMessageEvent event) {
        
        switch(event.getCommandType()) {
            case "PRIVMSG":
                if(event.getTags().containsKey("bits")) {
                    dispatchCheerEvent(event);
                } else if(event.getMessage().isPresent()) {
                    dispatchChannelMessageEvent(event);
                }
                break;
            case "WHISPER":
                dispatchWhisperEvent(event);
                break;
            case "USERNOTICE":
                if(event.getTags().containsKey("msg-id")) {
                    if(event.getTags().get("msg-d").equalsIgnoreCase("raid")) {
                        dispatchRaidEvent(event);
                    } else {
                        dispatchSubEvent(event);
                    }
                }
                break;
            case "CLEARCHAT":
                dispatchClearChatEvent(event);
                break;
            case "JOIN":
                if(event.getChannelName().isPresent() && event.getClientName().isPresent()) {
                    dispatchChannelJoinEvent(event);
                }
                break;
            case "PART":
                if(event.getChannelName().isPresent() && event.getClientName().isPresent()) {
                    dispatchChannelLeaveEvent(event);
                }
                break;
            case "MODE":
                if(event.getPayload().isPresent()) {
                    // Recieving Mod Status
                    if(event.getPayload().get().substring(1).startsWith("o")) {
                        dispatchModChangeEvent(event);
                    }
                }
                break;
            case "NOTICE":
                dispatchNoticeEvent(event);
                break;
            case "ROOMSTATE":
                dispatchChannelStateEvent(event);
                break;
        }
        
    }
    
    private void dispatchCheerEvent(IRCMessageEvent event) {
        // Load Info
        EventChannel channel = event.getChannel();
        EventUser user = event.getUser();
        String message = event.getMessage().orElse("");
        Integer bits = Integer.parseInt(event.getTags().get("bits"));
        
        // Dispatch Event
        eventManager.dispatchEvent(new CheerEvent(channel, user, message, bits));
    }
    
    private void dispatchChannelMessageEvent(IRCMessageEvent event) {
        // Load Info
        EventChannel channel = event.getChannel();
        EventUser user = event.getUser();
        
        // Dispatch Event
        if(event.getMessage().get().startsWith("\u0001ACTION ")) {
            // Action
            eventManager.dispatchEvent(new ChannelMessageActionEvent(channel, user, event.getMessage().get().substring(8), event.getClientPermissions()));
        } else {
            // Regular Message
            eventManager.dispatchEvent(new ChannelMessageEvent(channel, user, event.getMessage().get(), event.getClientPermissions()));
        }
    }
    
    private void dispatchWhisperEvent(IRCMessageEvent event) {
        // Load Info
        EventUser user = event.getUser();
        
        // Dispatch Event
        eventManager.dispatchEvent(new PrivateMessageEvent(user, event.getMessage().get(), event.getClientPermissions()));
    }
    
    private void dispatchRaidEvent(IRCMessageEvent event) {
        EventChannel channel = event.getChannel();
        EventUser raider = event.getUser();
        Integer viewers;
        try {
            viewers = Integer.parseInt(event.getTags().get("msg-param-viewerCount"));
        }
        catch(NumberFormatException ex) {
            viewers = 0;
        }
        eventManager.dispatchEvent(new RaidEvent(channel, raider, viewers));
    }
    
    private void dispatchSubEvent(IRCMessageEvent event) {
        if(event.getTags().get("msg-id").equalsIgnoreCase("sub") || event.getTags().get("msg-id").equalsIgnoreCase("resub")) {
            // Load Info
            EventChannel channel = event.getChannel();
            EventUser user = event.getUser();
            String subPlan = event.getTagValue("msg-param-sub-plan").get();
            boolean isResub = event.getTags().get("msg-id").equalsIgnoreCase("resub");
            Integer cumulativeMonths = (event.getTags().containsKey("msg-param-cumulative-months")) ? Integer.parseInt(event.getTags().get("msg-param-cumulative-months")) : 0;
            //according to the Twitch docs, msg-param-months is used only for giftsubs, which are handled below
            
            // twitch sometimes returns 0 months for new subs
            if(cumulativeMonths == 0) {
                cumulativeMonths = 1;
            }
            
            // check user's sub streak
            // Twitch API specifies that 0 is returned if the user chooses not to share their streak
            Integer streak = event.getTags().containsKey("msg-param-streak-months") ? Integer.parseInt(event.getTags().get("msg-param-streak-months")) : 0;
            
            // Dispatch Event
            eventManager.dispatchEvent(new SubscriptionEvent(channel, user, subPlan, event.getMessage(), cumulativeMonths, false, null, streak));
        }
        // Receive Gifted Sub
        else if(event.getTags().get("msg-id").equalsIgnoreCase("subgift")) {
            // Load Info
            EventChannel channel = event.getChannel();
            EventUser user = new EventUser(event.getTagValue("msg-param-recipient-id").get(), event.getTagValue("msg-param-recipient-user-name").get());
            EventUser giftedBy = event.getUser();
            String subPlan = event.getTagValue("msg-param-sub-plan").get();
            Integer subStreak = (event.getTags().containsKey("msg-param-months")) ? Integer.parseInt(event.getTags().get("msg-param-months")) : 1;
            
            // twitch sometimes returns 0 months for new subs
            if(subStreak == 0) {
                subStreak = 1;
            }
            
            // Dispatch Event
            eventManager.dispatchEvent(new SubscriptionEvent(channel, user, subPlan, event.getMessage(), subStreak, true, giftedBy, 0));
        }
        // Gift X Subs
        else if(event.getTags().get("msg-id").equalsIgnoreCase("submysterygift")) {
            // Load Info
            EventChannel channel = event.getChannel();
            EventUser user = event.getUser();
            String subPlan = event.getTagValue("msg-param-sub-plan").get();
            Integer subsGifted = (event.getTags().containsKey("msg-param-mass-gift-count")) ? Integer.parseInt(event.getTags().get("msg-param-mass-gift-count")) : 0;
            Integer subsGiftedTotal = (event.getTags().containsKey("msg-param-sender-count")) ? Integer.parseInt(event.getTags().get("msg-param-sender-count")) : 0;
            
            // Dispatch Event
            eventManager.dispatchEvent(new GiftSubscriptionsEvent(channel, user, subPlan, subsGifted, subsGiftedTotal));
        }
    }
    
    private void dispatchClearChatEvent(IRCMessageEvent event) {
        EventChannel channel = event.getChannel();
        if (event.getTags().containsKey("target-user-id")) { // ban or timeout
            if (event.getTags().containsKey("ban-duration")) { // timeout
                // Load Info
                EventUser user = event.getTargetUser();
                Integer duration = Integer.parseInt(event.getTagValue("ban-duration").get());
                String banReason = event.getTags().get("ban-reason") != null ? event.getTags().get("ban-reason").toString() : "";
                banReason = banReason.replaceAll("\\\\s", " ");
                UserTimeoutEvent timeoutEvent = new UserTimeoutEvent(channel, user, duration, banReason);
                
                // Dispatch Event
                eventManager.dispatchEvent(timeoutEvent);
            } else { // ban
                // Load Info
                EventUser user = event.getTargetUser();
                String banReason = event.getTagValue("ban-reason").orElse("");
                banReason = banReason.replaceAll("\\\\s", " ");
                UserBanEvent banEvent = new UserBanEvent(channel, user, banReason);
                
                // Dispatch Event
                eventManager.dispatchEvent(banEvent);
            }
        } else { // Clear chat event
            eventManager.dispatchEvent(new ClearChatEvent(channel));
        }
    }
    
    private void dispatchChannelJoinEvent(IRCMessageEvent event) {
        // Load Info
        EventChannel channel = event.getChannel();
        EventUser user = event.getUser();
        
        // Dispatch Event
        if (channel != null && user != null) {
            eventManager.dispatchEvent(new ChannelLeaveEvent(channel, user));
        }
    }
    
    private void dispatchChannelLeaveEvent(IRCMessageEvent event) {
        // Load Info
        EventChannel channel = event.getChannel();
        EventUser user = event.getUser();
        
        // Dispatch Event
        if (channel != null && user != null) {
            eventManager.dispatchEvent(new ChannelLeaveEvent(channel, user));
        }
    }
    
    private void dispatchModChangeEvent(IRCMessageEvent event) {
        // Load Info
        EventChannel channel = event.getChannel();
        EventUser user = new EventUser(null, event.getPayload().get().substring(3));
        
        // Dispatch Event
        eventManager.dispatchEvent(new ChannelModEvent(channel, user, event.getPayload().get().startsWith("+")));
    }
    
    private void dispatchNoticeEvent(IRCMessageEvent event) {
        EventChannel channel = event.getChannel();
        String messageId = event.getTagValue("msg-id").get();
        String message = event.getMessage().get();
        
        eventManager.dispatchEvent(new ChannelNoticeEvent(channel, messageId, message));
        
        switch(messageId) {
            case "host_on":
                String targetChannelName = message.substring(12, message.length() - 1);
                EventChannel targetChannel = new EventChannel(null, targetChannelName);
                eventManager.dispatchEvent(new HostOnEvent(channel, targetChannel));
                break;
            case "host_off":
                eventManager.dispatchEvent(new HostOffEvent(channel));
                break;
        }
    }
    
    private void dispatchChannelStateEvent(IRCMessageEvent event) {
        // getting Status on channel
        EventChannel channel = event.getChannel();
        Map<ChannelStateEvent.ChannelState, Object> states = new HashMap<ChannelStateEvent.ChannelState, Object>();
        if (event.getTags().size() > 2) {
            event.getTags().forEach((k, v) -> {
                switch (k) {
                    case "broadcaster-lang":
                        states.put(ChannelStateEvent.ChannelState.BROADCAST_LANG, (v != null) ? Locale.forLanguageTag(v) : v);
                        break;
                    case "emote-only":
                        states.put(ChannelStateEvent.ChannelState.EMOTE, v.equals("1"));
                        break;
                    case "followers-only":
                        states.put(ChannelStateEvent.ChannelState.FOLLOWERS, Long.parseLong(v));
                        break;
                    case "r9k":
                        states.put(ChannelStateEvent.ChannelState.R9K, v.equals("1"));
                        break;
                    case "slow":
                        states.put(ChannelStateEvent.ChannelState.SLOW, Long.parseLong(v));
                        break;
                    case "subs-only":
                        states.put(ChannelStateEvent.ChannelState.SUBSCRIBERS, v.equals("1"));
                        break;
                    default:
                        break;
                }
            });
        }
        eventManager.dispatchEvent(new ChannelStateEvent(channel, states));
    }
    
}
