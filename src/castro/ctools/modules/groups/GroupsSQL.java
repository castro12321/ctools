/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package castro.ctools.modules.groups;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import castro.base.data.SQLBase;
import castro.ctools.Plugin;

public class GroupsSQL extends SQLBase 
{
	private final String TABLENAME = "ctools_groups";
	private final String GET_GROUPS_QUERY = "SELECT * FROM " + TABLENAME;
	
	
	public GroupsSQL(Plugin plugin)
	{
		super(plugin, true);
		
		try 
		{			
			Connection conn = getConn();
			conn.createStatement().executeUpdate(
					  "CREATE TABLE IF NOT EXISTS "+TABLENAME+"("
					+ "id    INT        NOT NULL AUTO_INCREMENT, "
					+ "type  INT        NOT NULL, "
					+ "importance INT   NOT NULL, "
					+ "name VARCHAR(24) NOT NULL, " 
					+ "displayname VARCHAR(24) NOT NULL, "
					+ "PRIMARY KEY(id), "
					+ "UNIQUE(name)"
					+ ") ENGINE=MyIsam "
					);
		}
		catch (SQLException e) 
		{
			printErrors(e);
		}
	}
	
	
	public List<Group> getAllGroups()
	{
		List<Group> groups = new ArrayList<>();
		
        try
        {
            ResultSet rs = getConn().createStatement().executeQuery(GET_GROUPS_QUERY);
            while(rs.next())
            {
            	int    type        = rs.getInt("type");
            	int    order       = rs.getInt("importance");
            	String name        = rs.getString("name");
            	String displayname = rs.getString("displayname");
            	
            	GroupType gType = GroupType.get(type);
            	groups.add(new Group(gType, order, name, displayname));
            }
            rs.close();
        }
        catch(SQLException e) { e.printStackTrace(); }
        
        return groups;
	}
}
