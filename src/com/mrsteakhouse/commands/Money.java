package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.Util;

public class Money implements CommandExecutor
{
	// TODO: eigene subcommands mit withdraw und deposit schreiben. am besten
	// kein eigenes paket
	private final String perm[] =
	{ "seconomy.coinpurse.show", "seconomy.account.deposit",
			"seconomy.account.withdraw" };
	private static SEconomy plugin = null;

	public Money(SEconomy plugin)
	{
		if (Money.plugin == null)
		{
			Money.plugin = plugin;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s,
			String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("money"))
		{
			if (!(sender instanceof Player))
			{
				sender.sendMessage(String
						.valueOf(plugin.getLangData().get("4")));
				return false;
			}

			Player player = (Player) sender;
			if (!player.hasPermission(perm[0]))
			{
				player.sendMessage(noPerm());
				return false;
			}

			if (args.length > 0)
			{
				if (!Util.isDouble(args[1]))
				{
					player.sendMessage(ChatColor.DARK_RED
							+ String.valueOf(plugin.getLangData().get("10")));
					return false;
				}

				double amount = Double.valueOf(args[1]);
				if (amount <= 0)
				{
					sender.sendMessage(ChatColor.DARK_RED
							+ String.valueOf(plugin.getLangData().get("58")));
					return false;
				}

				if (args[0].equalsIgnoreCase("withdraw"))
				{
					if (!player.hasPermission(perm[2]))
					{
						player.sendMessage(ChatColor.RED
								+ MessageFormat.format(String.valueOf(plugin
										.getLangData().get("7")), perm[2]));
					}

					Location target = player.getTargetBlock(null, 10)
							.getLocation();
					if (plugin.getBankBlocks().contains(target))
					{
						com.mrsteakhouse.account.Account acc = plugin
								.getAccountList().get(player.getUniqueId());

						if (acc.withdraw(amount))
						{
							player.sendMessage(MessageFormat.format(String
									.valueOf(plugin.getLangData().get("15")),
									ChatColor.AQUA, String.valueOf(amount),
									plugin.getLangData().get("currSymbol"),
									ChatColor.DARK_GREEN));
						} else
						{
							player.sendMessage(ChatColor.DARK_RED
									+ String.valueOf(plugin.getLangData().get(
											"16")));
						}
						return true;
					}
					player.sendMessage(ChatColor.DARK_RED
							+ String.valueOf(plugin.getLangData().get("17")));
					return true;

				} else if (args[0].equalsIgnoreCase("deposit"))
				{
					if (!player.hasPermission(perm[1]))
					{
						player.sendMessage(ChatColor.RED
								+ MessageFormat.format(String.valueOf(plugin
										.getLangData().get("7")), perm[1]));
					}
					Location target = player.getTargetBlock(null, 10)
							.getLocation();
					if (plugin.getBankBlocks().contains(target))
					{
						com.mrsteakhouse.account.Account acc = plugin
								.getAccountList().get(player.getUniqueId());
						if (acc.deopsit(amount, plugin.getTax()))
						{
							player.sendMessage(MessageFormat.format(String
									.valueOf(plugin.getLangData().get("11")),
									ChatColor.AQUA, String.valueOf(amount),
									plugin.getLangData().get("currSymbol"),
									ChatColor.DARK_GREEN));
						} else
						{
							player.sendMessage(ChatColor.DARK_RED
									+ String.valueOf(plugin.getLangData().get(
											"12")));
						}
						return true;
					}
					player.sendMessage(ChatColor.DARK_RED
							+ String.valueOf(plugin.getLangData().get("13")));
					return true;
				}
			}

			double money = plugin.getAccountList().get(player.getUniqueId())
					.getCoinpurseValue();
			player.sendMessage(MessageFormat.format(String.valueOf(plugin
					.getLangData().get("24")),
					(money >= 0 ? ChatColor.DARK_GREEN : ChatColor.DARK_RED),
					money, plugin.getLangData().get("currSymbol")));
			return true;
		}
		return false;
	}

	public String noPerm()
	{
		return ChatColor.RED
				+ MessageFormat.format(
						String.valueOf(plugin.getLangData().get("7")), perm[0]);
	}

	public static void help(CommandSender sender)
	{
		sender.sendMessage(ChatColor.YELLOW
				+ "/money [(withdraw | deposit) <amount>]: " + ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("25")));
	}

}
