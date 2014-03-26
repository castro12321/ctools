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

package castro.EventListeners;

import java.util.LinkedList;
import java.util.Queue;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
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

import castro.ctools.Plugin;
import castro.ctools.modules.Bank;

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
	public void onFireChargePlace(PlayerInteractEvent event)
	{
		if((event.getAction() == Action.RIGHT_CLICK_BLOCK)
		&& (event.getMaterial() == Material.FIREBALL))
			event.setCancelled(true);
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClick(PlayerInteractEntityEvent event)
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
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
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
	public void onSignChange(SignChangeEvent event)
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
		if(command.startsWith("/modreq ranga"))
			if(plugin.SQL.modreqPending(player))
				plugin.sendMessage(player, "Obecnie jeden z twoich modreqow oczekuje na ocene. Poczekaj, az jakis moderator go obejrzy.");
			else if(!plugin.SQL.sendRankRequest(player))
					event.setCancelled(true);
	}
}