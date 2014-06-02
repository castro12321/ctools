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

package castro.commands;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.entity.Player;

import castro.base.plugin.CUtils;
import castro.ctools.Plugin;

public class Pay extends BaseCommand
{
	private final Economy eco = Plugin.economy;
	String target;
	Double amount;
	
	@Override
	protected boolean prep()
	{
		target = args[0];
		amount = CUtils.convert(args[1], Double.class);
		
		if(args[1].length() > 6)
			return !plugin.sendMessage(sender, "&cYou are trying to pay too much money!");
		if(amount == null)
			return !plugin.sendMessage(sender, "&cEntered wrong money!");
		if(!eco.hasAccount(target))
			return !plugin.sendMessage(sender, "&cTarget player doesn't have bank account!");
		if(eco.getBalance(playername) < amount)
			return !plugin.sendMessage(sender, "&cYou don't have enough cash!");
		return true;
	}

	@Override
	protected boolean exec()
	{
		EconomyResponse resp;
		resp = eco.withdrawPlayer(sender.getName(), amount);
		if(!resp.transactionSuccess())
			return !plugin.sendMessage(sender, "Something wrong happened! " + resp.errorMessage);
		
		resp = eco.depositPlayer(target, amount);
		if(!resp.transactionSuccess())
		{
			resp = eco.depositPlayer(sender.getName(), amount); // Try to give money back
			return !plugin.sendMessage(sender, "Something very wrong happened! Please contact an administrator. " + resp.errorMessage);
		}
		
		plugin.sendMessage(sender, "&aYou have sent " + amount + "$ to " + target);
		return true;
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return true;
	}

	@Override
	protected int minArgs()
	{
		return 2;
	}
	
	
	@Override
	protected String getPermission()
	{
		return "aliquam.guest";
	}
}
