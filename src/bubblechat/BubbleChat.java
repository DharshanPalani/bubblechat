package bubblechat;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class BubbleChat implements Listener  {
	private final Plugin plugin;
	
	private BubbleChatManager bubbleChatManager;;
	
	BubbleChat(Plugin plugin, BubbleChatManager bubbleChatManager) {
		this.plugin = plugin;
		this.bubbleChatManager = bubbleChatManager;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		if(!bubbleChatManager.isEnabled()) return;
		
		String message = event.getMessage();
		

		Bukkit.getScheduler().runTask(plugin, () -> {
			Player player = event.getPlayer();
			
			List<ArmorStand> stands = bubbleChatManager.getBubbles(player.getUniqueId());
			
			if(stands != null) {
			    System.out.println("Removing existing armor stands");
			    bubbleChatManager.removeAll(player.getUniqueId());
			}

			
			Location location = player.getLocation().add(0, 0.3, 0);
			
			ArmorStand stand = event.getPlayer().getWorld().spawn(location, ArmorStand.class);
			
			stand.setVisible(false);           
			stand.setGravity(false);            
			stand.setCustomName("§l " + message + " §l");
			stand.setCustomNameVisible(true);
			stand.setArms(true);                
			stand.setBasePlate(false);

			
			bubbleChatManager.add(player.getUniqueId(), stand);
			
			Bukkit.getScheduler().runTaskTimer(plugin, () -> {

			    if (stand.isDead() || !player.isOnline()) {
			        return;
			    }
			    
			    Location currentLocation = player.getLocation().add(0, 0.3, 0);

			    stand.teleport(currentLocation);

			}, 0L, 1L);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				stand.remove();
				bubbleChatManager.remove(player.getUniqueId(), stand);
			}, 60L);
		});
		
	}
}
