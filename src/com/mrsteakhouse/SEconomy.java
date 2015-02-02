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
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
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
	private static final Logger logger = Logger.getLogger("Minecraft");
	private Map<UUID, Account> accountList = new HashMap<UUID, Account>();
	private Set<Location> bankBlocks = new HashSet<Location>();
	private Account adminAccount;
	private double tax;
	private boolean useSQL = true;
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

		if (!setupEconomy())
		{
			logger.severe(String.format(
					"[%s] - Disabled due to no Vault dependency found!",
					getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		setupAdminAccount();
	}

	@Override
	public void onDisable()
	{
		logger.info(String.format("[%s] %s %s", getDescription().getName(),
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
		useSQL = cs.getBoolean("usesql");
		nextClean.setTimeInMillis(cs.getLong("nextClean"));
	}

	@SuppressWarnings("deprecation")
	private void saveConfiguration()
	{
		if (useSQL)
		{
		} else
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
			cs.set("usesql", useSQL);
			cs.set("nextClean", nextClean.getTimeInMillis());

			try
			{
				config.save(file);
			} catch (IOException e)
			{
				logger.severe(String.valueOf(this.langData.get("2")));
				e.printStackTrace();
			}
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
		FileConfiguration config = YamlConfiguration
				.loadConfiguration(new File("bukkit.yml"));

		try
		{
			DB.initializeDatabase(config.getString("database.driver"),
					config.getString("database.url"),
					config.getString("database.username"),
					config.getString("database.password"),
					config.getString("database.isolation"));
		} catch (Exception e)
		{
		}
		database = DB.getDatabase();
	}

	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			return false;
		}
		final ServicesManager sm = getServer().getServicesManager();
		sm.register(Economy.class, new VaultConnector(this), this,
				ServicePriority.Highest);

		economy = getServer().getServicesManager()
				.getRegistration(Economy.class).getProvider();
		return economy != null;
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
			logger.severe("Error loading language file");
			e.printStackTrace();
		}
	}

	private void loadBankBlocks()
	{
		EBeanHandler handler = EBeanHandler.getEBH();
		if (!useSQL)
		{
			logger.log(Level.INFO,
					"Failed to initialize database or disabled. Switching to file storage");
			File file = new File(root, "banks.yml");
			FileConfiguration datei = YamlConfiguration.loadConfiguration(file);

			if (!file.exists())
			{
				return;
			}

			ConfigurationSection cs = datei.getConfigurationSection("banks");
			List<String> sList = cs.getStringList("values");

			for (String s : sList)
			{
				String[] splits = s.split(",");
				World w = Bukkit.getWorld(splits[0]);
				Location loc = new Location(w, Double.valueOf(splits[1]),
						Double.valueOf(splits[2]), Double.valueOf(splits[3]));
				bankBlocks.add(loc);
			}
		} else
		{
			bankBlocks = handler.getBankBlocks();
		}
	}

	private void saveBankBlocks()
	{
		if (!useSQL)
		{
			File file = new File(root, "banks.yml");
			FileConfiguration config = YamlConfiguration
					.loadConfiguration(file);
			ConfigurationSection cs;

			if (!file.exists())
			{
				config.createSection("banks");
			}

			List<String> sList = new LinkedList<String>();
			String s;
			for (Location loc : bankBlocks)
			{
				s = loc.getWorld().getName() + "," + loc.getX() + ","
						+ loc.getY() + "," + loc.getZ();
				sList.add(s);
			}

			cs = config.getConfigurationSection("banks");

			cs.set("values", sList);
			try
			{
				config.save(file);
			} catch (IOException e)
			{
				logger.severe(String.valueOf(this.langData.get("2")));
				e.printStackTrace();
			}
		} else
		{
			for (Location loc : bankBlocks)
			{
				if (!EBeanHandler.getEBH().hasBankBlock(loc))
				{
					EBeanHandler.getEBH().storeBankBlock(loc);
				}
			}
		}
	}

	private void saveAccounts()
	{
		if (!useSQL)
		{
			File file = new File(root, "accounts.yml");
			FileConfiguration datei;
			ConfigurationSection cs;

			datei = YamlConfiguration.loadConfiguration(file);
			if (!file.exists())
			{
				cs = datei.createSection("values");
			} else
			{
				cs = datei.getConfigurationSection("values");
			}
			Map<String, Object> accMap = new HashMap<String, Object>();
			Map<String, Object> cpMap = new HashMap<String, Object>();
			for (UUID uuid : accountList.keySet())
			{
				accMap.put(uuid.toString(), accountList.get(uuid)
						.getAccountValue());
				cpMap.put(uuid.toString(), accountList.get(uuid)
						.getCoinpurseValue());
			}

			cs.set("account", accMap);
			cs.set("coinpurse", cpMap);
			try
			{
				datei.save(file);
			} catch (IOException e)
			{
				logger.severe("failed to load accounts");
			}
		} else
		{
			EBeanHandler handler = EBeanHandler.getEBH();
			for (Account acc : accountList.values())
			{
				if (handler.hasAccount(acc.getPlayer()))
				{
					if(!handler.updateAccount(acc))
					{
						logger.log(Level.INFO, "Failed to update sql query for " + acc.getPlayer());
					}
				} else
				{
					handler.storeAccount(acc);
				}
			}
		}
	}

	private void loadAccounts()
	{
		if (!useSQL)
		{
			File file = new File(root, "accounts.yml");
			FileConfiguration datei = YamlConfiguration.loadConfiguration(file);

			if (!file.exists())
			{
				return;
			}

			ConfigurationSection cs = datei.getConfigurationSection("values");
			Map<String, Object> accMap = cs.getConfigurationSection("account")
					.getValues(false);
			Map<String, Object> cpMap = cs.getConfigurationSection("coinpurse")
					.getValues(false);

			for (String s : accMap.keySet())
			{
				UUID uuid = UUID.fromString(s);
				Account acc = new Account(this, uuid, (double) accMap.get(s),
						(double) cpMap.get(s), defaultLimit);
				accountList.put(uuid, acc);
			}
		} else
		{
			accountList = EBeanHandler.getEBH().getAccounts();
		}
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
		saveConfig();
		saveConfiguration();
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
		Account acc = accountList.get(player.getName());
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
