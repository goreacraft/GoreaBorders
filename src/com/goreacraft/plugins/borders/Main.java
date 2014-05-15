package com.goreacraft.plugins.borders;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	
	public static Main plugin;
	public static final Logger logger = Logger.getLogger("minecraft");
	
	public static YamlConfiguration WorldsData=new YamlConfiguration();
	//public YamlConfiguration borderyml;
	//public File banlistfile;
	public static YamlConfiguration pos = new YamlConfiguration();
	//public static Set<String> placelist;
	//public static Set<String> itemuse;
	private List<String> aliases;
	//List<String> arguments;
	HashMap<String, Object> AAA = new HashMap<String, Object>();
	public static double movementfinesse;
	private static File debugFile;
	private static File worldsDataFile;
	public static double globalMove;
	//public static File bordersfolder;
	
	public void onEnable()
	
    {
		plugin = this;
		PluginDescriptionFile pdfFile = this.getDescription();
    	logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been enabled! " + pdfFile.getWebsite());
		getConfig().options().copyDefaults(true);
      	getConfig().options().header("If you need help with this plugin you can contact goreacraft on teamspeak ip: goreacraft.com\n Website http://www.goreacraft.com");
      	saveConfig();

      	Bukkit.getServer().getPluginManager().registerEvents(new ChunksListener(), this);
      	//bordersFile = new File(getDataFolder(), "Borders.yml");
      	debugFile = new File(getDataFolder(), "Debug.yml");
      	worldsDataFile = new File(getDataFolder(), "WorldsData.yml");
      	//bordersfolder = new File(getDataFolder() +  File.separator + "Borders");
      	//spawnPointsFile = new File(getDataFolder(), "SpawnPoints.yml");
      	loadconfigs();
      	//arguments = Arrays.asList("Min", "Max", "W","M","Item");
      	aliases = Bukkit.getPluginCommand("goreaborders").getAliases();
      	
      //====================================== METRICS STUFF =====================================================
      	 try {
      		    Metrics metrics = new Metrics(this);
      		    metrics.start();
      		} catch (IOException e) {
      		    // Failed to submit the stats :-(
      		}
      	 
      	if(getConfig().getBoolean("ChechUpdates"))
      	{
   		//new Updater(79646);
      	}
      	
    }
	void loadconfigs(){
		if (!worldsDataFile.exists())
 		{
 	            try {
 	            	worldsDataFile.createNewFile();
 	            } catch (IOException e) {
 	                e.printStackTrace();
 	            }
 	            
 	           globalMove= getConfig().getDouble("Allow move in global region");
 	           movementfinesse = getConfig().getDouble("Movement sensitiveness");
 	          //  checkFixPlayerData(name); 
 		}
		
		WorldsData=YamlConfiguration.loadConfiguration(worldsDataFile);
	//files to load
		
	}
	public void onDisable()
    {
		PluginDescriptionFile pdfFile = this.getDescription();
    	logger.info(pdfFile.getName() + " Version " + pdfFile.getVersion() + " has been disabled!" + pdfFile.getWebsite());
    	
    }
	
	
	static Player findPlayerByString(String name) 
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
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
		if (aliases.contains(label))
		{
			if (sender instanceof Player)
			{
				
				Player player = (Player) sender;
				if(args.length==1)
				{
					if(args[0].equals("help"))
					{
						showplayerhelp(player);					
					}					
				}
				
				if(!player.isOp()) 
				{
					
					player.sendMessage(ChatColor.RED + "You need to be Op to be able to use this commands.");
					return true;
				}
				if(args.length>0)
				{
					if(args[0].equalsIgnoreCase("pos1") || args[0].equalsIgnoreCase("pos2"))
					{
						if(args.length==1)
						{
							String name = player.getName();
							pos.createSection(name  + "."+ args[0]);							
							pos.set(name + "." + args[0] + ".X" , player.getLocation().getX());
							pos.set(name + "." + args[0] + ".Z" , player.getLocation().getZ());

						sender.sendMessage(args[0] + "Selected as " + player.getLocation().getX() + "," + player.getLocation().getZ());
						}
					}
					
					if(args[0].equalsIgnoreCase("create"))
					{
						if(args.length==2)
						{
							if(!pos.isConfigurationSection(player.getName()  + ".pos1"))
							{
								sender.sendMessage("You'r missing pos1");
								return true;
							}
							if(!pos.isConfigurationSection(player.getName()  + ".pos2"))
							{
								sender.sendMessage("You'r missing pos2");
								return true;
							}
	
							HashMap<String,Integer> aaa = new HashMap<String,Integer>();
							aaa.put("X", pos.getInt(player.getName() + ".pos1.X"));
							aaa.put("Z", pos.getInt(player.getName() + ".pos1.Z"));
							String world = ((Player) sender).getWorld().getName();
							WorldsData.createSection(world + "." + args[1]+ ".pos1");
							WorldsData.createSection(world + "." + args[1]+ ".pos2");
							WorldsData.set(world + "." + args[1] + ".pos1",aaa);
							aaa = new HashMap<String,Integer>();
							aaa.put("X", pos.getInt(player.getName() + ".pos2.X"));
							aaa.put("Z", pos.getInt(player.getName() + ".pos2.Z"));
							WorldsData.set(world + "." + args[1] + ".pos2",aaa);
							
							savetofile();
							sender.sendMessage("Saved position to file");
							WorldsData=YamlConfiguration.loadConfiguration(worldsDataFile);
						}
						if(args.length==3)
						{
							if(isInteger(args[2]))
							{
							HashMap<String,Integer> aaa = new HashMap<String,Integer>();
							aaa.put("X", ((int) player.getLocation().getX()) + Integer.parseInt(args[2]));
							aaa.put("Z", ((int) player.getLocation().getZ()) + Integer.parseInt(args[2]));
							String world = ((Player) sender).getWorld().getName();
							WorldsData.createSection(world + "." + args[1]+ ".pos1");
							WorldsData.createSection(world + "." + args[1]+ ".pos2");
							WorldsData.set(world + "." + args[1] + ".pos1",aaa);
							aaa = new HashMap<String,Integer>();
							aaa.put("X", ((int) player.getLocation().getX()) - Integer.parseInt(args[2]));
							aaa.put("Z", ((int) player.getLocation().getZ()) - Integer.parseInt(args[2]));
							WorldsData.set(world + "." + args[1] + ".pos2",aaa);
							player.sendMessage(ChatColor.YELLOW + "Border " + args[1] + " created!");
							savetofile();
							sender.sendMessage("Saved position to file");
							WorldsData=YamlConfiguration.loadConfiguration(worldsDataFile);
							
							} else { player.sendMessage("Usage" + ChatColor.GOLD+ " '/gborder create [name] [range]'"+ ChatColor.RESET + " - range has to be a number.");}
							return true;
						}
						if(args.length>3)
						{
							if(args.length==5)
							{
								if(isInteger(args[2]) && isInteger(args[3]) && isInteger(args[4]))
								{
									HashMap<String,Integer> aaa = new HashMap<String,Integer>();
									aaa.put("X", (Integer.parseInt(args[3]) + Integer.parseInt(args[2])));
									aaa.put("Z", (Integer.parseInt(args[4]) + Integer.parseInt(args[2])));
									String world = ((Player) sender).getWorld().getName();
									WorldsData.createSection(world + "." + args[1]+ ".pos1");
									WorldsData.createSection(world + "." + args[1]+ ".pos2");
									WorldsData.set(world + "." + args[1] + ".pos1",aaa);
									aaa = new HashMap<String,Integer>();
									aaa.put("X", (Integer.parseInt(args[3]) - Integer.parseInt(args[2])));
									aaa.put("Z", (Integer.parseInt(args[4]) - Integer.parseInt(args[2])));
									WorldsData.set(world + "." + args[1] + ".pos2",aaa);
									savetofile();
									sender.sendMessage("Saved position to file");
									WorldsData=YamlConfiguration.loadConfiguration(worldsDataFile);
									player.sendMessage(ChatColor.YELLOW + "Border " + args[1] + " created!");
									
								}else { player.sendMessage("Usage" + ChatColor.GOLD+ " '/gborder create [name] [range] <X> <Y>'"+ ChatColor.RESET + " - 'range', 'X' and 'Y' has to be a numbers.");}
							
							}else { player.sendMessage("Usage" + ChatColor.GOLD+ " '/gborder create [name] [range] <X> <Y>'"+ ChatColor.RESET + " - 'range', 'X' and 'Y' has to be a numbers.");}
						}
						
					} 
					if(args[0].equalsIgnoreCase("remove") && args.length>1)
					{
						if(args.length==2)
						{
							if(WorldsData.isConfigurationSection(player.getWorld().getName() + "."+ args[1]))
							{
								WorldsData.set(player.getWorld().getName() + "."+ args[1], null);
								savetofile();
								return true;
								
							} 
							else 
							{ 
								player.sendMessage("No region in this dimension with this name, try adding the world name too.");
							return false;	
							}
						}
						if(args.length>2)
						{
							if(WorldsData.isConfigurationSection(args[2] + "."+ args[1]))
							{
								WorldsData.set(args[2] + "."+ args[1], null);
								savetofile();
								return true;
								
							}
							else 
							{ player.sendMessage("No region in this dimension with this name.");
							return false;
							}
							
							
						}
						
					}
					if(args[0].equalsIgnoreCase("list") && args.length>0)
					{
						if(args.length==1)
						{							
							for(String world:WorldsData.getKeys(false))
							{
							player.sendMessage(ChatColor.YELLOW + world + ChatColor.RESET + ": " + WorldsData.getConfigurationSection(world).getKeys(false));
							}
							return true;
						}
						if(args.length==2)
						{
							if(args[1].equals("here"))
							{
								List<String> regions = ChunksListener.aplicableregions(player.getPlayer().getWorld().getName(), player.getLocation().getX(),player.getLocation().getZ());
								player.sendMessage(ChatColor.YELLOW + "Regions here" + ChatColor.RESET + ": " + regions);
								return true;
							}
							if(WorldsData.isConfigurationSection(args[1]))
							{
								player.sendMessage(args[1] + ": " + WorldsData.getConfigurationSection(args[1]).getKeys(false));
								return true;
							} 
							else {player.sendMessage("No regions in this dimension.");}							
						}
					}
					if(args[0].equalsIgnoreCase("reload"))
					{
						pos = new YamlConfiguration();
						loadconfigs();
						this.reloadConfig();
						sender.sendMessage(ChatColor.GREEN + "Plugin reloaded");
						return true;
					}
					if(args[0].equalsIgnoreCase("save"))
					{
						try {
							WorldsData.save(debugFile);
						} catch (IOException e) {
							e.printStackTrace();
						}
						sender.sendMessage("Saved WorldsData to file");
						return true;
					}
					if(args[0].equalsIgnoreCase("debug"))
					{
						
						try {
							pos.save(debugFile);
						} catch (IOException e) {
							
							e.printStackTrace();
							sender.sendMessage("Something got wrong while saving data to file Debug.yml");
						}
						sender.sendMessage("Position saved to file Debug.yml");
						return true;
					}
			}
			}
		}
		return false;
		
    }
	
	
	private void savetofile() {
		try {
			WorldsData.save(worldsDataFile);
		} catch (IOException e) {e.printStackTrace();}
		
		
	}

	private void showplayerhelp(Player player) {
		player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + " Plugin made by: "+ ChatColor.YELLOW + ".......................................................");
     	player.sendMessage( ChatColor.YELLOW + "     o   \\ o /  _ o              \\ /               o_   \\ o /   o");
     	player.sendMessage( ChatColor.YELLOW + "    /|\\     |      /\\   __o        |        o__    /\\      |     /|\\");
     	player.sendMessage( ChatColor.YELLOW + "    / \\   / \\    | \\  /) |       /o\\       |  (\\   / |    / \\   / \\");
     	player.sendMessage( ChatColor.YELLOW + "......................................................." + ChatColor.GOLD + ChatColor.BOLD + " GoreaCraft  "+ ChatColor.YELLOW + ".......................................................");
     	
     	player.sendMessage("");
     	player.sendMessage( ChatColor.YELLOW + "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );
     	player.sendMessage( ChatColor.YELLOW + "/gborder ?/help :" + ChatColor.RESET + " Shows this.");
     	player.sendMessage( ChatColor.YELLOW + "/gborder reload :" + ChatColor.RESET + " Reloads Configs.");
     	player.sendMessage( ChatColor.YELLOW + "/gborder list <world_name>" + ChatColor.RESET + " Lists all regions or in one specific world if world name provided." );
     	player.sendMessage( ChatColor.YELLOW + "/gborder list here" + ChatColor.RESET + " Lists all regions for your location." );
     	player.sendMessage( ChatColor.YELLOW + "/gborder 'pos1'/'pos2'" + ChatColor.RESET + " Will select your location as corners for the next region." );
     	player.sendMessage( ChatColor.YELLOW + "/gborder create <name> <range> <centerX> <centerZ>:" + ChatColor.RESET + " <> are optional, if you dont add them it will take the pos1/pos2" );
     	player.sendMessage( ChatColor.YELLOW + "/gborder remove [region_name]" + ChatColor.RESET + " Removes the region with that name from the world you are in." );

     	player.sendMessage( ChatColor.RED + "Give the permission 'gborder."+ ChatColor.ITALIC + "<region_name>" + ChatColor.YELLOW + "' To allow players to walk in regions");
		
	}
	@SuppressWarnings("unused")
	private void showplayerbanhelp(Player player) {
		player.sendMessage( ChatColor.YELLOW + "Aliases: " + ChatColor.LIGHT_PURPLE +  aliases );
		player.sendMessage( ChatColor.YELLOW + "/gr center" + ChatColor.RESET + " Will set the center of the world you are in at your location (work in progress)");
		player.sendMessage( ChatColor.RED + "/gr ban" + ChatColor.RESET + " Bans the item in your hand globally (all dimensions).");
		player.sendMessage( ChatColor.GREEN + "Available arguments:" + ChatColor.RESET + " Add the arguments for more advanced options.");
		player.sendMessage( ChatColor.RED + "ItemID:ItemDamageValue" + ChatColor.RESET + " To ban that specific item and not the one in yor hand");
		player.sendMessage("Use "+ ChatColor.RED +"*"+ ChatColor.RESET +" as ItemDamageValue to ban all item variations.");
		player.sendMessage( ChatColor.RED + "W:<dimension_name>" + ChatColor.RESET + " To ban the item in that specific dimension");
		player.sendMessage( ChatColor.RED + "Min:<range>" + ChatColor.RESET + " Players needs to be at that minimum range from the word center_point to be able to use it");
		player.sendMessage( ChatColor.RED + "Max:<range>" + ChatColor.RESET + " Players needs to be at that maximum range from the word center_point to be able to use it");
		player.sendMessage( ChatColor.ITALIC + "Example 1: " + ChatColor.GREEN + "/gr ban 391:* W:world This will make you sick if you eat it.");
		player.sendMessage( ChatColor.ITALIC + "Example 2: " + ChatColor.GREEN + "/gr ban 394:0 W:world_nether Min:100 Only awesome players can eat this here. Try after 100 blocks from spawn.");
		player.sendMessage( ChatColor.ITALIC + "Example 3: " + ChatColor.GREEN + "/gr ban 511:0 W:DIM7 Min:200 Max:300 Quarries can be used only between coordinates 200 and 300 in Twilight");
		player.sendMessage( ChatColor.ITALIC + "Example 4: " + ChatColor.GREEN + "/gr ban 25256:3 Min:500 Max:1000 This wand can be used in all worlds but between coordinates 500 and 1000");
	}
	
	public static boolean isInteger(String s) 
	{
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	
		
	/*static void loadWorldFile(String name)
	{
		YamlConfiguration worldData = new YamlConfiguration();	
		//checkplayerfile(name);		
		File playersDataFile= new File(Main.bordersfolder, name + ".yml");
		worldData= YamlConfiguration.loadConfiguration(playersDataFile);
		HashMap<String, Object> main = new HashMap<String, Object>();
		Set<String> aaa = worldData.getKeys(false);
		for (String aa: aaa)
		{			
			main.put(aa, worldData.get(aa));			
		}			
		WorldsData.createSection(name, main);		
		//return GoreaProtect.playersData;		
	}*/

}
