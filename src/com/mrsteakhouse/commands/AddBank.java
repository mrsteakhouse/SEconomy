package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.Util;

public class AddBank implements SubCommand
{
	private final String perm = "seconomy.bank.add";
	private SEconomy plugin;

	public AddBank(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		Location loc = null;
		if (args.length == 0)
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage(String
						.valueOf(plugin.getLangData().get("4")));
				return false;
			}
			Player player = (Player) sender;
			if (!player.hasPermission(perm))
			{
				player.sendMessage(noPerm());
				return false;
			}
			loc = player.getTargetBlock(null, 100).getLocation();
		} else if (args.length == 4)
		{
			if (sender instanceof Player)
			{
				if (!((Player) sender).hasPermission(perm))
				{
					((Player) sender).sendMessage(noPerm());
					return false;
				}
			}
			int x = 0, y = 0, z = 0;
			if (!Util.isInteger(args[1]) || !Util.isInteger(args[2])
					|| !Util.isInteger(args[3]))
			{
				sender.sendMessage(ChatColor.DARK_RED
						+ String.valueOf(plugin.getLangData().get("9")));
				return false;
			}
			x = Integer.valueOf(args[1]);
			y = Integer.valueOf(args[2]);
			z = Integer.valueOf(args[3]);

			World world = Bukkit.getWorld(args[0]);
			if (world == null)
			{
				sender.sendMessage(MessageFormat.format(
						String.valueOf(plugin.getLangData().get("18")),
						ChatColor.DARK_RED, ChatColor.AQUA, args[0],
						ChatColor.DARK_RED));
				return false;
			}

			loc = new Location(world, x, y, z);
		} else
		{
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("19")));
			return false;
		}
		if (plugin.addBankBlock(loc))
		{
			sender.sendMessage(MessageFormat.format(
					String.valueOf(plugin.getLangData().get("20")),
					ChatColor.DARK_GREEN, ChatColor.AQUA, loc.toString(),
					ChatColor.DARK_GREEN));
		} else
		{
			sender.sendMessage(ChatColor.DARK_GREEN
					+ String.valueOf(plugin.getLangData().get("21")));
		}
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
				+ "/se addbank [<world> <x> <y> <z>]: " + ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("22")));
	}
}
