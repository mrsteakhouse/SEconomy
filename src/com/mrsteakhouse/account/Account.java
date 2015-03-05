package com.mrsteakhouse.account;

import java.util.UUID;

import com.mrsteakhouse.SEconomy;

public class Account implements Comparable<Account>
{
	private SEconomy plugin;
	private double accountValue;
	private double coinpurseValue;
	private UUID player;
	private double limit = -50.0f;

	public Account(SEconomy plugin, UUID player)
	{
		this(plugin, player, 0, 0, -50.0f);
	}

	public Account(SEconomy plugin, UUID player, double limit)
	{
		this(plugin, player, 0, 0, limit);
	}

	public Account(SEconomy plugin, UUID player, double aValue, double cpValue,
			double limit)
	{
		this.plugin = plugin;
		this.player = player;
		this.accountValue = aValue;
		this.coinpurseValue = cpValue;
		this.limit = limit;
	}

	public double getAccountValue()
	{
		return accountValue;
	}

	public void setAccountValue(double accountValue)
	{
		this.accountValue = accountValue;
	}

	public double getCoinpurseValue()
	{
		return coinpurseValue;
	}

	public void setCoinpurseValue(double coinpurseValue)
	{
		this.coinpurseValue = coinpurseValue;
	}

	public UUID getPlayer()
	{
		return player;
	}

	public void setPlayer(UUID player)
	{
		this.player = player;
	}

	public double getLimit()
	{
		return limit;
	}

	public void setLimit(double limit)
	{
		this.limit = limit;
	}

	public boolean withdraw(double amount)
	{
		if ((getAccountValue() - amount) >= getLimit())
		{
			addCoinpurseValue(amount);
			removeAccountValue(amount);
			return true;
		} else
		{
			return false;
		}
	}

	public boolean deopsit(double amount, double tax)
	{
		if (hasCoinpuse(amount))
		{
			double taxAmount = tax * amount;
			removeCoinpurseValue(amount);
			amount -= taxAmount;
			addAccountValue(amount);
			plugin.getAdminAccount().addAccountValue(taxAmount);
			return true;
		} else
		{
			return false;
		}
	}

	public boolean send(Account reciever, double amount)
	{
		if (hasCoinpuse(amount))
		{
			removeCoinpurseValue(amount);
			if (reciever.getPlayer().equals(new UUID(0, 0)))
			{
				reciever.addAccountValue(amount);
			} else
			{
				reciever.addCoinpurseValue(amount);
			}
			return true;
		} else
		{
			return false;
		}
	}

	public void addAccountValue(double amount)
	{
		this.accountValue += amount;
	}

	public void addCoinpurseValue(double amount)
	{
		this.coinpurseValue += amount;
	}

	public void removeAccountValue(double amount)
	{
		this.accountValue -= amount;
	}

	public void removeCoinpurseValue(double amount)
	{
		this.coinpurseValue -= amount;
	}

	public void reset()
	{
		this.accountValue = 0;
		this.coinpurseValue = 0;
	}

	public boolean hasCoinpuse(double amount)
	{
		return getCoinpurseValue() >= amount;
	}
	
	public boolean withdrawFromCoinpurse(double amount)
	{
		if (getCoinpurseValue() >= 0.0d)
		{
			removeCoinpurseValue(amount);
			return true;
		} else
		{
			return false;
		}
	}
	
	public boolean depositIntoCoinpurse(double amount)
	{
		addCoinpurseValue(amount);
		return true;
	}

	@Override
	public int compareTo(Account acc)
	{
		return (int) ((acc.getAccountValue() + acc.getCoinpurseValue()) - (this.getAccountValue() + this.getCoinpurseValue()));
	}
}
