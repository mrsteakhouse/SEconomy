package com.mrsteakhouse.commands;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

import com.mrsteakhouse.SEconomy;

public class CommandHandler implements CommandExecutor
{
	private SEconomy plugin;
	private TreeMap<String, SubCommand> commands;

	public CommandHandler(SEconomy plugin)
	{
		this.plugin = plugin;
		this.commands = new TreeMap<String, SubCommand>();
		loadCommands();
	}

	public void loadCommands()
	{
		// TODO: kommandos eintragen.
		commands.put("reload", new Reload(plugin));
		commands.put("addbank", new AddBank(plugin));
		commands.put("delbank", new DelBank(plugin));
		commands.put("acc", new Account(plugin));
		commands.put("send", new Send(plugin));
		commands.put("listbankblocks", new ListBankBlocks(plugin));
		commands.put("accadd", new AccountAdd(plugin));
		commands.put("walletadd", new CoinpurseAdd(plugin));
		commands.put("accremove", new AccountRemove(plugin));
		commands.put("walletremove", new CoinpurseRemove(plugin));
		commands.put("accset", new AccountSet(plugin));
		commands.put("walletset", new CoinpurseSet(plugin));
		commands.put("reset", new Reset(plugin));
		commands.put("settax", new SetTax(plugin));
		commands.put("showacc", new ShowAccount(plugin));
		commands.put("cleanaccounts", new CleanAccounts(plugin));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String s,
			String[] args)
	{
		String command = cmd.getName();
		PluginDescriptionFile pdfFile = this.plugin.getDescription();

		if (command.equalsIgnoreCase("seconomy"))
		{
			if ((args == null) || (args.length < 1))
			{
				sender.sendMessage(String.valueOf(ChatColor.GOLD)
						+ ChatColor.BOLD + "SEconomy - MrSteakhouse"
						+ ChatColor.RESET + ChatColor.YELLOW + " Version: "
						+ pdfFile.getVersion());
				sender.sendMessage(ChatColor.GOLD
						+ String.valueOf(plugin.getLangData().get("30")));
				return true;
			}
			if (args[0].equalsIgnoreCase("help"))
			{
				if (args.length == 1)
				{
					help(sender);
					return true;
				} else
				{
					SubCommand subClass = getCommandClass(args[1], sender);
					if (subClass != null)
					{
						subClass.help(sender);
					}
				}
			}

			String sub = args[0];
			Vector<String> l = new Vector<String>();
			l.addAll(Arrays.asList(args));
			l.remove(0);
			args = (String[]) l.toArray(new String[0]);
			SubCommand subClass = getCommandClass(sub, sender);

			if (subClass != null)
			{
				subClass.onCommand(sender, args);
			}
			return true;
		}
		return false;
	}

	public void help(CommandSender sender)
	{
		sender.sendMessage(String.valueOf(ChatColor.GOLD) + ChatColor.BOLD
				+ "SEconomy - MrSteakhouse" + ChatColor.RESET
				+ ChatColor.YELLOW + " Version: "
				+ plugin.getDescription().getVersion());
		Money.help(sender);
		for (SubCommand sub : commands.values())
		{
			sub.help(sender);
		}
	}

	public SubCommand getCommandClass(String command, CommandSender sender)
	{
		if (!this.commands.containsKey(command))
		{
			sender.sendMessage(plugin.getPrefix());
			sender.sendMessage(ChatColor.DARK_RED
					+ String.valueOf(plugin.getLangData().get("31")));
			sender.sendMessage(ChatColor.GOLD
					+ String.valueOf(plugin.getLangData().get("30")));
			return null;
		}

		try
		{
			return ((SubCommand) this.commands.get(command));
		} catch (Exception e)
		{
			e.printStackTrace();
			Bukkit.getLogger().log(
					Level.FINE,
					plugin.getPrefix()
							+ MessageFormat.format(
									String.valueOf(plugin.getLangData().get(
											"32")), command));
			return null;
		}
	}
}
