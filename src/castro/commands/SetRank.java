/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;

import castro.ctools.PermissionsBridge;
import castro.ctools.Plugin;


public class SetRank extends CCommand
{
	String target;
	String rank;
	
	@Override
	protected boolean prepare()
	{
		if(args.length > 1)
		{
			target = args[0];
			rank   = args[1];
			
			if (!sender.hasPermission("castro.setrank." + rank))
				return !plugin.sendMessage(sender, "You don't have permission for this rank: " + rank);
		}
		return true;
	}

	@Override
	protected boolean execute()
	{
		if(rank == null)
			Plugin.dispatchCommand(sender, "pex user " + args[0] + " group list");
		else
		{
			PermissionsBridge.setGroup(target, rank);
			plugin.broadcast(target + " received a " + rank + " rank!");
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
	public String[] neededPermissions()
	{
		return permissions("ctools.setrank", "aliquam.mod");
	}
}
