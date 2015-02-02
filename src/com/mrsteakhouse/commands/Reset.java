package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;

public class Reset implements SubCommand
{
	private final String perm = "seconomy.reset";
	private SEconomy plugin;

	public Reset(SEconomy plugin)
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

		if (args.length != 1)
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		com.mrsteakhouse.account.Account account = null;
		UUID uuid = null;

		try
		{
			uuid = UUIDFetcher.getUUIDOf(args[0]);
		} catch (Exception e)
		{
		}

		if (uuid == null)
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		account = plugin.getAccountList().get(uuid);

		account.reset();
		sender.sendMessage(MessageFormat.format(
				String.valueOf(plugin.getLangData().get("57")),
				ChatColor.DARK_GREEN, ChatColor.AQUA, args[0]));
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
		sender.sendMessage(ChatColor.YELLOW + "/se reset <name>: "
				+ ChatColor.AQUA + plugin.getLangData().get("50"));
	}
}
