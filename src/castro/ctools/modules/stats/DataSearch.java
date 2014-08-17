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

package castro.ctools.modules.stats;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import castro.cWorlds.plots.CPlot;
import castro.ctools.Plugin;


public class DataSearch
{
	private Set<String> playersFound = null;
	
	// Searches for all players stored <somewhere> in the server (even partially)
	// sometimes not being fully deleted 
	public Set<String> searchPlayers()
	{
		playersFound = new HashSet<>();
		searchDatFiles();
		searchPlotWorlds();
		searchWgConfigs();
		searchEssentialsPlayers();
		searchPexPlayers();
		searchEconomyAccounts();
		Plugin.get().log("Found " + playersFound.size() + " players");
		return playersFound;
	}
	
	private void found(String player)
	{
		Plugin.get().log("    - found " + player);
		playersFound.add(player);
	}
	
	private static File getWorldsDir()
	{
		return Bukkit.getWorldContainer();
	}
	
	
	private static File getPluginsDir()
	{
		return new File("plugins");
	}
	
	
	public static File getDatFilesDir()
	{
		String worldName = Bukkit.getWorlds().get(0).getName();
		File worldDir    = new File(getWorldsDir(), worldName);
		return new File(worldDir, "players");
	}
	
	
	private void searchDatFiles()
	{
		Plugin.get().log("- .dat search");
		File[] players = getDatFilesDir().listFiles();
		for(File player : players)
		{
			String playerName = player.getName().replace(".dat", "");
			found(playerName);
		}
		Plugin.get().log(ChatColor.GREEN + "- done");
	}
	
	private void searchPlotWorlds()
	{
		Plugin.get().log("- Plot search");
		File worlds = getWorldsDir();
		searchWorldsIn(worlds);
	}
	
	private void searchWgConfigs()
	{
		Plugin.get().log("- WG search");
		File worldguard = new File(getPluginsDir(), "WorldGuard");
		File worlds = new File(worldguard, "worlds");
		searchWorldsIn(worlds);
	}
	
	private void searchWorldsIn(File dir)
	{
		File[] worlds  = getWorldsDir().listFiles();
		for(File world : worlds)
		{
			String worldName = world.getName();
			if(worldName.startsWith("_"))
			{
				String playerName = CPlot.getPlayerName(worldName); 
				found(playerName);
			}
		}
		Plugin.get().log(ChatColor.GREEN + "- done");
	}
	
	
	private void searchEssentialsPlayers()
	{
		Plugin.get().log("- Essentials search");
		File essentials = new File(getPluginsDir(), "Essentials");
		File playersDir = new File(essentials, "userdata");
		File[] players  = playersDir.listFiles();
		for(File player : players)
		{
			String playerName = player.getName().replace(".yml", "");
			found(playerName);
		}
		Plugin.get().log(ChatColor.GREEN + "- done");
	}
	
	
	private void searchPexPlayers()
	{
		Plugin.get().log("- PEX search");
		
		Plugin.get().log("  - entities");
		try
		{
			PreparedStatement prep = Stats.sql.getPreparedStatement("pexEntities");
			ResultSet rs = prep.executeQuery();
			while(rs.next())
				found(rs.getString("name"));
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		Plugin.get().log("  - inheritance");
		try
		{
			PreparedStatement prep = Stats.sql.getPreparedStatement("pexInheritance");
			ResultSet rs = prep.executeQuery();
			while(rs.next())
				found(rs.getString("child"));
		}
		catch(SQLException e) { e.printStackTrace(); }
		
		
		Plugin.get().log(ChatColor.GREEN + "- done");
	}
	
	
	private void searchEconomyAccounts()
	{
		// TODO: search economy accounts
	}
}