/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;


class ModuleEssentials
{
}
/*
class ModuleEssentials extends PlayerPurgerModule
{
	private final File playerConfig, playerConfig2;
	
	public ModuleEssentials(PlayerData pData)
    {
		super(pData);
		
		File players  = new File(Backup.pluginConfig("Essentials"), "userdata");
		playerConfig  = new File(players, playername+".yml");
		if(pData.uuid == null)
			playerConfig2 = new File(players, "surelydoesntexist.ymldoesntexist");
		else
			playerConfig2 = new File(players, pData.uuid.toString()+".yml");
    }
	
	@Override
	protected boolean purge()
	{
		if(playerConfig.exists())
			if(!playerConfig.delete())
				return !log("- Cannot delete player (name) config " + playerConfig.getName());
		if(playerConfig2.exists())
			if(!playerConfig2.delete())
				return !log("- Cannot delete player (uuid) config " + playerConfig.getName());
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		if(playerConfig.exists())
			if(!backup.file(playerConfig, playername))
				return false;
		else
			log("- Skipping! No Essentials (name) config found " + playerConfig.getName());
		if(playerConfig2.exists())
			if(!backup.file(playerConfig2, playername))
				return false;
		else
			log("- Skipping! No Essentials (uuid) config found " + playerConfig.getName());
		return true;
	}
}
*/