package bubblechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.ArmorStand;

public class BubbleChatManager {
	private final Map<UUID, List<ArmorStand>> bubble = new HashMap<>();
	
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

	public void add(UUID playerUUID, ArmorStand stand) {
	    List<ArmorStand> stands = bubble.getOrDefault(playerUUID, new ArrayList<>());
	    stands.add(stand);
	    
	    bubble.put(playerUUID, stands);
	}

	public List<ArmorStand> getBubbles(UUID playerUUID) {
		
		List<ArmorStand> stands = bubble.get(playerUUID);

		return stands;
	}
	
	public void removeAll(UUID playerUUID) {
	    List<ArmorStand> stands = bubble.get(playerUUID);
	    if (stands != null) {
	        List<ArmorStand> standsCopy = new ArrayList<>(stands);
	        
	        for(ArmorStand stand : standsCopy) {
	            remove(playerUUID, stand);
	        }
	    }
	}
	
	public Set<UUID> getAllPlayer() {
		return new HashSet<UUID>(bubble.keySet());
	}
   
	public void remove(UUID playerUUID, ArmorStand stand) {
	    List<ArmorStand> stands = bubble.get(playerUUID);
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
			List<ArmorStand> stands = getBubbles(playerID);
			if(stands != null) {
				for(ArmorStand stand : stands) {
					if(stand != null && !stand.isDead()) {
						stand.remove();
					}
				}
			}
		}
		
		bubble.clear();
	}
}