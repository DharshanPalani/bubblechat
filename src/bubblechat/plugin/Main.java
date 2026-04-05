package bubblechat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import bubblechat.updater.Updater;

public class Main extends JavaPlugin {
    
    private BubbleChatManager bubbleChatManager;
    private BubbleChat bubbleChatListener;
    
    public boolean updating = false;
    
    @Override
    public void onEnable() {
    	bubbleChatManager = new BubbleChatManager();
        bubbleChatListener = new BubbleChat(this, this.bubbleChatManager);
        
        getServer().getPluginManager().registerEvents(bubbleChatListener, this);
        getLogger().info("BubbleChat enabled!");
        

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            boolean updateAvailable = Updater.checkForUpdate();

            if (updateAvailable) {
                Bukkit.getScheduler().runTask(this, () -> {
                	getServer().broadcastMessage(
                		    ChatColor.DARK_GRAY + "[" +
                		    ChatColor.AQUA + "BubbleChat" +
                		    ChatColor.DARK_GRAY + "] " +
                		    ChatColor.GREEN + "A new update is available! " +
                		    ChatColor.YELLOW + "Run /bubblechat update to install."
                		);
                });
            }
        }, 0L, 12000L);
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

            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "This command cannot be run by a player!");
                return true;
            }

            if (args.length > 0) {

                if (args[0].equalsIgnoreCase("update")) {

                	if(updating) {
                		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "BubbleChat" + ChatColor.GRAY + "] " + ChatColor.RED + "Update is already in process!");
                		return true;
                	}
                    sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "BubbleChat" + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Checking for updates...");

                    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                        boolean updateAvailable = Updater.checkForUpdate();

                        Bukkit.getScheduler().runTask(this, () -> {
                            if (updateAvailable) {
                            	updating = true;
                                sender.sendMessage(ChatColor.GREEN + "Update found!");
                                Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                                	Updater.downloadRunAndShutdown(this);
                                });
                            } else {
                                sender.sendMessage(ChatColor.GREEN + "You're running the latest version!");
                            }
                        });
                    });

                    return true;
                }
            }

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