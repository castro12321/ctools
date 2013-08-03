package castro.EventListeners;

import java.io.FileOutputStream;
import java.util.Calendar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.Plugin;

public class Logger implements Listener
{
	private Plugin plugin = Plugin.get();
	
	String path;
	
	
	public Logger(String path)
	{
		this.path = path;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		checkLog(event.getPlayer(), "command: " + event.getMessage());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		checkLog(event.getPlayer(), "chat: " + event.getMessage());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		checkLog(event.getPlayer(), "joined");
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		checkLog(event.getPlayer(), "left");
	}
	
	
	private void checkLog(Player player, String text)
	{
		if (player.hasPermission("castro.log"))
			save(player.getName(), text);
	}
	
	
	private void save(String player, String msg)
	{
		try {
			FileOutputStream fos = new FileOutputStream(path + player, true);
			fos.write((getDate() + " " + msg + "\n").getBytes());
			fos.close();
		} catch (Exception e) { plugin.log("ERROR " + e.getMessage()); }
	}
	
	
	private static String getDate()
	{
		Calendar cal = Calendar.getInstance();
		int tmp; String date;
		tmp = cal.get(Calendar.DAY_OF_MONTH);	date  = ((tmp < 10) ? ("0" + tmp) : (tmp)) + "-";
		tmp = cal.get(Calendar.MONTH) + 1;		date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + "-";
		tmp = cal.get(Calendar.YEAR); 			date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + " ";
		tmp = cal.get(Calendar.HOUR_OF_DAY);	date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + ":";
		tmp = cal.get(Calendar.MINUTE); 		date += ((tmp < 10) ? ("0" + tmp) : (tmp)) + ":";
		tmp = cal.get(Calendar.SECOND); 		date += ( tmp < 10) ? ("0" + tmp) : (tmp);
		return date;
	}
}