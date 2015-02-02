package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;

public class Reload implements SubCommand
{
	private final String perm = "seconomy.reload";
	private SEconomy plugin;

	public Reload(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			plugin.reload();
			return false;
		}

		if (!sender.hasPermission(perm))
		{
			sender.sendMessage(noPerm());
			return false;
		}
		plugin.reload();
		sender.sendMessage(String.valueOf(plugin.getLangData().get("39")));
		return true;
	}

	@Override
	public String noPerm()
	{
		return ChatColor.RED
				+ MessageFormat.format(
						String.valueOf(plugin.getLangData().get("7")), perm);
	}

	@Override
	public void help(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW + "/se reload: " + ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("40")));
	}
}
