package castro.ctools.modules.purger.players;

import castro.ctools.Plugin;
import castro.ctools.modules.purger.Backup;
import castro.ctools.modules.purger.Purger;

public abstract class PlayerPurgerModule
{
	protected final String player;
	protected final Backup backup;
	
	public PlayerPurgerModule(String player)
	{
		this.player = player;
		this.backup = Purger.instance.backup;
    }
	
	protected abstract boolean backup();
	protected abstract boolean purge();
	
	@Override
	public String toString()
	{
		String modulename = super.toString().replace("castro.ctools.modules.purger.players.Module", "");
		return modulename.substring(0, modulename.indexOf("@"));
	}
	
	protected void log(String msg)
	{
		Plugin.get().log("    " + msg);
	}
}
