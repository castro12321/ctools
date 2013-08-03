package castro.commands;

import castro.ctools.Plugin;


public class ModBroadcast extends BaseCommand
{
	String msg;
	
	@Override
	protected boolean prep()
	{		
		msg = Plugin.joinArgs(args);
		return true;
	}

	@Override
	protected boolean exec()
	{
		return plugin.modBroadcast(sender, msg);
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
