package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.Util;

public class SetTax implements SubCommand
{
	private SEconomy plugin;
	private final String perm = "seconomy.settax";

	public SetTax(SEconomy plugin)
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

		if (args.length < 1)
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		if (!Util.isDouble(args[0]))
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		double tax = Double.parseDouble(args[0]);
		if ((tax <= 0) || (tax > 100))
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("58")));
			return false;
		}

		plugin.setTax(tax / 100);
		sender.sendMessage(MessageFormat.format(
				String.valueOf(plugin.getLangData().get("59")),
				ChatColor.DARK_GREEN, ChatColor.GOLD, tax));
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
		sender.sendMessage(ChatColor.YELLOW + "/se settax <amount>: "
				+ ChatColor.AQUA + plugin.getLangData().get("60"));
	}

}
