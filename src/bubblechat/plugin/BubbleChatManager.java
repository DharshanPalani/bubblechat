package bubblechat.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.TextDisplay;

public class BubbleChatManager {
	private final Map<UUID, List<TextDisplay>> bubble = new HashMap<>();
	
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

	public void add(UUID playerUUID, TextDisplay text) {
	    List<TextDisplay> texts = bubble.getOrDefault(playerUUID, new ArrayList<>());
	    texts.add(text);
	    
	    bubble.put(playerUUID, texts);
	}

	public List<TextDisplay> getBubbles(UUID playerUUID) {
		
		List<TextDisplay> stands = bubble.get(playerUUID);

		return stands;
	}
	
	public void removeAll(UUID playerUUID) {
	    List<TextDisplay> stands = bubble.get(playerUUID);
	    if (stands != null) {
	        List<TextDisplay> textsCopy = new ArrayList<>(stands);
	        
	        for(TextDisplay text : textsCopy) {
	            remove(playerUUID, text);
	        }
	    }
	}
	
	public Set<UUID> getAllPlayer() {
		return new HashSet<UUID>(bubble.keySet());
	}
   
	public void remove(UUID playerUUID, TextDisplay stand) {
	    List<TextDisplay> stands = bubble.get(playerUUID);
	    if (stands != null) {
	    	stand.remove();
	        stands.remove(stand);
	        if (stands.isEmpty()) {
	            bubble.remove(playerUUID);
	        }
	    }
	}
	
	public void reloadCleanUp() {
		for(UUID playerID : getAllPlayer()) {
			List<TextDisplay> texts = getBubbles(playerID);
			if(texts != null) {
				for(TextDisplay text : texts) {
					if(text != null && !text.isDead()) {
						text.remove();
					}
				}
			}
		}
		
		bubble.clear();
	}
}