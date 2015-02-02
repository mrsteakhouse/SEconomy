package com.mrsteakhouse.commands;

import org.bukkit.command.CommandSender;

public abstract interface SubCommand
{
	public abstract boolean onCommand(CommandSender sender, String[] args);
	public abstract String noPerm();
	public abstract void help(CommandSender sender);
}
