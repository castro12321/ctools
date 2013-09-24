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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import castro.EventListeners.EventListener;
import castro.EventListeners.GameModeListener;
import castro.base.plugin.CPlugin;
import castro.base.plugin.CPluginSettings;
import castro.ctools.modules.CModule;
import castro.ctools.modules.CreatureLimiter;
import castro.ctools.modules.Lagmeter;
import castro.ctools.modules.Logger;
import castro.ctools.modules.ModBroadcast;

import com.onarandombox.MultiverseCore.api.MultiversePlugin;


public class Plugin extends CPlugin 
{
	private static Plugin instance;
	public SQL SQL;
	
	// Interfaces
	public MultiversePlugin multiverse;
	public Economy economy;
	public Permission permission;
	
	
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
		settings.listeners.add(new GameModeListener());
		settings.commandMgr = new CommandMgr();
		
		return settings;
	}
	
	
	@Override
	protected void init()
	{		
		SQL = new SQL();
		
		
		
		initEconomy();
		initPermissions();
		multiverse = (MultiversePlugin)getServer().getPluginManager().getPlugin("Multiverse-Core");
		
		
		// Init modules
		initModule(new Logger(getDataFolder().getPath() + File.separator));
		initModule(new CreatureLimiter());
		initModule(new ModBroadcast());
		initModule(new Lagmeter());
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
	
	
	private void initEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
	}
	
	private void initPermissions()
	{
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
			permission = permissionProvider.getProvider();
	}
	
	
	public void reloadWELimit(String playername)
	{ reloadWELimit(getServer().getPlayerExact(playername)); }
	public void reloadWELimit(Player player)
	{
		if(player != null)
			dispatchCommand(player, "/limit -1");
	}
	
	
	private static Set<String> familiars	= new HashSet<>(); // Familiars whose got money
	private static Set<String> builders		= new HashSet<>(); // And so on...
	private static Set<String> advBuilders	= new HashSet<>();
	private static Set<String> designers	= new HashSet<>();
	private static Set<String> architects	= new HashSet<>();
	private static final int k = 1000;
	public void checkPlayerBankAccount(Player player)
	{
		if(player == null)
			return;
		
		String group = permission.getPrimaryGroup(player);
		switch(group)
		{
		case "architect":	checkPlayerBankAccount(player, designers,	"architects",	30*k);
		case "designer":	checkPlayerBankAccount(player, architects,	"designers",	15*k);
		case "advbuilder":	checkPlayerBankAccount(player, advBuilders,	"advbuilders",	4000);
		case "builder":		checkPlayerBankAccount(player, builders,	"builders",		1500);
		case "familiar":	checkPlayerBankAccount(player, familiars,	"familiars",	500);
		}
	}
	
	
	private void checkPlayerBankAccount(Player player, Set<String> set, String filename, int money)
	{		
		if(set.isEmpty())
			load(set, filename);
		
		String playername = player.getName();
		if(set.contains(playername))
			return;
		
		economy.depositPlayer(playername, money);
		set.add(playername);
		save(filename, playername);
	}
	
	
	private void load(Set<String> set, String filename)
	{
		set.add("!empty");
		
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(getFile(filename)));
			
			String line = reader.readLine();
			while(line != null)
			{
				set.add(line);
				line = reader.readLine();
			}
			
			reader.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	
	private void save(String filename, String playername)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(getFile(filename), true)); // true = append
			writer.append(playername);
			writer.append("\n");
			writer.close();
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	
	
	private File getFile(String filename)
	{
		File file = new File(getDataFolder(), filename);
		if(!file.exists())
			try { file.createNewFile(); } catch (IOException e) {}
		return file;
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
