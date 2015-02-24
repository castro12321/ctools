/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import castro.ctools.Plugin;



public class Lagmeter extends CModule implements Runnable
{
	private long last = System.currentTimeMillis();
	private int timingsResetCounter = 0; 
	
	public Lagmeter()
	{
		plugin.scheduleSyncRepeatingTask(this, 1, 1);
	}
	
	
	@Override
	public void run()
	{
		long now = System.currentTimeMillis();
		long diff = now - last;
		if(diff > 250)
		{
			plugin.log("cLagmeter encountered a lag! ticks diff: " + diff);
			Plugin.dispatchConsoleCommand("timings paste");
		}
		if(timingsResetCounter ++> 10)
		{
			Plugin.dispatchConsoleCommand("timings reset");
			timingsResetCounter = 0;
		}
		last = now;
	}
	
	
	@Override public boolean isListener()	{ return false; }
	@Override public String[] getCommands()	{ return null; }
}