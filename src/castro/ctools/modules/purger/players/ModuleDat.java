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

import castro.ctools.modules.stats.DataSearch;


class ModuleDat extends PlayerPurgerModule
{
	public ModuleDat(String player)
    {
		super(player);
    }
	
	private File getDatFile(String player)
	{
		File datFiles = DataSearch.getDatFilesDir();
		return new File(datFiles, player+".dat");
	}
	
	@Override
	protected boolean purge()
	{
		log("- Looking for .dat: " + getDatFile(player).getAbsolutePath());
		if(!getDatFile(player).exists())
			return true;
		log("- .dat exists! Deleting...");
		return getDatFile(player).delete();
	}
	
	@Override
	protected boolean backup()
	{
		log("- Looking for .dat: " + getDatFile(player).getAbsolutePath());
		if(!getDatFile(player).exists())
			return true;
		log("- Exists! Backing up...");
		return backup.file(getDatFile(player), player);
	}
}
