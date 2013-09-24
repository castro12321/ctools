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

package castro.ctools.modules;

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

import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;


public class Bank extends CModule
{
	private static Bank instance;
	public Economy economy;
	public Permission permission;
	
	
	public Bank()
	{
		instance = this;
		
		ServicesManager services = plugin.getServer().getServicesManager();
		
		RegisteredServiceProvider<Economy> economyProvider = services.getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null)
			economy = economyProvider.getProvider();
		
		RegisteredServiceProvider<Permission> permissionProvider = services.getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null)
			permission = permissionProvider.getProvider();
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
		File file = new File(plugin.getDataFolder(), filename);
		if(!file.exists())
			try { file.createNewFile(); } catch (IOException e) {}
		return file;
	}
	
	
	public static Bank get()
	{
		return instance;
	}
	
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return null; }
}