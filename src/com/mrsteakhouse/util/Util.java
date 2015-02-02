package com.mrsteakhouse.util;

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

	public static boolean isInCircle(Player playA, Player playB)
	{
		// TODO: add routine
		return false;
	}

	public static boolean isInRectangle(Player playA, Player playB)
	{
		// TODO: add routine
		return false;
	}
}
