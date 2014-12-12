/* ctools
 * Copyright (C) 2013 Norbert Kawinski (norbert.kawinski@gmail.com)

 */

package castro.ctools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import castro.base.BaseCCommand;
import castro.base.CCommandMgr;
import castro.base.plugin.CPlugin;
import castro.commands.ClearScreen;
import castro.commands.Pay;
import castro.commands.SetRank;
import castro.commands.Spawner;
import castro.commands.Sudo;
import castro.commands.Who;

public class CommandMgr extends CCommandMgr 
{
	public CommandMgr(CPlugin plugin)
    {
	    super(plugin);
    }
	
	@Override
    protected BaseCCommand getCommand(CommandSender sender, Command cmd, String[] args)
    {
		switch(cmd.getName())
		{
		case "clearscreen": return new ClearScreen();
		case "spawner":     return new Spawner();
		case "setrank":     return new SetRank();
		case "sudo":        return new Sudo();
		case "list":        return new Who();
		case "pay":         return new Pay();
		}
		return null;
    }
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String[] args)
	{
		// TODO: is lowercase really needed? Let's see :P
		/*for(int i = 0; i < args.length; ++i)
			args[i] = args[i].toLowerCase();*/
		return super.onCommand(sender, command, args);
	}
}
