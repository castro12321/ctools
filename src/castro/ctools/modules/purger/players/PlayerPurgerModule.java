package castro.ctools.modules.purger.players;

import org.bukkit.OfflinePlayer;

import castro.base.plugin.CPlugin;
import castro.ctools.Plugin;
import castro.ctools.modules.purger.Backup;
import castro.ctools.modules.purger.Purger;
import castro.ctools.modules.stats.PlayerData;

public abstract class PlayerPurgerModule
{
	protected final PlayerData pData;
	protected final String playername;
	protected final OfflinePlayer offPlayer;
	protected final Backup backup;
	
	public PlayerPurgerModule(PlayerData pData)
	{
		this.pData = pData;
		this.playername = pData.playername;
		this.offPlayer = CPlugin.getOfflinePlayer(playername);
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
	
	protected boolean log(String msg)
	{
		return Plugin.get().log("    " + msg);
	}
}
