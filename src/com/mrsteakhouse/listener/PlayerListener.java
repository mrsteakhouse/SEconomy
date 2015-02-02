package com.mrsteakhouse.listener;

import java.text.MessageFormat;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.account.Account;

public class PlayerListener implements Listener
{
	private SEconomy plugin;

	public PlayerListener(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if (event.getEntity() instanceof Player)
		{
			Player player = event.getEntity();
			player.sendMessage(MessageFormat.format(
					String.valueOf(plugin.getLangData().get("41")), plugin
							.getAccountList().get(player.getName())
							.getCoinpurseValue(),
					String.valueOf(plugin.getLangData().get("currSymbol"))));
			plugin.resetCoinpurse(player);
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		plugin.createAccount(player);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getPlayer() instanceof Player)
		{
			Player player = event.getPlayer();
			Location target = player.getTargetBlock(null, 5).getLocation();
			if (plugin.getBankBlocks().contains(target))
			{
				Account acc = null;
				if ((acc = plugin.getAccountList().get(player.getUniqueId())) != null)
				{
					if (event.getAction() == Action.LEFT_CLICK_BLOCK)
					{
						if (acc.deopsit(100.0f, plugin.getTax()))
						{
							player.sendMessage(MessageFormat.format(String
									.valueOf(plugin.getLangData().get("11")),
									ChatColor.AQUA, 100, plugin.getLangData()
											.get("currSymbol"),
									ChatColor.DARK_GREEN));
						} else
						{
							player.sendMessage(ChatColor.DARK_RED
									+ String.valueOf(plugin.getLangData().get(
											"12")));
						}
					} else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
					{
						if (acc.withdraw(100.0f))
						{
							player.sendMessage(MessageFormat.format(String
									.valueOf(plugin.getLangData().get("15")),
									ChatColor.AQUA, 100, plugin.getLangData()
											.get("currSymbol"),
									ChatColor.DARK_GREEN));
						} else
						{
							player.sendMessage(ChatColor.DARK_RED
									+ String.valueOf(plugin.getLangData().get(
											"16")));
						}
					}
				}

			}
		}
	}
}
