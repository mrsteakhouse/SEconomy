package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.util.UUIDFetcher;
import com.mrsteakhouse.util.Util;

public class Money implements CommandExecutor
{
	private final static String perm[] =
	{ "seconomy.coinpurse.show", "seconomy.account.deposit",
			"seconomy.account.withdraw", "seconomy.money.top",
			"secomony.money.top.admin" };
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

			if (args.length == 2)
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

					boolean bankBlockFound = false;
					Location target = player.getTargetBlock(null, 10)
							.getLocation();
					if (plugin.getBankBlocks().contains(target))
					{
						bankBlockFound = true;
					}

					for (Location loc : Util.circle(player,
							player.getLocation(), 5, 5, false, true, 0))
					{
						if (plugin.getBankBlocks().contains(loc))
						{
							bankBlockFound = true;
							break;
						}
					}

					if (bankBlockFound)
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

					boolean bankBlockFound = false;
					Location target = player.getTargetBlock(null, 10)
							.getLocation();
					if (plugin.getBankBlocks().contains(target))
					{
						bankBlockFound = true;
					}

					for (Location loc : Util.circle(player,
							player.getLocation(), 5, 5, false, true, 0))
					{
						if (plugin.getBankBlocks().contains(loc))
						{
							bankBlockFound = true;
							break;
						}
					}

					if (bankBlockFound)
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

			if (args.length >= 1)
			{
				if (args[0].equalsIgnoreCase("top"))
				{
					int page = 0;
					int linesPerPage = 10;

					List<com.mrsteakhouse.account.Account> sortedAccounts = new ArrayList<com.mrsteakhouse.account.Account>();
					sortedAccounts.addAll(plugin.getAccountList().values());
					Collections.sort(sortedAccounts);
					sortedAccounts.remove(plugin.getAdminAccount());
					for (String name : plugin.getMoneyTopFilter())
					{
						try
						{
							sortedAccounts.remove(plugin.getAccountList().get(
									UUIDFetcher.getUUIDOf(name)));
						} catch (Exception e)
						{
						}
					}

					if (!player.hasPermission(perm[4]))
					{
						player.sendMessage(ChatColor.RED
								+ MessageFormat.format(String.valueOf(plugin
										.getLangData().get("7")), perm[4]));
						return false;
					}

					if (args.length == 2)
					{
						if (!player.hasPermission(perm[3]))
						{
							player.sendMessage(ChatColor.RED
									+ MessageFormat.format(String
											.valueOf(plugin.getLangData().get(
													"7")), perm[3]));
							return false;
						}

						page = Integer.valueOf(args[1]);
						if (page > 0)
						{
							page--;
						}

						sender.sendMessage(MessageFormat.format(
								"{0}-------- Top Player Page {1}"
										+ (page + 1)
										+ "{0}/{1}"
										+ (sortedAccounts.size() <= 10 ? 1
												: ((int) (sortedAccounts.size()
														/ linesPerPage + 0.5)))
										+ "{0} --------", ChatColor.DARK_GREEN,
								ChatColor.GOLD));

						if (!Util.isInteger(args[1]))
						{
							player.sendMessage(ChatColor.DARK_RED
									+ String.valueOf(plugin.getLangData().get(
											"9")));
							return false;
						}
					} else
					{
						sender.sendMessage(MessageFormat.format(
								"{0}------------ Top Player ------------",
								ChatColor.DARK_GREEN, ChatColor.GOLD));
					}

					com.mrsteakhouse.account.Account tempAcc;
					double value;
					String playerName;
					for (int i = (page * linesPerPage); i < (page
							* linesPerPage + linesPerPage)
							&& i < sortedAccounts.size(); i++)
					{
						tempAcc = sortedAccounts.get(i);
						value = tempAcc.getAccountValue()
								+ tempAcc.getCoinpurseValue();
						if (tempAcc.getPlayer().equals(new UUID(0, 0)))
						{
							playerName = "Adminaccount";
						} else
						{
							playerName = Bukkit.getOfflinePlayer(
									tempAcc.getPlayer()).getName();
						}
						sender.sendMessage(MessageFormat.format(
								"{0}{1}. {2}: {3}{4}{0}{5}", ChatColor.GREEN,
								i + 1, playerName, ChatColor.GOLD, Util
										.formatNumber(value), plugin
										.getLangData().get("currSymbol")));
					}
					sender.sendMessage(ChatColor.DARK_GREEN
							+ "-----------------------------------");
					return true;
				}
			}

			double money = plugin.getAccountList().get(player.getUniqueId())
					.getCoinpurseValue();
			player.sendMessage(MessageFormat.format(String.valueOf(plugin
					.getLangData().get("24")),
					(money >= 0 ? ChatColor.DARK_GREEN : ChatColor.DARK_RED),
					Util.formatNumber(money), ChatColor.RESET, plugin
							.getLangData().get("currSymbol")));
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
		if (sender instanceof Player)
		{
			if ((!sender.hasPermission(perm[0]))
					|| (!sender.hasPermission(perm[1]))
					|| (!sender.hasPermission(perm[2]))
					|| (!sender.hasPermission(perm[3]))
					|| (!sender.hasPermission(perm[4])))
			{
				return;
			}
		}
		sender.sendMessage(ChatColor.YELLOW
				+ "/money [(withdraw | deposit) <amount>] | [top <page>]: "
				+ ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("25")));
	}

}
