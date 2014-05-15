package com.goreacraft.plugins.borders;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ChunksListener implements Listener{
	HashMap<Integer,String> mess = new HashMap<Integer,String>();
	//int i = 0;
	@EventHandler
    public void onPlayerChangeChunk(PlayerMoveEvent event)
	
	{
		if(event.getPlayer().isOp() || event.getPlayer().hasPermission("gborder.all"))
		{
			return;
		}
		
		if(event.getFrom().distanceSquared(event.getTo())>Main.movementfinesse)
		if (!event.getFrom().getChunk().equals(event.getTo().getChunk())  || !Main.plugin.getConfig().getBoolean("Check players only on chunk change")) 
		{
			//i++;
			
			Player player = event.getPlayer();
			//player.sendMessage("I: " + i);
			if(Main.WorldsData.isConfigurationSection(player.getWorld().getName()))
			{
			//changedchunks
			//check if region
			List<String> regions = aplicableregions(event.getPlayer().getWorld().getName(), event.getTo().getX(),event.getTo().getZ());
			//player.sendMessage("Regions" + regions);
			//if (regions != null)
			{
				//player.sendMessage("regions in next: " + regions);
				boolean allowed=false;
				String bordername = "Global";
			//	if (!regions.isEmpty()) bordername=regions.iterator().next();
				try{
					
				for(String region: regions)
				{
					if(player.hasPermission("gborder."+ region))
					{
						allowed=true;						
						break;
					}else bordername=region;
				}
				} catch(NoSuchElementException e) {
					
				}
					if(!allowed || regions.isEmpty())
					{
						if(player.isInsideVehicle()) player.getVehicle().remove();
					
						player.teleport(event.getFrom());
						if(!mess.values().contains(player.getName()))
						{
							player.sendMessage(ChatColor.RED+ "[GoreaBorder]"+ ChatColor.YELLOW   + Main.plugin.getConfig().getString("Message " + bordername, "You dont have permissions to cross into region") +": "+ ChatColor.AQUA + bordername);
							if(Main.plugin.getConfig().getBoolean("Console output border crosing", true)) 
								{
									//System.out.println("[GoreaBorder] " + player.getName() + " is trying to cross border: " +region + " at: " + (int)player.getLocation().getX()+ " " + (int)player.getLocation().getY() + " " + (int)player.getLocation().getZ());
								Main.logger.info("[GoreaBorder] " + player.getName() + " is trying to cross border at: " + (int)player.getLocation().getX()+ " " + (int)player.getLocation().getY() + " " + (int)player.getLocation().getZ());
								}
						
							int taskid = new BukkitRunnable() 
							{
								@Override
										public void run() {
	            								mess.remove(getTaskId());
	            							}
							}.runTaskLater(Main.plugin, 100).getTaskId();
						mess.put(taskid, player.getName());
						} 
						
					}
				
			}
			
			// teleport player back player.teleport(event.getFrom());
			
			}
		}
        
       // event.getPlayer().sendMessage("D: " + event.getFrom().distanceSquared(event.getTo()));
	}

	List<String> isOutsideBorder(String world, double x, double z){
		List<String> regions = new ArrayList<String>();
		for ( String zone:Main.WorldsData.getConfigurationSection(world).getKeys(false))
		{
			double pos1x = Main.WorldsData.getDouble(world+"."+zone + ".pos1.X");
			double pos1z = Main.WorldsData.getDouble(world+"."+zone + ".pos1.Z");
			double pos2x = Main.WorldsData.getDouble(world+"."+zone + ".pos2.X");
			double pos2z = Main.WorldsData.getDouble(world+"."+zone + ".pos2.Z");
			double minx = 0;double maxx = 0;double minz = 0;double maxz = 0;
			
			
			if(pos1x>=pos2x)
			{
			minx=pos2x; 
			maxx=pos1x;				
			} else {
				minx=pos1x; 
				maxx=pos2x;
			}
			if(pos1z>=pos2z)
			{
				minz = pos2z;
				maxz = pos1z;
			} else {
				minz = pos1z;
				maxz = pos2z;
			}
			if(x<minx || x> maxx || z<minz||z>maxz)
			{
				regions.add(zone);
			}
			
		}
		
		
		
		return regions;
		
	}
	
	static List<String> aplicableregions(String world, double x, double z) {
		List<String> regions = new ArrayList<String>();
		if(Main.WorldsData.isConfigurationSection(world))
		{
			
		for ( String zone:Main.WorldsData.getConfigurationSection(world).getKeys(false))
		{
			double pos1x = Main.WorldsData.getInt(world+"."+zone + ".pos1.X");
			double pos1z = Main.WorldsData.getInt(world+"."+zone + ".pos1.Z");
			double pos2x = Main.WorldsData.getInt(world+"."+zone + ".pos2.X");
			double pos2z = Main.WorldsData.getInt(world+"."+zone + ".pos2.Z");
			//if(((pos1x<=x && x<=pos2x) && (pos1z<=z) && (z<=pos2z)) || ((pos1x>=x && x>=pos2x) && (pos1z>=z) && (z>=pos2z)))
			if((pos1x<=x && x<=pos2x) || (pos1x>=x && x>=pos2x) )
				if((pos1z<=z && z<=pos2z) || (pos1z>=z && z>=pos2z))
			//if (isBetween(pos1x,pos2x,x) && isBetween(pos1z,pos2z,z))
			{
				//findPlayerByString("gorea01").sendMessage("found :" + zone);
				regions.add(zone);
			}
			
			
		}
		
		
		}
		return regions;
	}

	
	
	@SuppressWarnings("unused")
	private Player findPlayerByString(String name) 
	{
		for ( Player player : Bukkit.getServer().getOnlinePlayers())
		{
			if(player.getName().equals(name)) 
			{
				return player;
			}
		}
		
		return null;
	}
	
	
	
}
