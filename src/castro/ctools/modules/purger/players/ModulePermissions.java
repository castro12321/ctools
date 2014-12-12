/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules.purger.players;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import castro.base.plugin.CUtils;
import castro.ctools.Plugin;
import castro.ctools.modules.purger.Purger;


class ModulePermissions extends PlayerPurgerModule
{
	public ModulePermissions(String player)
    {
		super(player);
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
		String[] groups = Plugin.permission.getPlayerGroups((String)null, player);
		//String[] groups = Plugin.permission.getPlayerGroups((World)null, player);
		return backup.text("groups", playername, CUtils.joinArgs(groups));
	}
}
