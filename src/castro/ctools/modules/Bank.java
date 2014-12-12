/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import castro.ctools.Plugin;


public class Bank extends CModule implements Runnable
{
	public Bank()
	{
		final int second = 20;
		final int minute = 60*second;
		plugin.scheduleSyncRepeatingTask(this, 60*minute, 60*minute);
		//plugin.scheduleSyncRepeatingTask(this, 6*second, 6*second);
	}
	
	private int getWage(Player player)
	{
		int wage = 10;
		if     (player.hasPermission("aliquam.architect"))  wage = 50;
		else if(player.hasPermission("aliquam.advbuilder")) wage = 40;
		else if(player.hasPermission("aliquam.builder"))    wage = 30;
		else if(player.hasPermission("aliquam.familiar"))   wage = 20;
		
		if     (player.hasPermission("aliquam.admin"))      wage += 50;
		else if(player.hasPermission("aliquam.mod"))        wage += 30;
		else if(player.hasPermission("aliquam.helper"))     wage += 15;
		
		return wage;
	}
	
	public void run()
	{
		Economy eco = Plugin.economy;
		for(Player player : Bukkit.getOnlinePlayers())
		{
			double wage = getWage(player);
			EconomyResponse resp = eco.depositPlayer(player, wage);
			if(!resp.transactionSuccess())
			{	
				try
				{
					throw new Exception();
				}
				catch(Exception e)
				{
					Plugin.get().sendMessage(player, "We want to give you some money, but we can't. Please relay this message to administrator: " + resp.errorMessage);
					Plugin.get().log("BANK CANNOT PAY ERROR " + resp.errorMessage);
					e.printStackTrace();
				}
			}
		}
	}
	
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return null; }
}