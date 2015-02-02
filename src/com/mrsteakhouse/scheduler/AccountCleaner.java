package com.mrsteakhouse.scheduler;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.mrsteakhouse.SEconomy;

public class AccountCleaner implements Runnable
{
	private SEconomy plugin;
	private Calendar lastCheck;
	private Calendar nextClean;

	public AccountCleaner(SEconomy plugin, Calendar nextClean)
	{
		this.plugin = plugin;
		this.nextClean = nextClean;
	}

	@Override
	public void run()
	{
		this.lastCheck = new GregorianCalendar();
		long time = nextClean.getTime().getTime() - lastCheck.getTime().getTime();
		long days = Math.round( (double)time / (24. * 60.*60.*1000.) );
		if (days >= 14 || days < 0)
		{
			plugin.cleanAccounts();
			nextClean.add(Calendar.DAY_OF_MONTH, 14);
			
			plugin.setNextClean(nextClean);
		}
	}
}
