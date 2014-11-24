/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;



public class ClearScreen extends BaseCommand
{
	@Override
	protected boolean prep()
	{
		return true;
	}

	@Override
	protected boolean exec()
	{
		for(int i = 0; i < 100; ++i)
			plugin.broadcast("\n", false);
		return plugin.broadcast(sender.getName() + " wyczyscil czat");
	}
	
	
	@Override 
	protected boolean onlyPlayer()
	{
		return false;
	}

	@Override
	protected int minArgs()
	{
		return 0;
	}
	
	
	@Override
	protected String getPermission()
	{
		return "aliquam.mod";
	}
}
