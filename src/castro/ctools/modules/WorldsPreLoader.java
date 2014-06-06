package castro.ctools.modules;

import java.io.File;
import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;

import castro.cWorlds.plots.CPlot;
import castro.cWorlds.plots.PlotsMgr;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.Warps;



public class WorldsPreLoader extends CModule
{
	private final Essentials ess;
	private Field warpPoints, userConfig;
	
	public WorldsPreLoader(PluginManager pluginMgr)
    {
		ess = (Essentials)pluginMgr.getPlugin("Essentials");
	    
        try
        {
        	warpPoints = Warps.class.getField("warpPoints");
	        userConfig = User.class.getField("config");
	        warpPoints.setAccessible(true);
	        userConfig.setAccessible(true);
        }
        catch(SecurityException | NoSuchFieldException e)
        {
	        e.printStackTrace();
        }
    }
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event)
	{
		String message = event.getMessage();
		if(message.startsWith("/warp"))
			loadWarp(message);
		if(message.startsWith("/home"))
			loadHome(event.getPlayer(), message);
	}
	
	
	private void loadWarp(String message)
	{
		String[] parts = message.split(" ");
		if(parts.length > 0 && !parts[0].matches("[0-9]+"))
		{
			String warpname = parts[0];
			
			//Map<StringIgnoreCase, EssentialsConf> warps = warpPoints.get(ess.getWarps());
			
			File warps = new File(ess.getDataFolder(), "warps");
			File warpfile = new File(warps, warpname+".yml");
			if(warpfile.exists() && warpfile.isFile())
			{
				try
				{
					EssentialsConf conf = new EssentialsConf(warpfile);
					conf.load();
					String world = conf.getString("world");
					if (world != null)
					{
						CPlot plot = PlotsMgr.get(world);
						if(plot != null)
							plot.load();
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private void loadHome(Player player, String home)
	{
		User user = ess.getUser(player);
		try
		{
			int homeNum = Integer.parseInt(home);
			home = user.getHomes().get(homeNum - 1);
		}
		catch (NumberFormatException | IndexOutOfBoundsException e)
		{
		}
		
		try
        {
	        EssentialsConf conf = (EssentialsConf)userConfig.get(user);
	        String world = conf.getString("homes."+home+".world");
	        CPlot plot = PlotsMgr.get(world);
	        if(plot != null)
	        	plot.load();
        }
        catch(IllegalArgumentException | IllegalAccessException e)
        {
	        e.printStackTrace();
        }
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}
