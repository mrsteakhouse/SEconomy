package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;

public class Send implements SubCommand
{
	private final String perm = "seconomy.coinpurse.send";
	private SEconomy plugin;

	public Send(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (!(sender instanceof Player))
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("4")));
			return false;
		}

		Player player = (Player) sender;
		if (!player.hasPermission(perm))
		{
			player.sendMessage(noPerm());
			return false;
		}

		if (args.length != 2)
		{
			player.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		try
		{
			if (!plugin.getAccountList().containsKey(
					UUIDFetcher.getUUIDOf(args[0]))
					&& !args[0].equalsIgnoreCase("admin"))
			{
				player.sendMessage(ChatColor.DARK_RED
						+ String.valueOf(plugin.getLangData().get("26")));
				return false;
			}
		} catch (Exception e1)
		{
		}

		double amount = 0;
		String playerName = args[0];
		com.mrsteakhouse.account.Account acc = null;
		UUID uuid = null;

		if (player.getName().equals(playerName))
		{
			player.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("43")));
			return false;
		}

		try
		{
			if (args[0].equalsIgnoreCase("admin"))
			{
				uuid = new UUID(0, 0);
			} else
			{
				uuid = UUIDFetcher.getUUIDOf(args[0]);
			}
			amount = Double.parseDouble(args[1]);
		} catch (Exception e)
		{
		}

		if (uuid == null)
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("9")));
			return false;
		}

		acc = plugin.getAccountList().get(uuid);

		if (amount <= 0)
		{
			player.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("58")));
			return false;
		}

		if (plugin.getAccountList().get(player.getUniqueId()).send(acc, amount))
		{
			player.sendMessage(MessageFormat.format(String.valueOf(plugin
					.getLangData().get("27")), ChatColor.DARK_GREEN, String
					.valueOf(amount), plugin.getLangData().get("currSymbol"),
					ChatColor.RESET));
			Player play = Bukkit.getPlayer(playerName);
			if (play != null)
			{
				play.sendMessage(MessageFormat.format(
						String.valueOf(plugin.getLangData().get("42")),
						ChatColor.DARK_GREEN, String.valueOf(amount),
						String.valueOf(plugin.getLangData().get("currSymbol")),
						ChatColor.RESET, player.getName()));
			}
			return true;
		} else
		{
			player.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("28")));
			return false;
		}
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
		sender.sendMessage(ChatColor.YELLOW + "/se send <name> <amount>: "
				+ ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("29")));
	}

}
