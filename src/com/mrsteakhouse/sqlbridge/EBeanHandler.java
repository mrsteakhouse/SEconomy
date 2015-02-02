package com.mrsteakhouse.sqlbridge;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.Update;
import com.mrsteakhouse.SEconomy;
import com.mrsteakhouse.account.Account;

public class EBeanHandler
{
	private final EbeanServer db = SEconomy.getDB();

	private static EBeanHandler ebh;

	public boolean storeBankBlock(Location location)
	{
		EBeanBankBlock bankblock = new EBeanBankBlock();
		bankblock.setWorld(location.getWorld().getName());
		bankblock.setX((int) location.getX());
		bankblock.setY((int) location.getY());
		bankblock.setZ((int) location.getZ());
		db.save(bankblock);
		return true;
	}

	public boolean destroyBankBlock(Location location)
	{
		return deleteBankBlock(location.getWorld().getName(),
				(int) location.getX(), (int) location.getY(),
				(int) location.getZ());
	}

	public boolean storeAccount(Account account)
	{
		if (hasAccount(account.getPlayer()))
			return false;

		EBeanAccount acc = new EBeanAccount();
		acc.setUser(account.getPlayer().toString());
		acc.setAccountValue(new BigDecimal(account.getAccountValue()));
		acc.setCoinpurseValue(new BigDecimal(account.getCoinpurseValue()));
		db.save(acc);
		return true;
	}

	public boolean hasAccount(UUID uuid)
	{
		int accCount = db.find(EBeanAccount.class).where()
				.ieq("user", uuid.toString()).findRowCount();
		return accCount == 1;
	}

	public boolean hasBankBlock(Location location)
	{
		int xCount = db.find(EBeanBankBlock.class).where()
				.eq("x", (int) location.getX()).findRowCount();
		int yCount = db.find(EBeanBankBlock.class).where()
				.eq("y", (int) location.getY()).findRowCount();
		int zCount = db.find(EBeanBankBlock.class).where()
				.eq("z", (int) location.getZ()).findRowCount();
		int worldCount = db.find(EBeanBankBlock.class).where()
				.ieq("world", location.getWorld().getName()).findRowCount();
		return (xCount == 1 && yCount == 1 && zCount == 1 && worldCount == 1);
	}

	public Set<Location> getBankBlocks()
	{
		List<SqlRow> result = db.createSqlQuery(
				"SELECT * FROM seconomy_bankblocks").findList();

		Set<Location> bankblocks = new HashSet<>();

		for (SqlRow c : result)
		{
			String worldName = c.getString("world");
			int x = c.getInteger("x");
			int y = c.getInteger("y");
			int z = c.getInteger("z");

			World world = Bukkit.getWorld(worldName);
			if (world == null)
				continue;

			Location loc = new Location(world, x, y, z);
			bankblocks.add(loc);

		}

		return bankblocks;
	}

	private boolean deleteBankBlock(String world, int x, int y, int z)
	{
		SqlUpdate deleteChest = db
				.createSqlUpdate("delete from seconomy_bankblocks where world = :world and x = :x and y = :y and z = :z");
		deleteChest.setParameter("world", world);
		deleteChest.setParameter("x", x);
		deleteChest.setParameter("y", y);
		deleteChest.setParameter("z", z);

		return deleteChest.execute() > 0;
	}

	public static EBeanHandler getEBH()
	{
		if (ebh != null)
			return ebh;
		ebh = new EBeanHandler();
		return ebh;
	}

	public Map<UUID, Account> getAccounts()
	{
		SqlQuery getAccounts = db
				.createSqlQuery("SELECT * FROM seconomy_accounts");
		SEconomy plugin = (SEconomy) Bukkit.getPluginManager().getPlugin(
				"SEconomy");
		Map<UUID, Account> accounts = new HashMap<UUID, Account>();
		for (SqlRow result : getAccounts.findSet())
		{
			String uuid = result.getString("user");
			double accountValue = result.getDouble("account_value");
			double coinpurseValue = result.getDouble("coinpurse_value");

			accounts.put(UUID.fromString(uuid),
					new Account(plugin, UUID.fromString(uuid), accountValue,
							coinpurseValue, plugin.getTax()));
		}

		return accounts;
	}

	public boolean updateAccount(Account account)
	{
//		String sqlquery = new String(
//				"UPDATE seconomy_accounts SET account_value = "
//						+ account.getAccountValue() + " and coinpurse_value = "
//						+ account.getCoinpurseValue() + " WHERE user = '"
//						+ account.getPlayer().toString() + "'");
//		SqlUpdate up = db
//				.createSqlUpdate("UPDATE seconomy_accounts SET account_value = :account_value, coinpurse_value = :coinpurse_value WHERE user = :user");
//		up.setParameter("account_value",
//				new BigDecimal(account.getAccountValue()));
//		up.setParameter("coinpurse_value",
//				new BigDecimal(account.getCoinpurseValue()));
//		up.setParameter("user", account.getPlayer().toString());
//		<
//		return up.execute() == 1;
		
		Set<EBeanAccount> accs = db.find(EBeanAccount.class).where().eq("user", account.getPlayer().toString()).findSet();
		
		if(accs.isEmpty())
		{
			return false;
		}
		
		EBeanAccount acc = (EBeanAccount) accs.toArray()[0];
		acc.setAccountValue(new BigDecimal(account.getAccountValue()));
		acc.setCoinpurseValue(new BigDecimal(account.getCoinpurseValue()));
		db.update(acc);
		return true;
	}

	public void deleteAccount(Account account)
	{
		// TODO implement deleteAccount, mayhaps?
		throw new RuntimeException(
				"delete account not supported yet in EBeanHandler");
	}

	/**
	 * The classes comprising the DB model, required for the EBean DDL
	 * ("data description language").
	 */
	public static List<Class<?>> getDatabaseClasses()
	{
		return Arrays.asList(EBeanAccount.class, EBeanBankBlock.class);
	}
}
