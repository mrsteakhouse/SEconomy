package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;
import com.mrsteakhouse.util.Util;

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

		double amount = 0;
		com.mrsteakhouse.account.Account acc = null;
		UUID uuid = null;
		String playername = "";
		List<String> playernames;

		try
		{
			playernames = Util.fetchPlayerName(args[0]);
			if (playernames.size() > 1)
			{
				sender.sendMessage(MessageFormat.format(
						String.valueOf(plugin.getLangData().get("66")),
						ChatColor.RED));
				return false;
			} else if (playernames.isEmpty())
			{
				sender.sendMessage(MessageFormat.format(
						String.valueOf(plugin.getLangData().get("65")),
						ChatColor.RED));
				return false;
			}
			playername = playernames.get(0);
			uuid = UUIDFetcher.getUUIDOf(playername);
			if (!plugin.getAccountList().containsKey(uuid))
			{
				player.sendMessage(ChatColor.DARK_RED
						+ String.valueOf(plugin.getLangData().get("26")));
				return false;
			}
			amount = Double.parseDouble(args[1]);
		} catch (Exception e1)
		{
		}

		if (player.getName().equals(playername))
		{
			player.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("43")));
			return false;
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
			Player play = Bukkit.getPlayer(playername);
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
		if (sender instanceof Player)
		{
			if (!sender.hasPermission(perm))
			{
				return;
			}
		}
		sender.sendMessage(ChatColor.YELLOW + "/se send <name> <amount>: "
				+ ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("29")));
	}

}
