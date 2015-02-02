package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;

public class ShowAccount implements SubCommand
{
	private SEconomy plugin;
	private final String perm = "seconomy.show";

	public ShowAccount(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!sender.hasPermission(perm))
		{
			sender.sendMessage(noPerm());
		}

		if (args.length != 1)
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		com.mrsteakhouse.account.Account account = null;
		UUID uuid = null;

		if (!args[0].equalsIgnoreCase("admin"))
		{
			try
			{
				uuid = UUIDFetcher.getUUIDOf(args[0]);
			} catch (Exception e)
			{
			}
		} else
		{
			uuid = new UUID(0, 0);
		}

		if (uuid == null)
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		account = plugin.getAccountList().get(uuid);

		if (account == null)
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;

		}

		sender.sendMessage(MessageFormat.format(
				String.valueOf(plugin.getLangData().get("61")),
				ChatColor.DARK_GREEN, ChatColor.AQUA, args[0], ChatColor.GOLD,
				account.getAccountValue(),
				plugin.getLangData().get("currSymbol"),
				account.getCoinpurseValue()));
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
		sender.sendMessage(ChatColor.YELLOW + "/se showacc <name>: "
				+ ChatColor.AQUA + plugin.getLangData().get("62"));
	}

}
