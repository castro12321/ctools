/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import castro.base.plugin.CPlugin;
import castro.base.plugin.CUtils;
import castro.ctools.Plugin;


public class Pay extends CCommand
{
	private final Economy eco = Plugin.economy;
	OfflinePlayer target;
	Double amount;
	private boolean silent = false;
	private boolean console = false;
	
    @Override
	protected boolean prepare()
	{
    	console = !(sender instanceof Player);
		target = CPlugin.getOfflinePlayer(args[0]);
		amount = CUtils.convert(args[1], Double.class);
		if(args.length > 2)
			silent = args[2].equalsIgnoreCase("-silent");
		
		if(args[1].length() > 6)
			return !plugin.sendMessage(sender, "&cYou are trying to pay too much money!");
		if(amount == null)
			return !plugin.sendMessage(sender, "&cEntered wrong money!");
		if(!eco.hasAccount(target))
			return !plugin.sendMessage(sender, "&cTarget player doesn't have bank account!");
		if(!console)
		{
			if(eco.getBalance(senderPlayer) < amount)
				return !plugin.sendMessage(sender, "&cYou don't have enough cash!");
		}
		return true;
	}

	@Override
	protected boolean execute()
	{
		EconomyResponse resp;
		if(!console)
		{
			resp = eco.withdrawPlayer(senderPlayer, amount);
			if(!resp.transactionSuccess())
				return !plugin.sendMessage(sender, "Something wrong happened! " + resp.errorMessage);
		}
		
		resp = eco.depositPlayer(target, amount);
		if(!resp.transactionSuccess())
		{
			resp = eco.depositPlayer(senderPlayer, amount); // Try to give money back
			return !plugin.sendMessage(sender, "Something very wrong happened! Please contact an administrator. " + resp.errorMessage);
		}
		
		plugin.sendMessage(sender, "&aYou have sent " + amount + "$ to " + target.getName());
		if(!silent)
			plugin.sendMessage(target.getName(), "&aYou have received " + amount + "$ from " + sender.getName());
		return true;
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return false;
	}

	@Override
	protected int minArgs()
	{
		return 2;
	}
	
	
	@Override
	public String[] neededPermissions()
	{
		return permissions("ctools.pay", "aliquam.guest");
	}
}
