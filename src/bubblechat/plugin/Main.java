package bubblechat.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.plugin.java.JavaPlugin;

import bubblechat.updater.Updater;

public class Main extends JavaPlugin {
    
    private BubbleChatManager bubbleChatManager;
    private BubbleChatListener bubbleChatListener;
    
    public boolean updating = false;

    @Override
    public void onEnable() {
//    	On Enable, this creates new chatManager and chatListener
    	bubbleChatManager = new BubbleChatManager();
        bubbleChatListener = new BubbleChatListener(this, this.bubbleChatManager);

//       This just registers the bubble chat and logs it.
        getServer().getPluginManager().registerEvents(bubbleChatListener, this);
        getLogger().info("BubbleChat enabled!");
        
//		This scheduler runs for every 10 minutes checking for update, if update it will log. If not ignored.
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            boolean updateAvailable = Updater.checkForUpdate(this);

            if (updateAvailable) {
//            	This is the sexy scheduler that runs the broadcast message with peak color ever 10 mintues.
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
        
        
        Bukkit.getScheduler().runTask(this, () -> {
//        	when the plugin starts, it removes every single textDisplay which exists, since those don't have a gaurenteed way of being removed due to previous bugs.
            for (World world : Bukkit.getWorlds()) {
                for (TextDisplay display : world.getEntitiesByClass(TextDisplay.class)) {
                    display.remove();
                }
            }
        });
    }
    
    @Override
    public void onDisable() {
//    	On disable of the plugin it clears out every chat before so.
    	if(bubbleChatManager != null) {
    		bubbleChatManager.reloadCleanUp();
    	}
        getLogger().info("BubbleChat disabled!");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
//		If the command has bubble chat it goes in for further check.
        if (command.getName().equalsIgnoreCase("bubblechat")) {
//			This checks if the command ran by is player, if so then it says GET OU-
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "This command cannot be run by a player!");
                return true;
            }

            if (args.length > 0) {
//            	If the command arg has the word "update" it will check for update, if there is any then it will update, if not alerted saying it's on latest version.
                if (args[0].equalsIgnoreCase("update")) {

                	if(updating) {
                		sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "BubbleChat" + ChatColor.GRAY + "] " + ChatColor.RED + "Update is already in process!");
                		return true;
                	}
                    sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + "BubbleChat" + ChatColor.GRAY + "] " + ChatColor.YELLOW + "Checking for updates...");

                    Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                        boolean updateAvailable = Updater.checkForUpdate(this);

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

//              If the command arg has the word "toggle" it will toggle the bubble chat.
                else if(args[0].equalsIgnoreCase("toggle")) {                	
                	boolean currentState = bubbleChatManager.isEnabled();
                	bubbleChatManager.setEnabled(!currentState);
                	
                	if (bubbleChatManager.isEnabled()) {
                		getLogger().info("BubbleChat has been ENABLED!");
                	} else {
                		getLogger().info("BubbleChat has been DISABLED!");
                	}
                }
            }


            return true;
        }

        return false;
    }
}