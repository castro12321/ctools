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

package castro.ctools.modules.purger;

import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import castro.ctools.Plugin;


public class ModuleEconomy extends Module
{
	boolean purge (String player)
	{
		
		EconomyResponse response = Plugin.economy.deleteBank(player);
		if(response.type == ResponseType.SUCCESS)
			return true;
		Plugin.get().log("Error: Cannot delete economy account. " + response.errorMessage);
		return false;
	}
	
	
	boolean backup(String player)
	{
		EconomyResponse response = Plugin.economy.bankBalance(player);
		if(response.type == ResponseType.SUCCESS)
			return backupText("money", player, response.balance + " " + response.amount);
		Plugin.get().log("Error: Cannot backup economy account. " + response.errorMessage);
		return false;
	}
}
