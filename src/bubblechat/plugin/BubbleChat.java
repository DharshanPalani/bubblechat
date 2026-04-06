package bubblechat.plugin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Display.Billboard;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;


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
			
			List<TextDisplay> texts = bubbleChatManager.getBubbles(player.getUniqueId());
			
			if(texts != null) {
			    System.out.println("Removing existing armor stands");
			    bubbleChatManager.removeAll(player.getUniqueId());
			}

			
			Location location = player.getLocation().add(0, 2, 0);
			
			TextDisplay text = event.getPlayer().getWorld().spawn(location, TextDisplay.class);
			
//			text.setVisible(false);           
//			text.setGravity(false);            
//			text.setCustomName(ChatColor.RED + message);
//			text.setCustomNameVisible(true);
//			text.setArms(true);                
//			text.setBasePlate(false);

			text.setBillboard(Billboard.CENTER);
			text.setTransformation(new Transformation(
			        new Vector3f(0, 0, 0),
			        new AxisAngle4f(0, 0, 0, 1),
			        new Vector3f(1.5f, 1.5f, 1),
			        new AxisAngle4f(0, 0, 0, 1)
			    ));
			text.setText(ChatColor.GREEN +  message);
			
			
			
			bubbleChatManager.add(player.getUniqueId(), text);
			
			Bukkit.getScheduler().runTaskTimer(plugin, () -> {

			    if (text.isDead() || !player.isOnline()) {
			        return;
			    }
			    
			    Location currentLocation = player.getLocation().add(0, 2, 0);

			    text.setInterpolationDelay(3);
			    text.teleport(currentLocation);

			}, 0L, 0L);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				text.remove();
				bubbleChatManager.remove(player.getUniqueId(), text);
			}, 60L);
		});
		
	}
}
