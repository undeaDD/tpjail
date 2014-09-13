package tpjail;

import java.io.File;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author DieFriiks / CustomCraftDev / undeaD_D
 * @category PerPlayerCommands plugin
 * @version 1.0
 */
public class TpJail extends JavaPlugin implements Listener{
	
    FileConfiguration config;
    boolean debug;
    boolean isplayer;
    
    Location jail;
	String nopermission_msg;

	
	/**
     * on Plugin enable
     */
	public void onEnable() {
		loadConfig();
    	say("Config loaded");   	
    	
    	this.getServer().getPluginManager().registerEvents(this, this);
    	say("Events registered");
	}

	
	/**
     * on Plugin disable
     */
	public void onDisable() {
		// nothing to save here :(
	}

	/**
     * on Command
     * @param sender - command sender
     * @param cmd - command
     * @param alias
     * @return true or false
     */
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		isplayer = false;
		Player p = null;
		
		if ((sender instanceof Player)) {
			p = (Player)sender;
			isplayer = true;
		}
			if(cmd.getName().equalsIgnoreCase("tpjail") && args.length != 0){
				
				// set
				if(args[0].equalsIgnoreCase("set")){
					if(isplayer){
						if(p.hasPermission("tpjail.set")){
								jail = p.getLocation();
									save();
								p.sendMessage(ChatColor.RED + "[TpJail] Jail Location set and saved to config");
								say("new Jail location set by " + p.getName());
							return true;
						}
						else{
							p.sendMessage(ChatColor.RED + nopermission_msg);
							return true;
						}
					}
					else{
						System.out.println("[TpJail] SET is only ingame available ...");
						return true;
					}
				}
				
				// disable
				if(args[0].equalsIgnoreCase("disable")){
					if(isplayer){
						if(p.hasPermission("tpjail.disable")){
								this.setEnabled(false);
								p.sendMessage(ChatColor.RED + "[TpJail] was disabled");
								say("disabled by " + p.getName());
							return true;
						}
						else{
							p.sendMessage(ChatColor.RED + nopermission_msg);
							return true;
						}
					}
					else{
							this.setEnabled(false);
						System.out.println("[TpJail] was disabled");
						return true;
					}
				}
				
				// reset
				if(args[0].equalsIgnoreCase("reset")){
					if(isplayer){
						if(p.hasPermission("tpjail.reset")){
							    File configFile = new File(getDataFolder(), "config.yml");
							    configFile.delete();
							    saveDefaultConfig();
								p.sendMessage(ChatColor.RED + "[TpJail] config reset");
							    reload();
								p.sendMessage(ChatColor.RED + "[TpJail] was reloaded");
								say("reset by " + p.getName());
							return true;
						}
						else{
							p.sendMessage(ChatColor.RED + nopermission_msg);
							
							return true;
						}
					}
					else{
						    File configFile = new File(getDataFolder(), "config.yml");
						    configFile.delete();
						    saveDefaultConfig();
						System.out.println("[TpJail] config reset");
						    reload();
					    System.out.println("[TpJail] was reloaded");
						return true;
					}
				}
				
				// reload
				if(args[0].equalsIgnoreCase("reload")){
					if(isplayer){
						if(p.hasPermission("tpjail.reload")){
								reload();
								p.sendMessage(ChatColor.RED + "[TpJail] was reloaded");
								say("reloaded by " + p.getName());
							return true;
						}
						else{
							p.sendMessage(ChatColor.RED + nopermission_msg);
							return true;
						}
					}
					else{
						    reload();
					    System.out.println("[TpJail] was reloaded");
						return true;
				    }
				}
			}
		
		// nothing to do here \o/
		return false;
	}
	
    
	/**
     *  saving new location
     */
	public void save(){
		config.set("jail-location.worldname",jail.getWorld().getName());
		config.set("jail-location.x",jail.getX());
		config.set("jail-location.y",jail.getY());
		config.set("jail-location.z",jail.getZ());
		config.set("jail-location.pitch",jail.getPitch());
		config.set("jail-location.yaw",jail.getYaw());
			saveConfig();
			reloadConfig();
	}
	
    
	/**
     *  join event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAction(PlayerJoinEvent e) {
    	Player p = e.getPlayer();
    	if(p.hasPermission("tpjail.active")){
    		p.teleport(jail);
    	}
    }
	
    
	/**
     *  respawn event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAction(PlayerRespawnEvent e) {
    	Player p = e.getPlayer();
		   if(p.hasPermission("tpjail.active")){
			   e.setRespawnLocation(jail);
		   }
    }
	
	
	/**
     * load config settings
     */
	private void loadConfig() {
		
		config = getConfig();
		config.options().copyDefaults(true);
		saveConfig();
		
		debug = config.getBoolean("debug");
		nopermission_msg = config.getString("nopermission-msg");
		jail = new Location(this.getServer().getWorld(config.getString("jail-location.worldname")), config.getDouble("jail-location.x"), config.getDouble("jail-location.y"), config.getDouble("jail-location.z"), (float) config.getDouble("jail-location.pitch"), (float) config.getDouble("jail-location.yaw"));
		
	}
	   
    
    /**
     * reload
     */
    private void reload(){
 	   	try {
 	   		// Remove unused variables and objects
			    config = null;
			    jail = null;
			    nopermission_msg = null;
			    
			// Run java garbage collector to delete unused things
			    System.gc();
			
			// load everything again
				reloadConfig();
				loadConfig();
			
 	   	} catch (Exception e) {
        	if(debug){
        		e.printStackTrace();
        	}
        }
    }
    
    
    /**
     * print to console
     * @param message to print
     */
	public void say(String out) {
		if(debug){
			System.out.println("[TpJail] " + out);
		}
	}	
}
