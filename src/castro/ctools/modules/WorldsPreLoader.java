package castro.ctools.modules;

import java.io.File;
import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;

import castro.base.plugin.CUtils;
import castro.cWorlds.CPlot;
import castro.cWorlds.PlotsMgr;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.EssentialsConf;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserData;
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
        	warpPoints = Warps.class.getDeclaredField("warpPoints");
	        userConfig = UserData.class.getDeclaredField("config");
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
		if(message.startsWith("/warp "))
			loadWarp(message);
		else if(message.startsWith("/home "))
			loadHome(event.getPlayer(), message);
		else if(message.startsWith("/tpid "))
			loadModreq(message);
	}
	
	
	private void loadWarp(String message)
	{
		String[] parts = message.split(" ");
		if(parts.length > 0 && !parts[1].matches("[0-9]+"))
		{
			String warpname = parts[1];
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
	
	
	private void loadHome(Player player, String message)
	{
		String[] parts = message.split(" ");
		String home;
		if(parts.length == 0)
			home = "0";
		else
			home = parts[1];
		
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
	        if(world == null)
	        	return;
	        CPlot plot = PlotsMgr.get(world);
	        if(plot != null)
	        	plot.load();
        }
        catch(IllegalArgumentException | IllegalAccessException e)
        {
	        e.printStackTrace();
        }
	}
	
	
	private void loadModreq(String message)
	{
		String[] parts = message.split(" ");
		if(parts.length == 0)
			return;
		Integer id = CUtils.convert(parts[1], Integer.class, null);
		if(id == null)
			return;
		String world = plugin.SQL.modreqWorld(id);
		if(world == null)
			return;
		
		CPlot plot = PlotsMgr.get(world);
        if(plot != null)
        	plot.load();
	}
	
	
	@Override public boolean isListener()	{ return true; }
	@Override public String[] getCommands()	{ return null; }
}
