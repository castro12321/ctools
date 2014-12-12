/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.commands;

public class ClearScreen extends CCommand
{
	@Override
	protected boolean prepare()
	{
		return true;
	}

	@Override
	protected boolean execute()
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
	public String[] neededPermissions()
	{
		return permissions("ctools.clearscreen", "aliquam.mod");
	}
}
