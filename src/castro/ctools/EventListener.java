/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package castro.ctools;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.server.v1_7_R1.WorldServer;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import castro.ctools.modules.Bank;
import castro.ctools.modules.stats.PlayerData;
import castro.ctools.modules.stats.Stats;

public class EventListener implements Listener
{
	private Plugin plugin = Plugin.get();
	
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		plugin.reloadWELimit(player);
		Bank.get().checkPlayerBankAccount(player);
	}
	
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Action action = event.getAction();
		if(action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			Block clicked = event.getClickedBlock();
			if(clicked.getType().equals(Material.REDSTONE_LAMP_OFF))
			{
				Player player = event.getPlayer();
				if(Plugin.worldguard.canBuild(player, clicked))
					staticSet(clicked, Material.REDSTONE_LAMP_ON);
			}
		}
	}
	
	
	private void staticSet(Block block, Material material)
	{
		WorldServer ws = ((CraftWorld)block.getWorld()).getHandle();
		boolean old = ws.isStatic;
		ws.isStatic = true;
		block.setType(material);
		ws.isStatic = old;
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
			if(command.startsWith("/modreq ranga"))
				handleModreq(command, player, event);
	}
	
	
	// Because it is easier than configuring permissions :D
	private void blockBadCommand(String command, Player player, Cancellable event)
	{
		if(player.isOp())
			return;
		
		boolean isBad =
		       command.startsWith("/? ")
            || command.matches("/pl")
            || command.matches("/ver")
            || command.matches("/pex user .* group set .*");
		
		if(isBad)
		{
			event.setCancelled(true);
			player.sendMessage(ChatColor.DARK_RED + "Zablokowane!");
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
			
			if(pdata.modreqsCount < 2)
			{
				pdata.modreqsCount += 1;
				plugin.sendMessage(player, "You have successfully sent your " + pdata.modreqsCount + " modreq this week. "
					+ "Remember that you can send only 2 modreqs per week!");
			}
			else
			{
				float secondsLeft = pdata.modreqsReset - now;
				float daysLeft    = secondsLeft/day;
				plugin.sendMessage(player, "You have already sent 2 modreqs this week. "
					+ "Please wait " + daysLeft + " days to refresh the limit.");
				event.setCancelled(true);
			}
			
			pdata.save();
		}
	}
}