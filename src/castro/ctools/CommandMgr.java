package castro.ctools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import castro.base.GenericCommandMgr;
import castro.commands.BaseCommand;
import castro.commands.ClearScreen;
import castro.commands.ModBroadcast;
import castro.commands.SetRank;
import castro.commands.Sudo;

public class CommandMgr implements GenericCommandMgr 
{	
	
	private BaseCommand getCommand(String command, String[] args)
	{
		switch(command)
		{
		case "clearscreen": return new ClearScreen();
		case "setrank":		return new SetRank();
		case "sudo":		return new Sudo();
		case "modbroadcast":return new ModBroadcast();
		}
		
		return null;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String[] args)
	{	
		for(int i = 0; i < args.length; ++i)
			args[i] = args[i].toLowerCase();
		
		BaseCommand ccommand = getCommand(command.getName(), args);
		if(ccommand == null)
			return false;
		return ccommand.exec(sender, args);
	}
}
