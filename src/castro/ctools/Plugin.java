/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools;

import java.io.File;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import castro.base.plugin.CPlugin;
import castro.base.plugin.CPluginSettings;
import castro.cWorlds.CPlayers;
import castro.ctools.modules.AutoRank;
import castro.ctools.modules.Bank;
import castro.ctools.modules.CModule;
import castro.ctools.modules.Contest;
import castro.ctools.modules.Lagmeter;
import castro.ctools.modules.Logger;
import castro.ctools.modules.ModBroadcast;
import castro.ctools.modules.SelectionLimiter;
import castro.ctools.modules.WorldsPreLoader;
import castro.ctools.modules.groups.ChatManager;
import castro.ctools.modules.groups.GroupManager;
import castro.ctools.modules.purger.Purger;
import castro.ctools.modules.stats.Stats;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class Plugin extends CPlugin 
{
	private static Plugin instance;
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
				
		for(Player p : Bukkit.getOnlinePlayers())
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
		
		settings.commandMgr = new CommandMgr(this);
		settings.useConfig = true;
		settings.listeners.add(new EventListener());
		
		return settings;
	}
	
	
	@Override
	protected void init()
	{
		Server server = getServer();
		PluginManager PM = server.getPluginManager();
		
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
		initModule(new GroupManager());
		initModule(new ChatManager());
		initModule(new Stats());
		initModule(new Purger());
		initModule(new AutoRank());
		initModule(new WorldsPreLoader(PM));
		initModule(new SelectionLimiter());
		
		/*
		scheduleSyncDelayedTask(new Runnable()
		{
			@Override
			public void run()
			{
				dispatchConsoleCommand("randomteleporter reload");
			}
		}, 200);
		*/
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
	
	
	public void reloadPlayer(Player player)
	{
		reloadWELimit(player);
		if(player != null)
			CPlayers.checkPlayer(player, null);
	}
	
	
	private void reloadWELimit(Player player)
	{
		if(player != null)
			dispatchCommand(player, "/limit -1");
	}
	
	
	public static Plugin get() { return instance; }
}
