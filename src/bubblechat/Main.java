package bubblechat;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    
    private BubbleChatManager bubbleChatManager;
    private BubbleChat bubbleChatListener;
    
    @Override
    public void onEnable() {
    	bubbleChatManager = new BubbleChatManager();
        bubbleChatListener = new BubbleChat(this, this.bubbleChatManager);
        
        getServer().getPluginManager().registerEvents(bubbleChatListener, this);
        getLogger().info("BubbleChat enabled!");
    }
    
    @Override
    public void onDisable() {
    	
    	if(bubbleChatManager != null) {
    		bubbleChatManager.reloadCleanUp();
    	}
        getLogger().info("BubbleChat disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        if (command.getName().equalsIgnoreCase("bubblechat")) {
            
            if ((sender instanceof Player)) {
                sender.sendMessage("This command cannot be ran by a player!");
                return true;
            }
            
//            Player player = (Player) sender;
//            
//            if (!player.isOp() && !player.hasPermission("bubblechat.admin")) {
//                player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
//                return true;
//            }
            
            boolean currentState = bubbleChatManager.isEnabled();
            bubbleChatManager.setEnabled(!currentState);
            
            if (bubbleChatManager.isEnabled()) {
                getLogger().info("BubbleChat has been ENABLED!");
            } else {
                getLogger().info("BubbleChat has been DISABLED!");
            }
            
            return true;
        }
        
        return false;
    }
}