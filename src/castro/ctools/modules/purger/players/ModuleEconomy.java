/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;

import org.bukkit.OfflinePlayer;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import castro.ctools.Plugin;
import castro.ctools.modules.stats.PlayerData;


class ModuleEconomy extends PlayerPurgerModule
{
	public ModuleEconomy(PlayerData pData)
    {
		super(pData);
    }
	
	@Override
	protected boolean purge()
	{
		if(!hasBankSupport())
			return true; // Banks not supported. Skipping;
		if(!hasAccount(offPlayer))
			return log("No bank account. Skipping"); 
		
		EconomyResponse response = Plugin.economy.deleteBank(playername);
		if(response.type == ResponseType.SUCCESS)
			return true;
		
		return !log("Error: Cannot delete economy account. " + response.errorMessage);
	}
	
	@Override
	protected boolean backup()
	{
		if(!hasAccount(offPlayer))
			return log("No bank account. Skipping");
		
		double balance = Plugin.economy.getBalance(offPlayer);
		return backup.text("money", playername, balance+"");
	}
	
	private boolean hasBankSupport()
	{
		return Plugin.economy.hasBankSupport();
	}
	
	private boolean hasAccount(OfflinePlayer player)
	{
		return Plugin.economy.hasAccount(player);
	}
}
