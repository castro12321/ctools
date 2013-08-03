package castro.commands;

import castro.ctools.Plugin;


public class SetRank extends BaseCommand
{
	String target;
	String rank;
	
	@Override
	protected boolean prep()
	{
		if(args.length > 1)
		{
			target = args[0];
			rank   = args[1];
			
			if (!sender.hasPermission("castro.setrank." + rank))
				return !plugin.sendMessage(sender, "Nie masz praw do nadawania rangi " + rank);
		}
		return true;
	}

	@Override
	protected boolean exec()
	{
		if(rank == null)
			Plugin.dispatchCommand(sender, "pex user " + args[0] + " group list");
		else
		{			
			Plugin.dispatchConsoleCommand("pex user " + target + " group set " + rank);
			plugin.broadcast(target + " otrzymal range " + rank);
			plugin.reloadWELimit(target);
			plugin.checkPlayerBankAccount(plugin.getServer().getPlayerExact(target));
		}
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
		return 1;
	}
	
	
	@Override
	protected String getPermission()
	{
		return "aliquam.mod";
	}
}
