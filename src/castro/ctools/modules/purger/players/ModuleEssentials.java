/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

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
		return backup.file(playerConfig, playername);
	}
}
