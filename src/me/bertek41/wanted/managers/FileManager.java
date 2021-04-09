package me.bertek41.wanted.managers;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.base.Charsets;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.gun.Gun;
import me.bertek41.wanted.gun.GunManager;
import me.bertek41.wanted.misc.Stats;

public class FileManager {
	private Wanted instance;
	private File arenasFile, gunsFile, langFile;
	private FileConfiguration arenas, guns, lang;
	
	public FileManager(Wanted instance) {
		this.instance = instance;
	}
	
	public void loadArenas() {
		if(!arenas.isSet("arenas") && !arenas.isSet("auto_join") && !arenas.isSet("spawn"))
			return;
		if(arenas.isSet("spawn")) {
			instance.getArenaManager().setSpawn(getLocationFromString(arenas.getString("spawn")));
		}
		if(arenas.isSet("arenas")) {
			for(String string : arenas.getConfigurationSection("arenas").getKeys(false)) {
				Arena arena = new Arena(arenas.getString("arenas." + string + ".name"));
				if(arenas.isSet("arenas." + string + ".material"))
					arena.setMaterial(Material.valueOf(arenas.getString("arenas." + string + ".material")));
				if(arenas.isSet("arenas." + string + ".amount"))
					arena.setAmount(arenas.getInt("arenas." + string + ".amount"));
				if(arenas.isSet("arenas." + string + ".durability"))
					arena.setDurability(Short.valueOf(String.valueOf(arenas.getInt("arenas." + string + ".durability"))));
				if(arenas.isSet("arenas." + string + ".lore"))
					arena.setLore(arenas.getStringList("arenas." + string + ".lore"));
				if(arenas.isSet("arenas." + string + ".pos1"))
					arena.setPos1(getLocationFromString(arenas.getString("arenas." + string + ".pos1")));
				if(arenas.isSet("arenas." + string + ".pos2"))
					arena.setPos2(getLocationFromString(arenas.getString("arenas." + string + ".pos2")));
				if(arenas.isSet("arenas." + string + ".lobby"))
					arena.setLobby(getLocationFromString(arenas.getString("arenas." + string + ".lobby")));
				if(arenas.isSet("arenas." + string + ".wanted_location"))
					arena.setWantedLocation(getLocationFromString(arenas.getString("arenas." + string + ".wanted_location")));
				if(arenas.isSet("arenas." + string + ".spectate_location"))
					arena.setSpectate(getLocationFromString(arenas.getString("arenas." + string + ".spectate_location")));
				if(arenas.isSet("arenas." + string + ".minimum_players"))
					arena.setRequiredPlayers(arenas.getInt("arenas." + string + ".minimum_players"));
				if(arenas.isSet("arenas." + string + ".maximum_players"))
					arena.setMaximumPlayers(arenas.getInt("arenas." + string + ".maximum_players"));
				if(arenas.isSet("arenas." + string + ".maximum_reward"))
					arena.setMaximumReward(arenas.getInt("arenas." + string + ".maximum_reward"));
				if(arenas.isSet("arenas." + string + ".signs")) {
					for(String location : arenas.getStringList("arenas." + string + ".signs")) {
						Location loc = getLocationFromString(location);
						if(loc.getBlock().getState() instanceof Sign) {
							arena.addSign((Sign) loc.getBlock().getState());
						}
					}
				}
				if(arenas.isSet("arenas." + string + ".locations")) {
					for(String location : arenas.getStringList("arenas." + string + ".locations")) {
						arena.addLocation(getLocationFromString(location));
					}
				}
				if(!arena.isCompleted()) {
					instance.getArenaManager().addArena(arena, false);
					Bukkit.getLogger().warning("[Wanted] Arena " + arena.getName() + " is not completed.");
				} else
					instance.getArenaManager().addArena(arena, true);
				if(arena.getSigns() != null && !arena.getSigns().isEmpty())
					arena.updateSign();
			}
		}
		if(arenas.isSet("auto_join")) {
			List<Sign> signs = new ArrayList<>();
			for(String location : arenas.getStringList("auto_join")) {
				Location loc = getLocationFromString(location);
				if(loc.getBlock().getState() instanceof Sign) {
					signs.add((Sign) loc.getBlock().getState());
				}
			}
			if(!signs.isEmpty())
				instance.getArenaManager().setSigns(signs);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void saveArenas() {
		if(instance.getArenaManager().getArenas().isEmpty() && instance.getArenaManager().getSigns().isEmpty())
			return;
		if(arenas.isSet("arenas"))
			arenas.set("arenas", null);
		if(arenas.isSet("auto_join"))
			arenas.set("auto_join", null);
		for(Arena arena : instance.getArenaManager().getArenas()) {
			if(arena.getItem() != null)
				arenas.set("arenas." + arena.getId() + ".material", arena.getItem().getType().toString());
			if(arena.getItem() != null)
				arenas.set("arenas." + arena.getId() + ".amount", arena.getItem().getAmount());
			if(arena.getItem() != null)
				arenas.set("arenas." + arena.getId() + ".durability", arena.getItem().getDurability());
			if(arena.getName() != null)
				arenas.set("arenas." + arena.getId() + ".name", arena.getName());
			if(arena.getLore() != null)
				arenas.set("arenas." + arena.getId() + ".lore", arena.getLore());
			if(arena.getPos1() != null)
				arenas.set("arenas." + arena.getId() + ".pos1", getStringFromLocation(arena.getPos1()));
			if(arena.getPos2() != null)
				arenas.set("arenas." + arena.getId() + ".pos2", getStringFromLocation(arena.getPos2()));
			if(arena.getLobby() != null)
				arenas.set("arenas." + arena.getId() + ".lobby", getStringFromLocation(arena.getLobby()));
			if(arena.getWantedLocation() != null)
				arenas.set("arenas." + arena.getId() + ".wanted_location", getStringFromLocation(arena.getWantedLocation()));
			if(arena.getSpectate() != null)
				arenas.set("arenas." + arena.getId() + ".spectate_location", getStringFromLocation(arena.getSpectate()));
			if(arena.getRequiredPlayers() != 0)
				arenas.set("arenas." + arena.getId() + ".minimum_players", arena.getRequiredPlayers());
			if(arena.getMaximumPlayers() != 0)
				arenas.set("arenas." + arena.getId() + ".maximum_players", arena.getMaximumPlayers());
			if(arena.getMaximumReward() != 0)
				arenas.set("arenas." + arena.getId() + ".maximum_reward", arena.getMaximumReward());
			if(arena.getSigns() != null && !arena.getSigns().isEmpty()) {
				List<String> signs = new ArrayList<>();
				arena.getSigns().forEach(sign -> signs.add(getStringFromLocation(sign.getLocation())));
				arenas.set("arenas." + arena.getId() + ".signs", signs);
			}
			if(!arena.getLocations().isEmpty()) {
				List<String> locations = new ArrayList<>();
				arena.getLocations().forEach(location -> locations.add(getStringFromLocation(location)));
				arenas.set("arenas." + arena.getId() + ".locations", locations);
			}
		}
		if(!instance.getArenaManager().getSigns().isEmpty()) {
			List<String> signs = new ArrayList<>();
			for(int i = 0; i < instance.getArenaManager().getSigns().size(); i++) {
				signs.add(getStringFromLocation(instance.getArenaManager().getSigns().get(i).getLocation()));
			}
			arenas.set("auto_join", signs);
		}
		if(instance.getArenaManager().getSpawn() != null) {
			arenas.set("spawn", getStringFromLocation(instance.getArenaManager().getSpawn()));
		}
		saveADatabase();
		HashMap<UUID, Stats> stats = instance.getStatsManager().getStats();
		for(UUID uuid : stats.keySet()) {
			instance.getDatabase().update(stats.get(uuid).saveQuery());
		}
		instance.getDatabase().close();
	}
	
	public void loadGuns() {
		if(!GunManager.getGuns().isEmpty())
			GunManager.getGuns().clear();
		if(guns.isSet("guns")) {
			for(String string : guns.getConfigurationSection("guns").getKeys(false)) {
				Gun gun = new Gun(Integer.valueOf(string));
				if(guns.isSet("guns." + string + ".default"))
					gun.setDefault(guns.getBoolean("guns." + string + ".default"));
				if(guns.isSet("guns." + string + ".material"))
					gun.setMaterial(Material.valueOf(guns.getString("guns." + string + ".material")));
				if(guns.isSet("guns." + string + ".coin"))
					gun.setCoin(guns.getInt("guns." + string + ".coin"));
				if(guns.isSet("guns." + string + ".distance"))
					gun.setDistance(guns.getDouble("guns." + string + ".distance"));
				if(guns.isSet("guns." + string + ".magazine"))
					gun.setMagazine(guns.getInt("guns." + string + ".magazine"));
				if(guns.isSet("guns." + string + ".damage"))
					gun.setDamage(guns.getDouble("guns." + string + ".damage"));
				if(guns.isSet("guns." + string + ".headshot_damage"))
					gun.setHeadshotDamage(guns.getDouble("guns." + string + ".headshot_damage"));
				if(guns.isSet("guns." + string + ".yaw_recoil"))
					gun.setYawRecoil((float) guns.getDouble("guns." + string + ".yaw_recoil"));
				if(guns.isSet("guns." + string + ".zoom_yaw_recoil"))
					gun.setYawRecoil((float) guns.getDouble("guns." + string + ".zoom_yaw_recoil"));
				if(guns.isSet("guns." + string + ".pitch_recoil"))
					gun.setPitchRecoil((float) guns.getDouble("guns." + string + ".pitch_recoil"));
				if(guns.isSet("guns." + string + ".zoom_pitch_recoil"))
					gun.setPitchRecoil((float) guns.getDouble("guns." + string + ".zoom_pitch_recoil"));
				if(guns.isSet("guns." + string + ".bullet_particle"))
					gun.setBulletParticle(guns.getString("guns." + string + ".bullet_particle"));
				if(guns.isSet("guns." + string + ".zoom"))
					gun.setHasZoom(guns.getBoolean("guns." + string + ".zoom"));
				if(guns.isSet("guns." + string + ".zoom_required"))
					gun.setZoomRequired(guns.getBoolean("guns." + string + ".zoom_required"));
				if(guns.isSet("guns." + string + ".name"))
					gun.setName(guns.getString("guns." + string + ".name"));
				if(guns.isSet("guns." + string + ".lore"))
					gun.setLore(guns.getStringList("guns." + string + ".lore"));
				if(guns.isSet("guns." + string + ".reload_time"))
					gun.setReloadTime(guns.getInt("guns." + string + ".reload_time"));
				if(guns.isSet("guns." + string + ".cooldown"))
					gun.setCooldown(guns.getLong("guns." + string + ".cooldown"));
				if(guns.isSet("guns." + string + ".sound")) {
					String[] split = guns.getString("guns." + string + ".sound").split(":");
					gun.setSound(split[0]);
					gun.setSoundVolume(Float.valueOf(split[1]));
					gun.setSoundPitch(Float.valueOf(split[2]));
				}
				if(guns.isSet("guns." + string + ".hit_sound")) {
					String[] split = guns.getString("guns." + string + ".hit_sound").split(":");
					gun.setHitSound(split[0]);
					gun.setHitSoundVolume(Float.valueOf(split[1]));
					gun.setHitSoundPitch(Float.valueOf(split[2]));
				}
				if(guns.isSet("guns." + string + ".headshot_hit_sound")) {
					String[] split = guns.getString("guns." + string + ".headshot_hit_sound").split(":");
					gun.setHeadshotHitSound(split[0]);
					gun.setHeadshotHitSoundVolume(Float.valueOf(split[1]));
					gun.setHeadshotHitSoundPitch(Float.valueOf(split[2]));
				}
				if(gun.isCompleted())
					GunManager.addGun(gun);
			}
		}
	}
	
	public String getStringFromLocation(final Location location) {
		String loc = new String();
		loc = location == null ? "" : location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ();
		loc = location.getYaw() != 0.0 ? loc + ":yaw=" + location.getYaw() : loc;
		loc = location.getPitch() != 0.0 ? loc + ":pitch=" + location.getPitch() : loc;
		return loc;
	}
	
	public Location getLocationFromString(final String location) {
		if(location == null || location.trim() == "")
			return null;
		final String[] split = location.split(":");
		if(split.length == 4) {
			final World world = Bukkit.getWorld(split[0]);
			final double x = Double.parseDouble(split[1]);
			final double y = Double.parseDouble(split[2]);
			final double z = Double.parseDouble(split[3]);
			return new Location(world, x, y, z);
		} else if(split.length > 4) {
			final World world = Bukkit.getWorld(split[0]);
			final double x = Double.parseDouble(split[1]);
			final double y = Double.parseDouble(split[2]);
			final double z = Double.parseDouble(split[3]);
			final float yaw = (float) (split[4].contains("yaw") ? Float.parseFloat(split[4].replace("yaw=", "")) : 0.0);
			final float pitch = (float) (split[5] != null && split[5].contains("pitch") ? Float.parseFloat(split[5].replace("pitch=", ""))
					: split[4].contains("pitch") ? Float.parseFloat(split[5].replace("pitch=", "")) : 0.0);
			return new Location(world, x, y, z, yaw, pitch);
		}
		return null;
	}
	
	public FileConfiguration getADatabase() {
		return arenas;
	}
	
	public FileConfiguration getGDatabase() {
		return guns;
	}
	
	public FileConfiguration getLang() {
		return lang;
	}
	
	public void saveADatabase() {
		try {
			if(!arenasFile.exists()) {
				arenasFile.getParentFile().mkdirs();
				instance.saveResource("arenas.yml", true);
				arenas.load(new File(instance.getDataFolder(), "arenas.yml"));
			} else {
				arenas.save(arenasFile);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveGuns() {
		try {
			if(!gunsFile.exists()) {
				gunsFile.getParentFile().mkdirs();
				instance.saveResource("guns.yml", true);
			}
			guns.load(new File(instance.getDataFolder(), "guns.yml"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveLang() {
		try {
			if(!langFile.exists()) {
				langFile.getParentFile().mkdirs();
				instance.saveResource("lang.yml", true);
			}
			lang.load(new File(instance.getDataFolder(), "lang.yml"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public FileConfiguration reload(FileConfiguration configuration, File file, String yml) {
		configuration = YamlConfiguration.loadConfiguration(file);
		final InputStream defStream = instance.getResource(yml);
		if(defStream == null) {
			return null;
		}
		configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defStream, Charsets.UTF_8)));
		return configuration;
	}
	
	public void createFiles() {
		File configFile = new File(instance.getDataFolder(), "config.yml");
		arenasFile = new File(instance.getDataFolder(), "arenas.yml");
		gunsFile = new File(instance.getDataFolder(), "guns.yml");
		langFile = new File(instance.getDataFolder(), "lang.yml");
		if(!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			instance.saveResource("config.yml", true);
		}
		if(!arenasFile.exists()) {
			arenasFile.getParentFile().mkdirs();
			instance.saveResource("arenas.yml", true);
		}
		arenas = new YamlConfiguration();
		if(!gunsFile.exists()) {
			gunsFile.getParentFile().mkdirs();
			instance.saveResource("guns.yml", true);
		}
		guns = new YamlConfiguration();
		if(!langFile.exists()) {
			langFile.getParentFile().mkdirs();
			instance.saveResource("lang.yml", true);
		}
		lang = new YamlConfiguration();
		try {
			arenas.load(arenasFile);
			guns.load(gunsFile);
			lang.load(langFile);
			YamlConfiguration configResource = YamlConfiguration.loadConfiguration(new InputStreamReader(instance.getResource("config.yml"), Charsets.UTF_8));
			double oldLangVersion = instance.getConfig().getDouble("lang_version", 0);
			double oldGunsVersion = instance.getConfig().getDouble("guns_version", 0);
			double oldArenasVersion = instance.getConfig().getDouble("arenas_version", 0);
			if(instance.getConfig().getDouble("version") != configResource.getDouble("version")) {
				configFile.renameTo(new File(instance.getDataFolder(), "config_old.yml"));
				instance.saveResource("config.yml", true);
				instance.reloadConfig();
				instance.getServer().getConsoleSender().sendMessage("[Wanted] §6Wanted §eConfig has been updated to version " + instance.getConfig().getDouble("version")
						+ " and made backup for old one. Please copy your old configuration to new one.");
			}
			if(oldLangVersion == 0 || instance.getConfig().getDouble("lang_version") != configResource.getDouble("lang_version")) {
				langFile.renameTo(new File(instance.getDataFolder(), "lang_old.yml"));
				instance.saveResource("lang.yml", true);
				lang = reload(lang, langFile, "lang.yml");
				instance.getServer().getConsoleSender().sendMessage("[Wanted] §6Wanted §eLang has been updated to version " + instance.getConfig().getDouble("lang_version")
						+ " and made backup for old one. Please copy your old messages to new one.");
			}
			if(oldGunsVersion == 0 || instance.getConfig().getDouble("guns_version") != configResource.getDouble("guns_version")) {
				gunsFile.renameTo(new File(instance.getDataFolder(), "guns_old.yml"));
				instance.saveResource("guns.yml", true);
				guns = reload(guns, gunsFile, "guns.yml");
				instance.getServer().getConsoleSender().sendMessage("[Wanted] §6Wanted §eGuns has been updated to version " + instance.getConfig().getDouble("guns_version")
						+ " and made backup for old one. Please copy your old messages to new one.");
			}
			if(oldArenasVersion == 0 || instance.getConfig().getDouble("arenas_version") != configResource.getDouble("arenas_version")) {
				arenasFile.renameTo(new File(instance.getDataFolder(), "arenas_old.yml"));
				instance.saveResource("arenas.yml", true);
				arenas = reload(arenas, arenasFile, "arenas.yml");
				instance.getServer().getConsoleSender().sendMessage("[Wanted] §6Wanted §eArenas has been updated to version " + instance.getConfig().getDouble("arenas_version")
						+ " and made backup for old one. Please copy your old messages to new one.");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
