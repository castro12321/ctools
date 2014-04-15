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

import java.io.File;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import castro.base.plugin.CPlugin;
import castro.base.plugin.CPluginSettings;
import castro.ctools.modules.Bank;
import castro.ctools.modules.CModule;
import castro.ctools.modules.Contest;
import castro.ctools.modules.Lagmeter;
import castro.ctools.modules.Logger;
import castro.ctools.modules.ModBroadcast;
import castro.ctools.modules.SelectionLimiter;
import castro.ctools.modules.TimeLock;
import castro.ctools.modules.groups.ChatManager;
import castro.ctools.modules.groups.GroupManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Plugin extends CPlugin 
{
	private static Plugin instance;
	private static CommandMgr commandMgr;
	public SQL SQL;
	
	public static WorldGuardPlugin worldguard;
	public static WorldEditPlugin  worldedit;
	public static Permission permission;
	public static Economy economy;
	
	
	public boolean modBroadcast(CommandSender player, String msg)
	{
		if(!player.hasPermission("aliquam.mod"))
			return sendMessage(player, "&cNie masz praw do uzycia tej komendy");
		
		msg = ChatColor.RED + "[MB] "
			+ ChatColor.GOLD + player.getName() + ": " + ChatColor.GRAY
			+ msg;
				
		Player[] players = getServer().getOnlinePlayers();
		for(Player p : players)
			if(p.hasPermission("aliquam.mod"))
				p.sendMessage(msg);
		log(msg);
		return true;
	}
	
	
	@Override
	protected CPluginSettings getSettings()
	{
		instance = this;
		
		CPluginSettings settings = new CPluginSettings();
		
		settings.useConfig = true;
		settings.listeners.add(new EventListener());
		
		return settings;
	}
	
	
	@Override
	protected void init()
	{
		commandMgr = new CommandMgr();
		PluginManager PM = Plugin.get().getServer().getPluginManager();
		worldguard = (WorldGuardPlugin)PM.getPlugin("WorldGuard");
		worldedit  = (WorldEditPlugin) PM.getPlugin("WorldEdit");
		
		ServicesManager services = getServer().getServicesManager();
		RegisteredServiceProvider<Economy> economyProvider = services.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		RegisteredServiceProvider<Permission> permissionProvider = services.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
			permission = permissionProvider.getProvider();
		
		SQL = new SQL();	
		
		// Init modules
		initModule(new Logger(getDataFolder().getPath() + File.separator));
		initModule(new ModBroadcast());
		initModule(new Lagmeter());
		initModule(new Bank());
		initModule(new Contest());
		initModule(new TimeLock());
		initModule(new GroupManager());
		initModule(new ChatManager());
		//initModule(new Stats());
		//initModule(new Purger());
		initModule(new SelectionLimiter());
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		return commandMgr.onCommand(sender, cmd, args);
	}
	
	
	private void initModule(CModule module)
	{
		if(module.isListener())
			getServer().getPluginManager().registerEvents(module, this);
		
		String[] commands = module.getCommands();
		if(commands != null)
			for(String command : module.getCommands())
				getCommand(command).setExecutor(module);
	}
	
	
	public void reloadWELimit(String playername)
	{ reloadWELimit(getServer().getPlayerExact(playername)); }
	public void reloadWELimit(Player player)
	{
		if(player != null)
			dispatchCommand(player, "/limit -1");
	}
	
	
	public static Plugin get() { return instance; }
}




/*
enum cSound
{
	EAT				(Sound.EAT),
	FIRE			(Sound.FIRE),
	HURT			(Sound.HURT),
	LAVA			(Sound.LAVA),
	DRINK			(Sound.DRINK),
	WATER			(Sound.WATER),
	ENDERMAN_TELEPORT(Sound.ENDERMAN_TELEPORT);
	// Programming is an art
    
    private Sound sound;

	private cSound(Sound s)
	{
		this.sound = s;
	}
	
	public Sound getSound()
	{
		return sound;
	}
}
*/
