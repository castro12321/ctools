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
			return log("Banks not supported. Skipping");
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
