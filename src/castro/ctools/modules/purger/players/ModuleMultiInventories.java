/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;


class ModuleMultiInventories
{
}
/*
class ModuleMultiInventories extends PlayerPurgerModule
{
	public ModuleMultiInventories(PlayerData pData)
    {
		super(pData);
    }
	
	@Override
	protected boolean purge()
	{
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deletePlayerInvFromMultiInv");
			ps.setString(1, playername);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete player inventory");
		}
		
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deletePlayerInvFromMultiInv");
			ps.setString(1, playername);
			ps.executeUpdate();
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete player chests");
		}
		
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		// As we are only creative server (well, you can set survival, but that doesn't matter)
		// We don't need to backup the inventory
		return true;
	}
}
*/