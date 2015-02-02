package com.mrsteakhouse.listener;

import java.text.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import com.mrsteakhouse.SEconomy;

public class BlockListener implements Listener
{
	private SEconomy plugin;

	public BlockListener(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		Location loc = event.getBlock().getLocation();
		if (plugin.getBankBlocks().contains(loc))
		{
			if (event.getPlayer().getGameMode() == GameMode.CREATIVE)
			{
				event.setCancelled(true);
				return;
			}

			event.getPlayer().sendMessage(
					MessageFormat.format(
							String.valueOf(plugin.getLangData().get("33")),
							ChatColor.DARK_RED, ChatColor.DARK_GREEN,
							ChatColor.AQUA, loc.toString(),
							ChatColor.DARK_GREEN));
			plugin.delBankBlock(loc);
			return;
		}
	}
}
