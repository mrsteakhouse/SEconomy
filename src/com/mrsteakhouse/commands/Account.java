package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;

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
		Location target = player.getTargetBlock(null, 10).getLocation();
		for (Location loc : plugin.getBankBlocks())
		{
			if (loc.equals(target))
			{
				double money = plugin.getAccountList()
						.get(player.getUniqueId()).getAccountValue();
				player.sendMessage(MessageFormat.format(
						String.valueOf(plugin.getLangData().get("5")),
						(money >= 0 ? ChatColor.DARK_GREEN : ChatColor.DARK_RED),
						money, plugin.getLangData().get("currSymbol")));
				return true;
			}
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
		sender.sendMessage(ChatColor.YELLOW + "/se acc: " + ChatColor.AQUA
				+ plugin.getLangData().get("8"));
	}

}
