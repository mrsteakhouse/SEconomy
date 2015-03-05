package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.Util;

public class Account implements SubCommand
{

	private final String perm = "seconomy.account.show";
	private SEconomy plugin;

	public Account(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
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

		boolean bankBlockFound = false;
		Location target = player.getTargetBlock(null, 10).getLocation();
		if (plugin.getBankBlocks().contains(target))
		{
			bankBlockFound = true;
		}

		for (Location loc : Util.circle(player, player.getLocation(), 5, 5,
				false, true, 0))
		{
			if (plugin.getBankBlocks().contains(loc))
			{
				bankBlockFound = true;
				break;
			}
		}

		if (bankBlockFound)
		{
			double money = plugin.getAccountList().get(player.getUniqueId())
					.getAccountValue();
			player.sendMessage(MessageFormat.format(String.valueOf(plugin
					.getLangData().get("5")),
					(money >= 0 ? ChatColor.DARK_GREEN : ChatColor.DARK_RED),
					money, plugin.getLangData().get("currSymbol")));
			return true;
		}

		player.sendMessage(ChatColor.DARK_RED
				+ String.valueOf(plugin.getLangData().get("6")));
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
		sender.sendMessage(ChatColor.YELLOW + "/se acc: " + ChatColor.AQUA
				+ plugin.getLangData().get("8"));
	}

}
