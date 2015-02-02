package com.mrsteakhouse.sqlbridge;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "seconomy_accounts")
public class EBeanAccount
{
	private volatile Integer id;
	private String user;
	private BigDecimal accountValue;
	private BigDecimal coinpurseValue;

	@Id
	@Column(length = 9)
	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	@Column(length = 42, nullable = false)
	public String getUser()
	{
		return user;
	}

	public void setUser(String user)
	{
		this.user = user;
	}

	@Column(precision = 33, scale = 4)
	public BigDecimal getAccountValue()
	{
		return accountValue;
	}

	public void setAccountValue(BigDecimal accountValue)
	{
		this.accountValue = accountValue;
	}

	@Column(precision = 33, scale = 4)
	public BigDecimal getCoinpurseValue()
	{
		return coinpurseValue;
	}

	public void setCoinpurseValue(BigDecimal coinpurseValue)
	{
		this.coinpurseValue = coinpurseValue;
	}

	public String toString()
	{
		return "EBeanAccount(" + user + ":" + accountValue + ","
				+ coinpurseValue + ")";
	}
}
