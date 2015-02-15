/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools.modules;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import castro.base.plugin.CUtils;
import castro.cWorlds.AntiCheatListener;
import castro.ctools.Plugin;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.regions.Region;


/*
 * Limits worldedit selection size to 5*<blocks limit change>
 * Limits using radiuses bigger than 250
 */
public class SelectionLimiter extends CModule
{
    public SelectionLimiter()
    {
        plugin.registerEvents(this);
    }
    
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onCommandPreProcess(PlayerCommandPreprocessEvent event)
    {
        Player player  = event.getPlayer();
        String[] split = event.getMessage().split(" ");
        String command = split[0].substring(1);
        if(command.startsWith("/"))
            command = command.substring(1);
        
        // VoxelSniper
        if(command.startsWith("v ") || split.length > 1)
        {
        	int id = CUtils.convert(split[1], Integer.class, 0);
            if(id == 175) // tall flower, 175:6 crashes the client
            {
                event.setCancelled(true);
                plugin.sendMessage(player, "&cWarning: Cannot use this block");
            }
        }
        
        if(command.startsWith("u ")) // notice space at the end
        {
            event.setCancelled(true);
            plugin.sendMessage(player, "&cWarning: You can only undo one action at a time");
            return;
        }
        
        if(AntiCheatListener.isWeCommand(command))
        {
        	if(isSelectionTooBig(player))
            {
                event.setCancelled(true);
                plugin.sendMessage(player, "&cWarning: Your selection is too big. To reset your selection, type &a//sel");
            }
        }
    }
    
    
    @EventHandler()
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        final PlayerInteractEvent event = e; 
        plugin.scheduleSyncDelayedTask(new Runnable()
        {
            @Override
            public void run()
            {
            	if(event.getMaterial() == Material.WOOD_AXE)
            	{
            		Action action = event.getAction();
            		if (action == Action.RIGHT_CLICK_BLOCK
            		||  action == Action.LEFT_CLICK_BLOCK)
            		{
            			Player player = event.getPlayer();
                        if (isSelectionTooBig(player))
                            plugin.sendMessage(player, "&cWarning: Your selection is too big");
            		}
            	}
            }
        });
        
    }
    
    private boolean isSelectionTooBig(Player player)
    {
        LocalSession session = Plugin.worldedit.getSession(player);
        int limit = session.getBlockChangeLimit();
        if(limit == -1)
            return false;
        try
        {
            Region selection = session.getSelection(session.getSelectionWorld());
            return
                selection.getWidth()  > 750
            ||  selection.getLength() > 750
            ||  selection.getArea()   > 5 * 1000 * 1000 // 5mln blocks is too much ;)
            ||  selection.getArea()   > limit * 10; // In case of integer overflow above
        }
        catch (IncompleteRegionException | NullPointerException e)
        {
            // Nothing
        }
        return false;
    }
    
    
    @Override public boolean isListener()   { return true; }
    @Override public String[] getCommands() { return null; }
}