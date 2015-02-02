package com.mrsteakhouse;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

import com.mrsteakhouse.account.Account;
import com.mrsteakhouse.util.UUIDFetcher;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

public class VaultConnector implements Economy
{
	private final Economy eco = SEconomy.getEconomy();
	private SEconomy plugin;

	public VaultConnector(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public boolean isEnabled()
	{
		return eco != null;
	}

	@Override
	public String getName()
	{
		return "SEconomy";
	}

	@Override
	public boolean hasBankSupport()
	{
		return false;
	}

	@Override
	public int fractionalDigits()
	{
		return 2;
	}

	@Override
	public EconomyResponse bankBalance(String name)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse bankDeposit(String name, double amount)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse bankHas(String name, double amount)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse bankWithdraw(String name, double amount)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse createBank(String name, String player)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse createBank(String name, OfflinePlayer player)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public boolean createPlayerAccount(String playerName)
	{
		return hasAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player)
	{
		return hasAccount(player);
	}

	@Override
	public boolean createPlayerAccount(String playerName, String worldName)
	{
		return hasAccount(playerName);
	}

	@Override
	public boolean createPlayerAccount(OfflinePlayer player, String worldName)
	{
		return hasAccount(player);
	}

	@Override
	public String currencyNamePlural()
	{
		return String.valueOf(plugin.getLangData().get("currName"));
	}

	@Override
	public String currencyNameSingular()
	{
		return String.valueOf(plugin.getLangData().get("currName"));
	}

	@Override
	public EconomyResponse deleteBank(String name)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, double amount)
	{
		UUID uuid = null;
		try
		{
			uuid = UUIDFetcher.getUUIDOf(playerName);
		} catch (Exception e)
		{
			return null;
		}
		Account account = plugin.getAccountList().get(uuid);
		return depositPlayer(account, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player, double amount)
	{
		UUID uuid = player.getUniqueId();
		Account account = plugin.getAccountList().get(uuid);
		return depositPlayer(account, amount);
	}

	@Override
	public EconomyResponse depositPlayer(String playerName, String worldName,
			double amount)
	{
		UUID uuid;
		try
		{
			uuid = UUIDFetcher.getUUIDOf(playerName);
		} catch (Exception e)
		{
			return null;
		}
		Account account = plugin.getAccountList().get(uuid);
		return depositPlayer(account, amount);
	}

	@Override
	public EconomyResponse depositPlayer(OfflinePlayer player,
			String worldName, double amount)
	{
		UUID uuid = player.getUniqueId();
		Account account = plugin.getAccountList().get(uuid);
		return depositPlayer(account, amount);
	}

	private EconomyResponse depositPlayer(Account account, double amount)
	{
		if (account.depositIntoCoinpurse(amount))
		{
			return new EconomyResponse(amount, account.getCoinpurseValue(),
					ResponseType.SUCCESS, null);
		} else
		{
			return new EconomyResponse(0, account.getCoinpurseValue(),
					ResponseType.FAILURE, String.valueOf(plugin.getLangData()
							.get("12")));
		}
	}

	@Override
	public String format(double amount)
	{
		return String.valueOf(MessageFormat.format("{0}{1}", amount,
				String.valueOf(plugin.getLangData().get("currSymbol"))));
	}

	@Override
	public double getBalance(String playerName)
	{
		try
		{
			return plugin.getAccountList()
					.get(UUIDFetcher.getUUIDOf(playerName)).getCoinpurseValue();
		} catch (Exception e)
		{
			return 0;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player)
	{
		return plugin.getAccountList().get(player.getUniqueId())
				.getCoinpurseValue();
	}

	@Override
	public double getBalance(String playerName, String world)
	{
		try
		{
			return plugin.getAccountList()
					.get(UUIDFetcher.getUUIDOf(playerName)).getCoinpurseValue();
		} catch (Exception e)
		{
			return 0;
		}
	}

	@Override
	public double getBalance(OfflinePlayer player, String world)
	{
		return plugin.getAccountList().get(player.getUniqueId())
				.getCoinpurseValue();
	}

	@Override
	public List<String> getBanks()
	{
		return new ArrayList<>();
	}

	@Override
	public boolean has(String playerName, double amount)
	{
		try
		{
			return plugin.getAccountList()
					.get(UUIDFetcher.getUUIDOf(playerName)).hasCoinpuse(amount);
		} catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public boolean has(OfflinePlayer player, double amount)
	{
		return plugin.getAccountList().get(player.getUniqueId())
				.hasCoinpuse(amount);
	}

	@Override
	public boolean has(String playerName, String worldName, double amount)
	{
		try
		{
			return plugin.getAccountList()
					.get(UUIDFetcher.getUUIDOf(playerName)).hasCoinpuse(amount);
		} catch (Exception e)
		{
			return false;
		}
	}

	@Override
	public boolean has(OfflinePlayer player, String worldName, double amount)
	{
		return plugin.getAccountList().get(player.getUniqueId())
				.hasCoinpuse(amount);
	}

	@Override
	public boolean hasAccount(String playerName)
	{
		UUID uuid = null;
		try
		{
			uuid = UUIDFetcher.getUUIDOf(playerName);
		} catch (Exception e)
		{
			return false;
		}
		return plugin.getAccountList().get(uuid) != null;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player)
	{
		return plugin.getAccountList().get(player.getUniqueId()) != null;
	}

	@Override
	public boolean hasAccount(String playerName, String worldName)
	{
		UUID uuid = null;
		try
		{
			uuid = UUIDFetcher.getUUIDOf(playerName);
		} catch (Exception e)
		{
			return false;
		}
		return plugin.getAccountList().get(uuid) != null;
	}

	@Override
	public boolean hasAccount(OfflinePlayer player, String worldName)
	{
		return plugin.getAccountList().get(player.getUniqueId()) != null;
	}

	@Override
	public EconomyResponse isBankMember(String name, String playerName)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse isBankMember(String name, OfflinePlayer player)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse isBankOwner(String name, String playerName)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	@Override
	public EconomyResponse isBankOwner(String name, OfflinePlayer player)
	{
		return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED,
				String.valueOf(plugin.getLangData().get("63")));
	}

	private EconomyResponse withdrawPlayer(Account account, double amount)
	{
		if (account.withdrawFromCoinpurse(amount))
		{
			return new EconomyResponse(amount, account.getCoinpurseValue(),
					ResponseType.SUCCESS, null);
		} else
		{
			return new EconomyResponse(0, account.getCoinpurseValue(),
					ResponseType.FAILURE, String.valueOf(plugin.getLangData()
							.get("16")));
		}
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, double amount)
	{
		Account account = null;
		try
		{
			account = plugin.getAccountList().get(
					UUIDFetcher.getUUIDOf(playerName));
		} catch (Exception e)
		{
			return new EconomyResponse(0, 0, ResponseType.FAILURE,
					String.valueOf(plugin.getLangData().get("64")));
		}
		return withdrawPlayer(account, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount)
	{
		return withdrawPlayer(
				plugin.getAccountList().get(player.getUniqueId()), amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(String playerName, String worldName,
			double amount)
	{
		Account account = null;
		try
		{
			account = plugin.getAccountList().get(
					UUIDFetcher.getUUIDOf(playerName));
		} catch (Exception e)
		{
			return new EconomyResponse(0, 0, ResponseType.FAILURE,
					String.valueOf(plugin.getLangData().get("64")));
		}
		return withdrawPlayer(account, amount);
	}

	@Override
	public EconomyResponse withdrawPlayer(OfflinePlayer player,
			String worldName, double amount)
	{
		return withdrawPlayer(
				plugin.getAccountList().get(player.getUniqueId()), amount);
	}
}