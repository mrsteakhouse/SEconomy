package com.mrsteakhouse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.avaje.ebean.EbeanServer;
import com.google.common.collect.Maps;
import com.mrsteakhouse.account.Account;
import com.mrsteakhouse.commands.Money;
import com.mrsteakhouse.commands.CommandHandler;
import com.mrsteakhouse.listener.BlockListener;
import com.mrsteakhouse.listener.PlayerListener;
import com.mrsteakhouse.sqlbridge.DBUtil;
import com.mrsteakhouse.sqlbridge.EBeanAccount;
import com.mrsteakhouse.sqlbridge.EBeanBankBlock;
import com.mrsteakhouse.sqlbridge.EBeanHandler;

public class SEconomy extends JavaPlugin
{
	private Map<String, Object> langData = Maps.newHashMap();
	private static String root = "./plugins/SEconomy";
	private String language = "english";
	private final String prefix = "[SEconomy] ";
	private int startvalue = 0;
	private double defaultLimit = 0;
	private static Economy economy = null;
	private Map<UUID, Account> accountList = new HashMap<UUID, Account>();
	private Set<Location> bankBlocks = new HashSet<Location>();
	private Account adminAccount;
	private double tax;
	private String DBUser = "";
	private String DBPass = "";
	private String DBUrl = "";
	private Calendar nextClean = new GregorianCalendar();
	private static EbeanServer database;

	@Override
	public void onEnable()
	{
		try
		{
			loadConfig(this);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		loadLanguage();

		setupEBean();

		loadAccounts();
		loadBankBlocks();

		getCommand("seconomy").setExecutor(new CommandHandler(this));
		getCommand("money").setExecutor(new Money(this));

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new BlockListener(this), this);
		pm.registerEvents(new PlayerListener(this), this);

		setupAdminAccount();
	}

	@Override
	public void onDisable()
	{
		Bukkit.getLogger().info(
				String.format("[%s] %s %s", getDescription().getName(),
						this.langData.get("1"), getDescription().getVersion()));
		saveConfig();
		saveConfiguration();
		saveAccounts();
		saveBankBlocks();
	}

	private void loadConfig(Plugin plugin) throws Exception
	{
		FileConfiguration config = plugin.getConfig();
		File file = new File(root + "/config.yml");

		if (!file.exists())
		{
			config.options().copyDefaults(true);
			saveConfig();
		}
		ConfigurationSection cs = config.getConfigurationSection("seconomy");
		root = cs.getString("root");
		language = cs.getString("language");
		startvalue = cs.getInt("startvalue");
		defaultLimit = cs.getDouble("limit");
		tax = cs.getDouble("tax") / 100;
		nextClean.setTimeInMillis(cs.getLong("nextClean"));
		DBUser = cs.getString("username");
		DBPass = cs.getString("password");
		DBUrl = "jdbc:mysql://" + cs.getString("hostname") + ":"
				+ cs.getInt("port") + "/" + cs.getString("database");
	}

	@SuppressWarnings("deprecation")
	private void saveConfiguration()
	{
		File file = new File(root, "config" + ".yml");
		InputStream is;
		FileConfiguration config;

		if (!file.exists())
		{
			is = getResource("config" + ".yml");
			config = YamlConfiguration.loadConfiguration(is);
		} else
		{
			config = YamlConfiguration.loadConfiguration(file);
		}
		ConfigurationSection cs = null;
		if ((cs = config.getConfigurationSection("seconomy")) == null)
		{
			config.createSection("seconomy");
			cs = config.getConfigurationSection("seconomy");
		}

		cs.set("root", root);
		cs.set("language", language);
		cs.set("startvalue", startvalue);
		cs.set("limit", defaultLimit);
		cs.set("tax", tax * 100);
		cs.set("nextClean", nextClean.getTimeInMillis());
		cs.set("username", DBUser);
		cs.set("password", DBPass);

		try
		{
			config.save(file);
		} catch (IOException e)
		{
			Bukkit.getLogger().severe(String.valueOf(this.langData.get("2")));
			e.printStackTrace();
		}
	}

	private void setupEBean()
	{
		DBUtil DB = new DBUtil(this)
		{
			protected List<Class<?>> getDatabaseClasses()
			{
				List<Class<?>> list = new ArrayList<Class<?>>();
				list.add(EBeanAccount.class);
				list.add(EBeanBankBlock.class);
				return list;
			}
		};

		try
		{
			DB.initializeDatabase("com.mysql.jdbc.Driver", DBUrl, DBUser,
					DBPass, "SERIALIZABLE");
		} catch (Exception e)
		{
			List<Class<?>> tableClasses = new LinkedList<Class<?>>();
			tableClasses.add(EBeanAccount.class);
			tableClasses.add(EBeanBankBlock.class);

			try
			{
				for (Class<?> tableClass : tableClasses)
					getDatabase().find(tableClass).findRowCount();
			} catch (PersistenceException ex)
			{
				installDDL();
			}
		}
		database = DB.getDatabase();
	}

	@SuppressWarnings("deprecation")
	private void loadLanguage()
	{
		File file = new File(root, language + ".yml");
		InputStream is;
		FileConfiguration datei;

		if (!file.exists())
		{
			is = getResource(language + ".yml");
			datei = YamlConfiguration.loadConfiguration(is);
		} else
		{
			datei = YamlConfiguration.loadConfiguration(file);
		}
		ConfigurationSection cs = datei.getConfigurationSection(language);
		langData = cs.getConfigurationSection("values").getValues(false);

		try
		{
			datei.save(file);
		} catch (IOException e)
		{
			Bukkit.getLogger().severe("Error loading language file");
			e.printStackTrace();
		}
	}

	private void loadBankBlocks()
	{
		EBeanHandler handler = EBeanHandler.getEBH();
		bankBlocks = handler.getBankBlocks();
	}

	private void saveBankBlocks()
	{
		for (Location loc : bankBlocks)
		{
			if (!EBeanHandler.getEBH().hasBankBlock(loc))
			{
				EBeanHandler.getEBH().storeBankBlock(loc);
			}
		}
	}

	private void saveAccounts()
	{
		EBeanHandler handler = EBeanHandler.getEBH();
		for (Account acc : accountList.values())
		{
			if (handler.hasAccount(acc.getPlayer()))
			{
				if (!handler.updateAccount(acc))
				{
					Bukkit.getLogger().log(
							Level.INFO,
							"Failed to update sql query for " + acc.getPlayer()
									+ ". Inform your Dev.");
				}
			} else
			{
				handler.storeAccount(acc);
			}
		}
	}

	private void loadAccounts()
	{
		accountList = EBeanHandler.getEBH().getAccounts();
		adminAccount = accountList.get(new UUID(0, 0));
	}

	private void setupAdminAccount()
	{
		if (!this.accountList.containsKey(new UUID(0, 0)))
		{
			accountList.put(new UUID(0, 0), new Account(this, new UUID(0, 0)));
		}

		adminAccount = accountList.get(new UUID(0, 0));
	}

	public Map<String, Object> getLangData()
	{
		return this.langData;
	}

	public void reload()
	{
		saveAccounts();
		saveBankBlocks();

		try
		{
			loadConfig(this);
		} catch (Exception e)
		{
			Bukkit.getLogger().log(Level.CONFIG,
					getPrefix() + "Error loading Config");
		}
		accountList.clear();
		bankBlocks.clear();

		loadLanguage();
		loadAccounts();
		loadBankBlocks();

		Bukkit.getLogger().fine(this.prefix + this.langData.get("3"));
	}

	public String getPrefix()
	{
		return this.prefix;
	}

	public Map<UUID, Account> getAccountList()
	{
		return this.accountList;
	}

	public static Economy getEconomy()
	{
		return economy;
	}

	public void resetCoinpurse(Player player)
	{
		Account acc = accountList.get(player.getUniqueId());
		acc.setCoinpurseValue(0.0f);
	}

	public void createAccount(Player player)
	{
		if (!accountList.containsKey(player.getUniqueId()))
		{
			Account acc = new Account(this, player.getUniqueId(), startvalue,
					0, defaultLimit);
			accountList.put(player.getUniqueId(), acc);
			EBeanHandler.getEBH().storeAccount(acc);
		}
	}

	public void cleanAccounts()
	{
		// TODO routine einfügen
	}

	public Set<Location> getBankBlocks()
	{
		return this.bankBlocks;
	}

	public boolean addBankBlock(Location loc)
	{
		bankBlocks.add(loc);
		return EBeanHandler.getEBH().storeBankBlock(loc);
	}

	public boolean delBankBlock(Location loc)
	{
		bankBlocks.remove(loc);
		return EBeanHandler.getEBH().destroyBankBlock(loc);
	}

	public Calendar getNextClean()
	{
		return nextClean;
	}

	public void setNextClean(Calendar nextClean)
	{
		this.nextClean = nextClean;
	}

	public double getTax()
	{
		return tax;
	}

	public void setTax(double tax)
	{
		this.tax = tax;
	}

	public Account getAdminAccount()
	{
		return adminAccount;
	}

	public static File loadFile(String string)
	{
		File file = new File(root, string);

		return loadFile(file);
	}

	private static File loadFile(File file)
	{
		if (!file.exists())
		{
			try
			{
				if (file.getParent() != null)
				{
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}

	public static EbeanServer getDB()
	{
		return database;
	}
}
