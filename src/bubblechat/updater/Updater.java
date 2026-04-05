package bubblechat.updater;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Updater {
	
	private static final String VERSION_URL = "https://raw.githubusercontent.com/DharshanPalani/bubblechat/main/version.txt";
	
	private static final String UPDATER_DOWNLOAD_URL = "https://github.com/DharshanPalani/bubblechat/releases/latest/download/Updater.jar";


	public static String fetchVersion() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(VERSION_URL)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
	
	public static void downloadRunAndShutdown(Plugin plugin) {
	    try {
	        Path updaterPath = Path.of("Updater.jar");

	        HttpClient client = HttpClient.newHttpClient();

	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(UPDATER_DOWNLOAD_URL))
	                .GET()
	                .build();

	        HttpResponse<InputStream> response =
	                client.send(request, HttpResponse.BodyHandlers.ofInputStream());
	        

	        try (InputStream in = response.body()) {
	            Files.copy(in, updaterPath, StandardCopyOption.REPLACE_EXISTING);
	        }

	        if (!Files.exists(updaterPath)) {
	        	plugin.getLogger().severe("Updater download failed!");
	        	
	        	plugin.getLogger().severe("Try Downloading Manually with the following link");
	        	
	        	plugin.getLogger().severe("https://github.com/DharshanPalani/bubblechat/releases/latest/download/BubbleChat.jar" );
	        	
	        	plugin.getLogger().severe("Shutdown aborted.");
	        	
	        	return;
	        }

	        plugin.getLogger().info("Updater downloaded! Downloading and restarting...");

	        Process process = new ProcessBuilder(
	                "java",
	                "-jar",
	                updaterPath.toString()
	        ).start();

	        plugin.getLogger().info("Updater started (PID: " + process.pid() + ")");

	        Bukkit.getScheduler().runTaskLater(plugin, () -> {
	            plugin.getLogger().info("Shutting down for update...");
	            Bukkit.shutdown();
	        }, 40L);

	    } catch (Exception e) {
	        plugin.getLogger().severe("Failed to run updater!");
	        e.printStackTrace();
	    }
	}
	
	public static boolean checkForUpdate() {
		String latestVersion = fetchVersion();
		
		if("0.3".equals(latestVersion)) {
			return true;
		}
		
		return false;
	}
}
