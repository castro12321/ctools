/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import castro.ctools.Plugin;


class ModuleEconomy extends PlayerPurgerModule
{
	public ModuleEconomy(String player)
    {
		super(player);
    }
	
	@Override
	protected boolean purge()
	{
		if(!hasBankSupport())
			return true; // Banks not supported. Skipping;
		if(!hasAccount(player))
			return log("No bank account. Skipping"); 
		
		EconomyResponse response = Plugin.economy.deleteBank(player);
		if(response.type == ResponseType.SUCCESS)
			return true;
		
		return !log("Error: Cannot delete economy account. " + response.errorMessage);
	}
	
	@Override
	protected boolean backup()
	{
		if(!hasAccount(player))
			return log("No bank account. Skipping");
		
		double balance = Plugin.economy.getBalance(player);
		return backup.text("money", player, balance+"");
	}
	
	private boolean hasBankSupport()
	{
		return Plugin.economy.hasBankSupport();
	}
	
	private boolean hasAccount(String player)
	{
		return Plugin.economy.hasAccount(player);
	}
}
