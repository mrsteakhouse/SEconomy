package com.mrsteakhouse.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class Util
{
	public static boolean isDouble(String string)
	{
		try
		{
			Double.parseDouble(string);
			return true;
		} catch (NumberFormatException e)
		{
			return false;
		}
	}

	public static boolean isInteger(String string)
	{
		try
		{
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e)
		{
			return false;
		}
	}

	public static List<Location> circle(Player player, Location loc,
			int radius, int height, boolean hollow, boolean sphere, int plusY)
	{
		List<Location> circleblocks = new ArrayList<Location>();
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();

		for (int x = cx - radius; x <= cx + radius; x++)
		{
			for (int z = cz - radius; z <= cz + radius; z++)
			{
				for (int y = (sphere ? cy - radius : cy); y < (sphere ? cy
						+ radius : cy + height); y++)
				{
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z)
							+ (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < radius * radius
							&& !(hollow && dist < (radius - 1) * (radius - 1)))
					{
						Location l = new Location(loc.getWorld(), x, y + plusY,
								z);
						circleblocks.add(l);
					}
				}
			}
		}

		return circleblocks;
	}
	
	public static List<String> fetchPlayerName(String startWith)
	{
		List<String> names = new ArrayList<String>();
		OfflinePlayer[] playerArray = Bukkit.getOfflinePlayers();
		for(OfflinePlayer play : playerArray)
		{
			if(play.getName().toLowerCase().startsWith(startWith.toLowerCase()))
			{
				names.add(play.getName());
			}
		}
		return names;
	}
}
