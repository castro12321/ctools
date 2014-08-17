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
	private final File datFile;
	
	public ModuleDat(String player)
    {
		super(player);
		
		File datFiles = DataSearch.getDatFilesDir();
		datFile = new File(datFiles, player+".dat");
    }
	
	@Override
	protected boolean purge()
	{
		if(!datFile.exists())
			return log("- Skipping! No .dat found in " + datFile.getName());
		if(datFile.delete())
			return true;
		return !log("- Cannot delete .dat file! " + datFile.getName());
		
	}
	
	@Override
	protected boolean backup()
	{
		if(datFile.exists())
			return backup.file(datFile, player);
		log("- Skipping! No .dat found in " + datFile.getName());
		return true;
		
	}
}
