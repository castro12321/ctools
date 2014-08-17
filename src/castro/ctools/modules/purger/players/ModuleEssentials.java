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

package castro.ctools.modules.purger.players;

import java.io.File;


class ModuleEssentials extends PlayerPurgerModule
{
	private final File playerConfig;
	
	public ModuleEssentials(String player)
    {
		super(player);
		
		File players = new File(backup.pluginConfig("Essentials"), "userdata");
		playerConfig = new File(players, player+".yml");
    }
	
	@Override
	protected boolean purge()
	{
		if(!playerConfig.exists())
			return log("- Skipping! No Essentials config found " + playerConfig.getName());
		if(!playerConfig.delete())
			return !log("- Cannot delete player config " + playerConfig.getName());
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		if(!playerConfig.exists())
			return log("- Skipping! No Essentials config found " + playerConfig.getName());
		return backup.file(playerConfig, player);
	}
}
