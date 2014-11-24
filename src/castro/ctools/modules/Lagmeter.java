/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

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