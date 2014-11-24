/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.stats;

import java.sql.Timestamp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class PlayerData
{
	public final String playername;
	public String    lastWorld;
	public Timestamp seen;
	public int       playtime;
	public long      modreqsReset;
	public int       modreqsCount;
	
	// Not tracked in database
	public String currentResourcepack = null;
	
	PlayerData(String playername, String lastWorld, Timestamp seen, int playtime, long modreqsReset, int modreqsCount)
	{
		this.playername   = playername;
		this.lastWorld    = lastWorld;
		this.seen         = seen;
		this.playtime     = playtime;
		this.modreqsReset = modreqsReset;
		this.modreqsCount = modreqsCount;
	}
	
	
	public void save()
	{
		Stats.sql.updatePlayer(this);
		seen.setTime(System.currentTimeMillis());
	}
	
	
	public boolean setResourcePack(String resourcepack)
	{
		if(resourcepack.equals(currentResourcepack))
			return false;
		getPlayer().setResourcePack(resourcepack);
		currentResourcepack = resourcepack;
		return true;
	}
	
	
	public Player getPlayer()
	{
		return Bukkit.getPlayerExact(playername);
	}
}