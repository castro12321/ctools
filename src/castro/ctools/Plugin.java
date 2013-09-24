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

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import castro.EventListeners.EventListener;
import castro.EventListeners.GameModeListener;
import castro.base.plugin.CPlugin;
import castro.base.plugin.CPluginSettings;
import castro.ctools.modules.Bank;
import castro.ctools.modules.CModule;
import castro.ctools.modules.CreatureLimiter;
import castro.ctools.modules.Lagmeter;
import castro.ctools.modules.Logger;
import castro.ctools.modules.ModBroadcast;

public class Plugin extends CPlugin 
{
	private static Plugin instance;
	public SQL SQL;
	
	
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
		
		// Init modules
		initModule(new Logger(getDataFolder().getPath() + File.separator));
		initModule(new CreatureLimiter());
		initModule(new ModBroadcast());
		initModule(new Lagmeter());
		initModule(new Bank());
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
