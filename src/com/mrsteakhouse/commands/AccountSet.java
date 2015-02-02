package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;
import com.mrsteakhouse.util.Util;

public class AccountSet implements SubCommand
{
	private final String perm = "seconomy.account.set";
	private SEconomy plugin;

	public AccountSet(SEconomy plugin)
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

		if (args.length != 2)
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		com.mrsteakhouse.account.Account account = null;
		double amount = 0;
		UUID uuid = null;

		if (!args[0].equalsIgnoreCase("admin"))
		{
			try
			{
				uuid = UUIDFetcher.getUUIDOf(args[0]);
				amount = Double.parseDouble(args[1]);
			} catch (Exception e)
			{
			}
		} else
		{
			uuid = new UUID(0, 0);
			if (!Util.isDouble(args[1]))
			{
				sender.sendMessage(String
						.valueOf(plugin.getLangData().get("9")));
				return false;
			}
			amount = Double.parseDouble(args[1]);
		}

		if (uuid == null)
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		account = plugin.getAccountList().get(uuid);

		account.setAccountValue(amount);
		sender.sendMessage(MessageFormat.format(
				String.valueOf(plugin.getLangData().get("55")),
				ChatColor.DARK_GREEN, ChatColor.AQUA, args[0], ChatColor.GOLD,
				amount, String.valueOf(plugin.getLangData().get("currSymbol"))));
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
		sender.sendMessage(ChatColor.YELLOW
				+ "/se accset <name> <amount>: " + ChatColor.AQUA
				+ plugin.getLangData().get("48"));
	}
}
