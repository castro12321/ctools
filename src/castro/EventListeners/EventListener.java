package castro.EventListeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import castro.ctools.Plugin;


public class EventListener implements Listener 
{
	private Plugin plugin = Plugin.get();
	
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{		
		Player player = event.getPlayer();
		String command = event.getMessage().toLowerCase();
		
		if(!event.isCancelled())
			blockBadCommand(command, player, event);
		if(!event.isCancelled())
			handleModreq(command, player, event);
	}
	
	
	@EventHandler
	public void onPlayerChatEvent(AsyncPlayerChatEvent event)
	{
		Player player = event.getPlayer();
		
		if (event.getMessage().startsWith("@"))
		{
			event.setCancelled(true);
			String msg = event.getMessage();
			msg = msg.substring(1, msg.length()); // Cut '@' at beggining
			
			plugin.modBroadcast(player, msg);
		}
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		
		plugin.reloadWELimit(player);
		plugin.checkPlayerBankAccount(player);
		
		event.setJoinMessage(ChatColor.GREEN + "+ " + ChatColor.WHITE + event.getPlayer().getName());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		event.setQuitMessage(ChatColor.RED + "- " + ChatColor.WHITE + event.getPlayer().getName());
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKickEvent(PlayerKickEvent event)
	{
		event.setLeaveMessage(ChatColor.RED + "- " + ChatColor.WHITE + event.getPlayer().getName());
	}
	
	
	@EventHandler
	public void onFall(EntityChangeBlockEvent event)
	{
		if (event.getEntityType() == EntityType.FALLING_BLOCK)
			event.setCancelled(true);
	}
	
	
	private void blockBadCommand(String command, Player player, Cancellable event) // Because it is easier than configuring permissions :D
	{
		boolean isBad = command.equals("/pl")
				|| command.startsWith("/pl ")
				|| command.startsWith("/plugins")
				|| command.startsWith("/? ")
				|| command.matches("/pex user .* group set .*");
		
		if(isBad && !player.isOp())
		{
			event.setCancelled(true);
			player.sendMessage(ChatColor.DARK_RED + "Zablokowane!");
		}
	}
	
	
	private void handleModreq(String command, Player player, Cancellable event)
	{
		if(command.startsWith("/modreq familiar"))
			if(plugin.SQL.modreqPending(player))
				plugin.sendMessage(player, "Obecnie jeden z twoich modreqow oczekuje na ocene. Poczekaj, az jakis moderator go obejrzy.");
			else if(plugin.SQL.sendFamiliarRequest(player) == false)
			{				
				plugin.sendMessage(player, "Wykorzystales juz swoj limit! Mozesz uzyc komendy &a/modreq familiar &ftylko 3 razy miesiecznie. Poczekaj, az Ci sie odnowi limit. Proszenie o range na chacie bedzie karane!");
				event.setCancelled(true);
			}
	}
}