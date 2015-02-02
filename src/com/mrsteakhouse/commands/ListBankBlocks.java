package com.mrsteakhouse.commands;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mrsteakhouse.SEconomy;

public class ListBankBlocks implements SubCommand
{
	private final String perm = "seconomy.bank.showbanklist";
	private SEconomy plugin;

	public ListBankBlocks(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (!player.hasPermission(perm))
			{
				player.sendMessage(noPerm());
				return false;
			}
		}

		int i = 1;
		if (!plugin.getBankBlocks().isEmpty())
		{
			for (Location loc : plugin.getBankBlocks())
			{
				sender.sendMessage(String.format(MessageFormat.format(
						String.valueOf(plugin.getLangData().get("36")),
						ChatColor.GOLD, ChatColor.AQUA), i, loc.getWorld()
						.getName(), (int) loc.getX(), (int) loc.getY(),
						(int) loc.getZ()));
				i++;
			}
		} else
		{
			sender.sendMessage(String.valueOf(plugin.getLangData().get("37")));
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
		sender.sendMessage(ChatColor.YELLOW + "/se listbankblocks: "
				+ ChatColor.AQUA
				+ String.valueOf(plugin.getLangData().get("38")));
	}

}
