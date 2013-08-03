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
		return plugin.broadcast("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + sender.getName() + " wyczyscil czat");
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
