package castro.EventListeners;

import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import castro.ctools.Plugin;


public class GameModeListener implements Listener 
{
	private Plugin plugin = Plugin.get();
	
	//private boolean creative(Location loc)	{ return creative(loc.getWorld()); }
	//private boolean survival(Location loc)	{ return survival(loc.getWorld()); }
	//private void cancelIfSurvival(Cancellable event, World world) { if(survival(world)) event.setCancelled(true); }
	private boolean creative(World world)	{ return !survival(world); }
	private boolean survival(World world)	{ return world.getName().startsWith("s"); }
	private void cancelIfCreative(Cancellable event, World world) { if(creative(world)) event.setCancelled(true); }
	
	
	@EventHandler public void onDrop(ItemSpawnEvent event)						{ cancelIfCreative(event, event.getLocation().getWorld()); }
	@EventHandler public void onProjectileLaunch(ProjectileLaunchEvent event)	{ cancelIfCreative(event, event.getEntity().getWorld()); }
	@EventHandler public void onCreatureSpawn(CreatureSpawnEvent event)			{ cancelIfCreative(event, event.getLocation().getWorld()); }
	@EventHandler public void onPlayerItemConsume(PlayerItemConsumeEvent event)	{ cancelIfCreative(event, event.getPlayer().getWorld()); }
	
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event)
	{
		InventoryType invType = event.getInventory().getType();
		
		if(invType.equals(InventoryType.ENDER_CHEST))
			cancelIfCreative(event, event.getPlayer().getWorld());
	}
	
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if(creative(event.getPlayer().getWorld()))
			return;
		if(event.getPlayer().hasPermission("aliquam.mod"))
			return;
		
		String command = event.getMessage().split(" ")[0]; // Get first word
		command = command.substring(1);
		
		switch(command)
		{
		default:
			event.setCancelled(true);
			plugin.sendMessage(event.getPlayer(), "&cTa komenda jest zablokowana na mapie survival");
		
		case "kingdom":
		case "kingdoms":
		case "mail":
		case "m":
		case "msg":
		case "tell":
		case "votekick":
		case "voteban":
		case "modreq":
		case "afk":
		case "who":
		case "list":
		}
	}
}