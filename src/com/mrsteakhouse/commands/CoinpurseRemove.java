package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;

public class CoinpurseRemove implements SubCommand
{
	private final String perm = "seconomy.coinpurse.remove";
	private SEconomy plugin;

	public CoinpurseRemove(SEconomy plugin)
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

		try
		{
			uuid = UUIDFetcher.getUUIDOf(args[0]);
			amount = Double.parseDouble(args[1]);
		} catch (Exception e)
		{
		}

		if (uuid == null)
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		account = plugin.getAccountList().get(uuid);

		if (amount <= 0)
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("58")));
			return false;
		}

		account.removeCoinpurseValue(amount);
		sender.sendMessage(MessageFormat.format(
				String.valueOf(plugin.getLangData().get("54")), ChatColor.GOLD,
				amount, String.valueOf(plugin.getLangData().get("currSymbol")),
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
		sender.sendMessage(ChatColor.YELLOW
				+ "/se walletremove <name> <amount>: " + ChatColor.AQUA
				+ plugin.getLangData().get("47"));
	}
}
