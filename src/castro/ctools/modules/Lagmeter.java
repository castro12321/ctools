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

package castro.ctools.modules;



public class Lagmeter extends CModule implements Runnable
{
	long last = System.currentTimeMillis();
	
	public Lagmeter()
	{
		plugin.scheduleSyncRepeatingTask(this, 1, 1);
	}
	
	
	@Override
	public void run()
	{
		long now = System.currentTimeMillis();
		if((now - last) > 100l)
			plugin.log("cLagmeter encountered a lag! ticks diff: " + (now-last));
		last = now;
	}
	
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return null; }
}