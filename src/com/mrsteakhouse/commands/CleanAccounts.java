package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;

public class CleanAccounts implements SubCommand
{
	private SEconomy plugin;
	private String perm = "seconomy.cleanaccounts";

	public CleanAccounts(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!sender.hasPermission(perm))
		{
			sender.sendMessage(noPerm());
			return false;
		}

		plugin.cleanAccounts();

		sender.sendMessage(MessageFormat.format(
				String.valueOf(plugin.getLangData().get("68")),
				ChatColor.DARK_GREEN));

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
		if (sender instanceof Player)
		{
			if (!sender.hasPermission(perm))
			{
				return;
			}
		}
		sender.sendMessage(ChatColor.YELLOW
				+ "/se cleanaccounts: "
				+ ChatColor.AQUA
				+ MessageFormat.format(
						String.valueOf(plugin.getLangData().get("67")),
						// TODO variable einfügen
						"ONE MONTH"));
	}
}
