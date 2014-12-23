/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;


class ModulePermissions
{
}
/*
class ModulePermissions extends PlayerPurgerModule
{
	public ModulePermissions(PlayerData pData)
    {
		super(pData);
    }
	
	@Override
	protected boolean purge()
	{
		// Delete from the database.
		// Don't use PEX API; It doesn't delete everything
		// So we have to delete it like below
		
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deletePermissionsFromPEX");
			ps.setString(1, playername);
			ps.executeUpdate();
			
			if(pData.uuid != null)
			{
				ps.setString(1, pData.uuid.toString());
				ps.executeUpdate();
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete permissions from PEX");
		}
		
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deleteEntityFromPEX");
			ps.setString(1, playername);
			ps.executeUpdate();
			
			if(pData.uuid != null)
			{
				ps.setString(1, pData.uuid.toString());
				ps.executeUpdate();
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete entity from PEX");
		}
		
		try
		{
			PreparedStatement ps = Purger.purgerSQL.getPreparedStatement("deleteInheritanceFromPEX");
			ps.setString(1, playername);
			ps.executeUpdate();
			
			if(pData.uuid != null)
			{
				ps.setString(1, pData.uuid.toString());
				ps.executeUpdate();
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
			return !log("- Cannot delete inheritance from PEX");
		}
		
		return true;
	}
	
	@Override
	protected boolean backup()
	{
		String[] groups = Plugin.permission.getPlayerGroups((String)null, offPlayer);
		//String[] groups = Plugin.permission.getPlayerGroups((World)null, player);
		return backup.text("groups", playername, CUtils.joinArgs(groups));
	}
}
*/