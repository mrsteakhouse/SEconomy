package com.mrsteakhouse.sqlbridge;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.bukkit.Bukkit;
import org.bukkit.Location;

@Entity
@Table(name = "seconomy_bankblocks")
public class EBeanBankBlock
{
	private volatile Integer id;
	private volatile String world;
	private volatile Integer x;
	private volatile Integer y;
	private volatile Integer z;

	public EBeanBankBlock()
	{
	}

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

	public void setId(int id)
	{
		this.id = id;
	}

	@Column(length = 20, nullable = false)
	public String getWorld()
	{
		return world;
	}

	public void setWorld(String worldName)
	{
		this.world = worldName;
	}

	@Column(length = 5, nullable = false)
	public Integer getX()
	{
		return x;
	}

	public void setX(Integer x)
	{
		this.x = x;
	}

	@Column(length = 5, nullable = false)
	public Integer getY()
	{
		return y;
	}

	public void setY(Integer y)
	{
		this.y = y;
	}

	@Column(length = 5, nullable = false)
	public Integer getZ()
	{
		return z;
	}

	public void setZ(Integer z)
	{
		this.z = z;
	}

	public String toString()
	{
		return "EBeanBankBlock(" + world + ": " + x + "," + y + "," + z + ")";
	}

	@Transient
	private volatile Location location;

	public EBeanBankBlock(Location location)
	{
		setLocation(location);
	}

	@Transient
	public Location getLocation()
	{
		if (location == null)
			location = new Location(Bukkit.getWorld(getWorld()), getX(),
					getY(), getZ());
		return location;
	}

	@Transient
	public void setLocation(Location location)
	{
		this.location = location;
		setWorld(location.getWorld().getName());
		setX(location.getBlockX());
		setY(location.getBlockY());
		setZ(location.getBlockZ());
	}
}
