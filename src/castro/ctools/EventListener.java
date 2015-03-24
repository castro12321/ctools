/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import castro.cWorlds.CPlot;
import castro.cWorlds.MemberType;
import castro.cWorlds.PlotsMgr;
import castro.ctools.modules.stats.PlayerData;
import castro.ctools.modules.stats.Stats;

public class EventListener implements Listener
{
	private Plugin plugin = Plugin.get();
	//private final File dataFolder = plugin.getDataFolder();
	//private final File domainRedirects = new File(dataFolder, "domains");
	public static String lastJoinedDomain;
	
	/*
	private void delayCommand(final Player player, final String command)
	{
		plugin.scheduleSyncDelayedTask(new Runnable()
		{
			@Override
            public void run()
            {
	            Plugin.dispatchCommand(player, command);
            }
		});
	}
	*/
	
	@EventHandler
	public void onPlayerPreLogin1(AsyncPlayerPreLoginEvent e)
	{
		String player = e.getName();
		PlayerData pData = Stats.get(player);
		
		if(pData == null) // No nickname taken. Can go
			return;
		if(pData.uuid == null) // UUID not set yet. Shit happens
			return;
		
		UUID uuid = e.getUniqueId();
		if(!pData.uuid.equals(uuid)) // Don't allow taking the names. At least for now
		{
			Plugin.dispatchConsoleCommand("mb 1: " + pData.playername + "; " + pData.uuid + " ---> " + player + "; " + uuid);
			//e.disallow(Result.KICK_OTHER, "This nick is not available. Please change your nick in order to join the server. This nick is reserved by user UUID " + pData.uuid);
		}
	}
	
	@EventHandler
	public void onPlayerPreLogin2(AsyncPlayerPreLoginEvent e)
	{
		String player = e.getName();
		UUID uuid = e.getUniqueId();
		PlayerData pData = Stats.sql.getPlayerByUUID(uuid);
		
		if(pData == null) // No uuid taken. Can go
			return;
		if(pData.playername == null || pData.uuid == null) // nick not set yet. Shit happens
			return;
		
		String playername = e.getName();
		if(!pData.playername.equalsIgnoreCase(playername)) // Don't allow taking the names. At least for now
		{
			Plugin.dispatchConsoleCommand("mb 2: " + pData.playername + "; " + pData.uuid + " ---> " + player + "; " + uuid);
			//e.disallow(Result.KICK_OTHER, "Please change your nick back to " + pData.playername + " in order to join the server");
		}
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event)
	{
		String hostname = event.getHostname();
		//Player joined = event.getPlayer();
		//plugin.log(joined.getName() + " joined using hostname: " + hostname);
		if(hostname.contains("aliquam.pl.") // Redirected from aliquam.org (see '.' at the end)
		|| hostname.contains("aliquam.org"))
		{
			lastJoinedDomain = "En";
			//delayCommand(joined, "multichat eng");
		}
		else if
		(  hostname.contains("aliquam.pl")
		|| hostname.contains("kawinski.net")
		|| hostname.contains("minecraft.pl"))
		{
			lastJoinedDomain = "Pl";
			//delayCommand(joined, "multichat pl");
		}
		else // IP address or not specified
		{
			lastJoinedDomain = "En";
			//delayCommand(joined, "multichat eng");
		}
		/*
		// Check custom domains
		if(!hostname.startsWith("aliquam.")
		&& !hostname.startsWith("he.")
		&& hostname.contains("."))
		{
			String[] parts = hostname.split(".");
			if(parts.length > 0)
			{
				String filename = parts[0];
				plugin.log("lcp3 " + filename);
				File file = new File(domainRedirects, filename);
				if(file.exists())
				{
					try
	                {
	                	List<String> commands = FileUtils.readLines(file);
	                	for(String command : commands)
	    					delayCommand(joined, command);
	                }
	                catch(IOException e)
	                {
		                e.printStackTrace();
	                }
				}
			}
		}
		*/
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		plugin.reloadPlayer(player);
		if(player.getWorld().getName().equals("spawn"))
			player.performCommand("spawn"); // lol
	}
	
	@EventHandler
	public void resetVoxelBrushOnPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		CPlot plot = PlotsMgr.get(player.getWorld());
		if(!plot.is(player, MemberType.MEMBER))
			player.performCommand("b s");
	}
	
    @EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			Player player = event.getPlayer();
			if(!player.getGameMode().equals(GameMode.CREATIVE))
				return;
			
			Block clicked = event.getClickedBlock();
			if(clicked.getType().equals(Material.REDSTONE_LAMP_OFF))
			{
				if(Plugin.worldguard.canBuild(player, clicked))
					staticSet(clicked, Material.REDSTONE_LAMP_ON);
			}
			if(clicked.getType().equals(Material.FURNACE))
			{
				if(Plugin.worldguard.canBuild(player, clicked))
					staticSet(clicked, Material.BURNING_FURNACE);
			}
		}
	}
	
	@SuppressWarnings("deprecation") // Sorry, Bukkit sucks
	private void staticSet(Block block, Material material)
	{
	    
		WorldServer ws = ((CraftWorld)block.getWorld()).getHandle();
		boolean old = ws.isStatic;
		try
		{
    		setWorldStatic(ws, true);
    		byte data = block.getData();
    		block.setType(material);
    		block.setData(data);
    		setWorldStatic(ws, old);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
	private static void setWorldStatic(WorldServer world, boolean static_boolean) throws Exception
	{
		java.lang.reflect.Field static_field = net.minecraft.server.v1_8_R1.World.class.getDeclaredField("isStatic");
		static_field.setAccessible(true);
		//static_field.set(static_field, static_field.getModifiers() & ~Modifier.FINAL);
		static_field.set(world, static_boolean);
	}
	
	@EventHandler
	public void blockSpectatorForPlayers(PlayerTeleportEvent event)
	{
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getWorld().equals(to.getWorld()))
			return; // Whatever
		
		Player player = event.getPlayer();
		if(player.getGameMode() == GameMode.SPECTATOR)
			if(!player.hasPermission("aliquam.spectator"))
				event.setCancelled(true);
	}
	
	@EventHandler
	public void fireProtection(PlayerInteractEvent event)
	{
		if((event.getAction() == Action.RIGHT_CLICK_BLOCK)
		&& (event.getMaterial() == Material.FIREBALL))
			event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void itemFrameRotatingProtection(PlayerInteractEntityEvent event)
	{
		Player player = event.getPlayer();
		Entity clicked = event.getRightClicked();
		if(clicked.getType() == EntityType.ITEM_FRAME)
		{
			Location entityLocation = clicked.getLocation();
			if(!Plugin.worldguard.canBuild(player, entityLocation))
				event.setCancelled(true);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void itemFrameDestroyProtection(EntityDamageByEntityEvent event)
	{
		Entity damaged = event.getEntity();
		if(damaged.getType() == EntityType.ITEM_FRAME)
		{
			Entity damager = event.getDamager();
			if(damager instanceof Player)
			{
				Player player = (Player) damager;
				if(Plugin.worldguard.canBuild(player, damaged.getLocation()))
					return;
			}
			event.setCancelled(true);
		}
	}
	
	
	@EventHandler
	public void colorSigns(SignChangeEvent event)
	{
		Queue<String> queue = new LinkedList<>();
		queue.poll();
		
		Player player = event.getPlayer();
		if(player.hasPermission("castro.colors"))
		{
			String[] lines = event.getLines();
			for(int i = 0; i < lines.length; i++)
				event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
		}
	}
	
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		Player player = event.getPlayer();
		String command = event.getMessage().toLowerCase();
		
		if(!event.isCancelled())
			blockBadCommand(command, player, event);
		if(!event.isCancelled())
			if(command.startsWith("/modreq ranga")
			|| command.startsWith("/modreq rank")
			|| command.startsWith("/modreq familiar")
			|| command.startsWith("/modreq builder"))
				handleModreq(command, player, event);
	}
	
	
	// Because it is easier than configuring permissions :D
	private void blockBadCommand(String command, Player player, Cancellable event)
	{
		if(player.isOp())
			return;
		if(player.getName().equals("castro12321"))
			return;
		
		boolean isBad = 
		       command.startsWith("/ver")
		    || command.startsWith("/icanhasbukkit")
			|| command.startsWith("/pex")
			|| command.startsWith("/permissions");
		
		if(isBad && command.matches("/pex user .* group list"))
			isBad = false; // Allow only those parameters
		
		if(isBad)
		{
			event.setCancelled(true);
			player.sendMessage(ChatColor.DARK_RED + "Nope");
		}
	}
	
	
	private void handleModreq(String command, Player player, Cancellable event)
	{
		if(plugin.SQL.modreqPending(player))
		{
			plugin.sendMessage(player, "One of your modreqs is awaiting for approval. Please try again later.");
			event.setCancelled(true);
		}
		else
		{
			final int  day   = 86400; // seconds in day
			final long now   = System.currentTimeMillis() / 1000l;
			PlayerData pdata = Stats.get(player);
			
			if(now > pdata.modreqsReset)
			{
				pdata.modreqsReset = now + 7*day;
				pdata.modreqsCount = 0;
			}
			
			if(pdata.modreqsCount < 3)
			{
				pdata.modreqsCount += 1;
				plugin.sendMessage(player, "You have successfully sent your " + pdata.modreqsCount + " modreq this week. "
					+ "Remember that you can send only 3 modreqs per week!");
			}
			else
			{
				float secondsLeft = pdata.modreqsReset - now;
				float daysLeft    = secondsLeft/day;
				plugin.sendMessage(player, "You have already sent 3 modreqs this week. "
					+ "Please wait " + daysLeft + " days to refresh the limit.");
				event.setCancelled(true);
			}
			
			pdata.save();
		}
	}
}