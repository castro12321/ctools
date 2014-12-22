/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;

import java.io.File;

import castro.ctools.modules.stats.DataSearch;
import castro.ctools.modules.stats.PlayerData;


class ModuleDat extends PlayerPurgerModule
{
	private final File datFile;
	
	public ModuleDat(PlayerData pData)
    {
		super(pData);
		
		File datFiles = DataSearch.getDatFilesDir();
		datFile = new File(datFiles, pData.uuid+".dat");
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
			return backup.file(datFile, playername);
		log("- Skipping! No .dat found in " + datFile.getName());
		return true;
		
	}
}
