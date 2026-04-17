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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
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
	public void onPlayerQuit(PlayerQuitEvent event) {
//		System.out.println(event.getPlayer().getName());
		
		bubbleChatManager.removeAll(event.getPlayer().getUniqueId());
	}
	
//	This is a event listener where it runs whenever the player types something in the chat.
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
//		Returns if the chat is disabled. (From the toggle command)
		if(!bubbleChatManager.isEnabled()) return;

		String message = event.getMessage(); // Chat message from the event shit.
		
//		This runs once in the minecraft server thread for the bubblechat logic.
		Bukkit.getScheduler().runTask(plugin, () -> {
			Player player = event.getPlayer();

//			Retrieves all the current bubblechat typed (or spawned) by the player in the world from the bubbleChatManager.
			List<BubbleChat> bubbleChats = bubbleChatManager.getBubbles(player.getUniqueId());
			
//			This logic checks if that specific player has bubblechat, if they don't it's null and ignored. If they do it's not null and it runs the statement.
			if(bubbleChats != null) {
//				This function handles sending the old message on top and the scale and axis of it.
			    bubbleChatManager.pushUpOldMessage(player.getUniqueId());
			}
			
//			Creates offset for the text so it doesn't collide with the nametag
			Vector offSet = new Vector(0, 2.8f, 0);

//			Get's the location of the player and adds the offset to it.
			Location location = player.getLocation().add(offSet);
			
			TextDisplay text = event.getPlayer().getWorld().spawn(location, TextDisplay.class);
			text.setBillboard(Billboard.CENTER);

//			Transformation object is created, which just scales the text so it can be read. And it is set in the next logic.
			Transformation textTransformation = new Transformation(
			        new Vector3f(0, 0, 0),
			        new AxisAngle4f(0, 0, 0, 1),
			        new Vector3f(1.5f, 1.5f, 1),
			        new AxisAngle4f(0, 0, 0, 1)
			    );
			text.setTransformation(textTransformation);

			text.setText(ChatColor.GREEN +  message); // Color-coded chat message is set.

//			Creates bubbleChat which holds the text and yAxis.
			BubbleChat bubbleChat = new BubbleChat(text, offSet.getY());
			
			bubbleChatManager.add(player.getUniqueId(), bubbleChat); // Adds to the Map with the player UUID
			
			Bukkit.getScheduler().runTaskTimer(plugin, () -> {

			    if (text.isDead() || !player.isOnline()) {
			        return;
			    }
			    
			    bubbleChat.tick(); //Calls the bubbleChat tick function
			    
			    Location currentLocation = player.getLocation().add(0, bubbleChat.getYAxisOffset(), 0);

			    text.setInterpolationDelay(3);
			    text.teleport(currentLocation);

			}, 1L, 1L);

			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				text.remove();
				bubbleChatManager.remove(player.getUniqueId(), bubbleChat);
			}, 100L);
		});
		
	}
}
