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


public class BubbleChatListener implements Listener  {
	private final Plugin plugin;
	
	private BubbleChatManager bubbleChatManager;;
	
	BubbleChatListener(Plugin plugin, BubbleChatManager bubbleChatManager) {
		this.plugin = plugin;
		this.bubbleChatManager = bubbleChatManager;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		
		if(!bubbleChatManager.isEnabled()) return;
		
		String message = event.getMessage();
		

		Bukkit.getScheduler().runTask(plugin, () -> {
			Player player = event.getPlayer();
			
			List<BubbleChat> bubbleChats = bubbleChatManager.getBubbles(player.getUniqueId());
			
			if(bubbleChats != null) {
//			    System.out.println("Removing existing armor stands");
				if(bubbleChats.size() > 2) {
					System.out.println("More than 3 chat");
				}
			    bubbleChatManager.pushUpOldMessage(player.getUniqueId());
			}

			
			Location location = player.getLocation().add(0, 2, 0);
			
			TextDisplay text = event.getPlayer().getWorld().spawn(location, TextDisplay.class);


			text.setBillboard(Billboard.CENTER);
			
			Transformation textTransformation = new Transformation(
			        new Vector3f(0, 0, 0),
			        new AxisAngle4f(0, 0, 0, 1),
			        new Vector3f(1.5f, 1.5f, 1),
			        new AxisAngle4f(0, 0, 0, 1)
			    );
			
			text.setTransformation(textTransformation);
			
//			System.out.println(textTransformation.getScale());
			text.setText(ChatColor.GREEN +  message);

			BubbleChat bubbleChat = new BubbleChat(text, 2.0);

			
			bubbleChatManager.add(player.getUniqueId(), bubbleChat);
			
			Bukkit.getScheduler().runTaskTimer(plugin, () -> {

			    if (text.isDead() || !player.isOnline()) {
			        return;
			    }
			    
			    bubbleChat.tick();
			    
			    Location currentLocation = player.getLocation().add(0, bubbleChat.getYAxisOffset(), 0);

			    text.setInterpolationDelay(3);
			    text.teleport(currentLocation);

			}, 0L, 0L);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				text.remove();
				bubbleChatManager.remove(player.getUniqueId(), bubbleChat);
			}, 60L);
		});
		
	}
}
