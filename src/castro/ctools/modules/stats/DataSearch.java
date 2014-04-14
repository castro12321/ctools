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
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import castro.cWorlds.plots.CPlot;


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
		searchEssentialsPlayers();
		searchPexPlayers();
		searchEconomyAccounts();
		return playersFound;
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
		File[] players = getDatFilesDir().listFiles();
		for(File player : players)
		{
			String playerName = player.getName().replace(".dat", "");
			playersFound.add(playerName);
		}
	}
	
	
	private void searchPlotWorlds()
	{
		File[] worlds  = getWorldsDir().listFiles();
		for(File world : worlds)
		{
			String worldName = world.getName();
			if(worldName.startsWith("_"))
				playersFound.add(CPlot.getPlayerName(worldName));
		}
	}
	
	
	private void searchEssentialsPlayers()
	{
		File essentials = new File(getPluginsDir(), "Essenials");
		File playersDir = new File(essentials, "players");
		File[] players  = playersDir.listFiles();
		for(File player : players)
			playersFound.add(player.getName());
	}
	
	
	private void searchPexPlayers()
	{
		PermissionManager pex     = PermissionsEx.getPermissionManager();
		//pex.getUserIdentifiers();
		Set<PermissionUser> users = pex.getUsers();
		for(PermissionUser user : users)
			playersFound.add(user.getName());
	}
	
	
	private void searchEconomyAccounts()
	{
		// TODO: search economy accounts
	}
}