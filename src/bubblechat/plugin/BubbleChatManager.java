package bubblechat.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class BubbleChatManager {
	private final Map<UUID, List<BubbleChat>> bubble = new HashMap<>();
	
	private boolean enabled = true;
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void toggle() {
		this.enabled = !this.enabled;
	}

	public void add(UUID playerUUID, BubbleChat bubbleChat) {
	    List<BubbleChat> bubbleChats = bubble.getOrDefault(playerUUID, new ArrayList<>());
	    bubbleChats.add(bubbleChat);
	    
	    bubble.put(playerUUID, bubbleChats);
	}

	public List<BubbleChat> getBubbles(UUID playerUUID) {
		
		List<BubbleChat> bubbleChats = bubble.get(playerUUID);

		return bubbleChats;
	}
	
	public void removeAll(UUID playerUUID) {
	    List<BubbleChat> texts = bubble.get(playerUUID);
	    if (texts != null) {
	        List<BubbleChat> bubbleChatCopy= new ArrayList<>(texts);
	        
	        for(BubbleChat bubbleChat : bubbleChatCopy) {
	            remove(playerUUID, bubbleChat);
	        }
	    }
	}

	public void pushUpOldMessage(UUID playerUUID) {
		List<BubbleChat> bubbleChats = bubble.get(playerUUID);
		
		List<BubbleChat> bubbleChatsCopy = new ArrayList<>(bubbleChats);
		
		for(BubbleChat bubbleChat: bubbleChatsCopy) {
			bubbleChat.decreaseTargetTextScale(40);
			bubbleChat.updateTargetYAxisOffset(0.7);
		}
	}
	
	public Set<UUID> getAllPlayer() {
		return new HashSet<UUID>(bubble.keySet());
	}
   
	public void remove(UUID playerUUID, BubbleChat bubbleChat) {
	    List<BubbleChat> bubbleChats = bubble.get(playerUUID);
	    if (bubbleChats != null) {
	    	bubbleChat.getTextDisplay().remove();
	        bubbleChats.remove(bubbleChat);
	        if (bubbleChats.isEmpty()) {
	            bubble.remove(playerUUID);
	        }
	    }
	}
	
	public void reloadCleanUp() {
		for(UUID playerID : getAllPlayer()) {
			List<BubbleChat> bubbleChats = getBubbles(playerID);
			if(bubbleChats != null) {
				for(BubbleChat bubbleChat : bubbleChats) {
					if(bubbleChat != null && !bubbleChat.getTextDisplay().isDead()) {
						bubbleChat.getTextDisplay().remove();
					}
				}
			}
		}
		
		bubble.clear();
	}
}