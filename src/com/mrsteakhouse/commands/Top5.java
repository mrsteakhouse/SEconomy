package com.mrsteakhouse.commands;

import org.bukkit.command.CommandSender;

import com.mrsteakhouse.SEconomy;

public class Top5 implements SubCommand
{
	private SEconomy plugin;

	public Top5(SEconomy plugin)
	{
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{

		return false;
	}

	@Override
	public String noPerm()
	{
		return "";
	}

	@Override
	public void help(CommandSender sender)
	{
		// TODO Auto-generated method stub

	}

}
