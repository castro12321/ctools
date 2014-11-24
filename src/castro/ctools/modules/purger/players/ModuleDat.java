/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

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
