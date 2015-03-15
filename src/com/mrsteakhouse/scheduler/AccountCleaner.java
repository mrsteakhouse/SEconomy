package com.mrsteakhouse.scheduler;

import com.mrsteakhouse.SEconomy;

public class AccountCleaner implements Runnable
{
	private SEconomy plugin;

	public AccountCleaner(SEconomy plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void run()
	{
		plugin.cleanAccounts();
	}
}
