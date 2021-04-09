package me.bertek41.wanted.gun;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.utils.ReflectionUtils;
import me.bertek41.wanted.utils.Utils;

public class Gun {
	private int id, coin, magazine, bullets, reloadTime;
	private boolean defaultGun, reloading, hasZoom, zoomRequired;
	private String name = "";
	private ItemStack item = new ItemStack(Material.AIR);
	private double distance, damage, headshotDamage;
	private Particle bulletParticle;
	private long cooldown = 0L;
	private float yawRecoil, pitchRecoil, zoomYawRecoil, zoomPitchRecoil, soundVolume, soundPitch, hitSoundVolume, hitSoundPitch, headshotHitSoundVolume, headshotHitSoundPitch;
	private Object sound, hitSound, headshotHitSound;
	
	public Gun(int id) {
		this.id = id;
	}
	
	public Gun(String name) {
		this.name = name;
	}
	
	public Gun(int id, boolean defaultGun, String name, List<String> lore, Material material, int coin, int magazine, int reloadTime, double distance, double damage, double headshotDamage,
			float yawRecoil, float pitchRecoil, float zoomYawRecoil, float zoomPitchRecoil, float soundVolume, float soundPitch, float hitSoundVolume, float hitSoundPitch,
			float headshotHitSoundVolume, float headshotHitSoundPitch, Particle bulletParticle, boolean hasZoom, boolean zoomRequired, long cooldown, Object sound, Object hitSound,
			Object headshotHitSound) {
		this.id = id;
		this.defaultGun = defaultGun;
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.item = new ItemStack(material);
		ItemMeta meta = this.item.getItemMeta();
		meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList()));
		this.item.setItemMeta(meta);
		this.coin = coin;
		this.distance = distance;
		this.magazine = magazine;
		this.reloadTime = reloadTime;
		this.damage = damage;
		this.headshotDamage = headshotDamage;
		this.yawRecoil = yawRecoil;
		this.pitchRecoil = pitchRecoil;
		this.zoomYawRecoil = zoomYawRecoil;
		this.zoomPitchRecoil = zoomPitchRecoil;
		this.bulletParticle = bulletParticle;
		this.hasZoom = hasZoom;
		this.zoomRequired = zoomRequired;
		this.cooldown = cooldown;
		this.sound = ReflectionUtils.getSound(sound);
		this.soundVolume = soundVolume;
		this.soundPitch = soundPitch;
		this.hitSound = ReflectionUtils.getSound(hitSound);
		this.hitSoundVolume = hitSoundVolume;
		this.hitSoundPitch = hitSoundPitch;
		this.headshotHitSound = ReflectionUtils.getSound(headshotHitSound);
		this.headshotHitSoundVolume = headshotHitSoundVolume;
		this.headshotHitSoundPitch = headshotHitSoundPitch;
		setName(name);
		bullets = magazine;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public boolean isDefault() {
		return defaultGun;
	}
	
	public void setDefault(boolean defaultGun) {
		this.defaultGun = defaultGun;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		if(item.getType() != Material.AIR) {
			ItemMeta meta = this.item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			this.item.setItemMeta(meta);
		}
	}
	
	public List<String> getLore() {
		return item.getItemMeta().hasLore() ? item.getItemMeta().getLore() : null;
	}
	
	public void setLore(List<String> lore) {
		ItemMeta meta = this.item.getItemMeta();
		meta.setLore(lore.stream().map(l -> ChatColor.translateAlternateColorCodes('&', l)).collect(Collectors.toList()));
		this.item.setItemMeta(meta);
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public void setItem(ItemStack item) {
		this.item = item;
	}
	
	public void setMaterial(Material material) {
		this.item.setType(material);
		if(!this.name.isEmpty()) {
			ItemMeta meta = this.item.getItemMeta();
			meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
			this.item.setItemMeta(meta);
		}
	}
	
	public boolean hasCoin() {
		return !defaultGun && coin != 0;
	}
	
	public int getCoin() {
		return coin;
	}
	
	public void setCoin(int coin) {
		this.coin = coin;
	}
	
	public double getDistance() {
		return distance;
	}
	
	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	public int getMagazine() {
		return magazine;
	}
	
	public void setMagazine(int magazine) {
		this.magazine = magazine;
		bullets = magazine;
	}
	
	public double getDamage() {
		return damage;
	}
	
	public int getReloadTime() {
		return reloadTime;
	}
	
	public void setReloadTime(int reloadTime) {
		this.reloadTime = reloadTime;
	}
	
	public void setDamage(double damage) {
		this.damage = damage;
	}
	
	public double getHeadshotDamage() {
		return headshotDamage;
	}
	
	public void setHeadshotDamage(double headshotDamage) {
		this.headshotDamage = headshotDamage;
	}
	
	public boolean isReloading() {
		return reloading;
	}
	
	public float getYawRecoil() {
		return yawRecoil;
	}
	
	public void setYawRecoil(float yawRecoil) {
		this.yawRecoil = yawRecoil;
	}
	
	public float getPitchRecoil() {
		return pitchRecoil;
	}
	
	public void setPitchRecoil(float pitchRecoil) {
		this.pitchRecoil = pitchRecoil;
	}
	
	public float getZoomYawRecoil() {
		return zoomYawRecoil;
	}
	
	public void setZoomYawRecoil(float zoomYawRecoil) {
		this.zoomYawRecoil = zoomYawRecoil;
	}
	
	public float getZoomPitchRecoil() {
		return zoomPitchRecoil;
	}
	
	public void setZoomPitchRecoil(float zoomPitchRecoil) {
		this.zoomPitchRecoil = zoomPitchRecoil;
	}
	
	public Particle getBulletParticle() {
		return bulletParticle;
	}
	
	public void setBulletParticle(Particle bulletParticle) {
		this.bulletParticle = bulletParticle;
	}
	
	public void setBulletParticle(String bulletParticle) {
		this.bulletParticle = Particle.valueOf(bulletParticle);
	}
	
	public boolean isHasZoom() {
		return hasZoom;
	}
	
	public void setHasZoom(boolean hasZoom) {
		this.hasZoom = hasZoom;
	}
	
	public boolean isZoomRequired() {
		return zoomRequired;
	}
	
	public void setZoomRequired(boolean zoomRequired) {
		this.zoomRequired = zoomRequired;
	}
	
	public boolean hasCooldown() {
		return cooldown != 0L;
	}
	
	public long getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}
	
	public boolean hasSound() {
		return sound != null;
	}
	
	public Object getSound() {
		return sound;
	}
	
	public void setSound(Object sound) {
		this.sound = ReflectionUtils.getSound(sound);
	}
	
	public float getSoundVolume() {
		return soundVolume;
	}
	
	public void setSoundVolume(float soundVolume) {
		this.soundVolume = soundVolume;
	}
	
	public float getSoundPitch() {
		return soundPitch;
	}
	
	public void setSoundPitch(float soundPitch) {
		this.soundPitch = soundPitch;
	}
	
	public boolean hasHitSound() {
		return hitSound != null;
	}
	
	public Object getHitSound() {
		return hitSound;
	}
	
	public void setHitSound(Object hitSound) {
		this.hitSound = ReflectionUtils.getSound(hitSound);
	}
	
	public float getHitSoundVolume() {
		return hitSoundVolume;
	}
	
	public void setHitSoundVolume(float hitSoundVolume) {
		this.hitSoundVolume = hitSoundVolume;
	}
	
	public float getHitSoundPitch() {
		return hitSoundPitch;
	}
	
	public void setHitSoundPitch(float hitSoundPitch) {
		this.hitSoundPitch = hitSoundPitch;
	}
	
	public boolean hasHeadshotHitSound() {
		return headshotHitSound != null;
	}
	
	public Object getHeadshotHitSound() {
		return headshotHitSound;
	}
	
	public void setHeadshotHitSound(Object headshotHitSound) {
		this.headshotHitSound = ReflectionUtils.getSound(headshotHitSound);
	}
	
	public float getHeadshotHitSoundVolume() {
		return headshotHitSoundVolume;
	}
	
	public void setHeadshotHitSoundVolume(float headshotHitSoundVolume) {
		this.headshotHitSoundVolume = headshotHitSoundVolume;
	}
	
	public float getHeadshotHitSoundPitch() {
		return headshotHitSoundPitch;
	}
	
	public void setHeadshotHitSoundPitch(float headshotHitSoundPitch) {
		this.headshotHitSoundPitch = headshotHitSoundPitch;
	}
	
	public short getBoost() {
		return (short) (Math.round((float) item.getType().getMaxDurability() / (float) magazine));
	}
	
	public int getCurrentBullet() {
		return bullets;
	}
	
	public void setCurrentBullet(int bullets) {
		this.bullets = bullets;
	}
	
	public void setCurrentBullet(Player player, ItemStack itemInHand, int bullet) {
		if(bullet <= 0) {
			reload(player, itemInHand);
			return;
		}
		ItemMeta itemMeta = itemInHand.getItemMeta();
		((Damageable) itemMeta).setDamage((short) (bullet == 1 ? itemInHand.getType().getMaxDurability() - 1 : itemInHand.getType().getMaxDurability() - bullet * getBoost()));
		itemInHand.setItemMeta(itemMeta);
		bullets = bullet;
	}
	
	public void reload(Player player, ItemStack itemInHand) {
		this.reloading = true;
		if(!(itemInHand.getItemMeta() instanceof Damageable))
			return;
		ItemMeta itemMeta = itemInHand.getItemMeta();
		int[] fraction = asFraction(reloadTime, magazine);
		int bulletToPut = fraction[0];
		int tickToPut = fraction[1];
		new BukkitRunnable() {
			private double current = 1.0;
			private int tick = 0;
			
			@Override
			public void run() {
				if(tick != 0 && tick % tickToPut == 0) {
					if(tick >= reloadTime)
						current = magazine;
					double percentage = current / magazine;
					StringBuilder sb = new StringBuilder();
					for(int l = 0; l < magazine; l++) {
						if(l < magazine * percentage)
							sb.append("§e█");
						else
							sb.append("§f█");
					}
					Utils.sendActionBar(player, "§8[§r" + sb.toString() + "§8]");
					((Damageable) itemMeta).setDamage((short) (current == 1.0 ? item.getType().getMaxDurability() - 1 : item.getType().getMaxDurability() - current * getBoost()));
					itemInHand.setItemMeta(itemMeta);
					if(current >= magazine) {
						cancel();
						((Damageable) itemMeta).setDamage((short) 0);
						itemInHand.setItemMeta(itemMeta);
						bullets = magazine;
						reloading = false;
					} else
						current += bulletToPut;
				}
				tick++;
			}
		}.runTaskTimer(Wanted.getInstance(), 0, 1);
	}
	
	private int gcm(int a, int b) {
		return b == 0 ? a : gcm(b, a % b);
	}
	
	private int[] asFraction(int a, int b) {
		int gcm = gcm(a, b);
		return new int[] { (a / gcm), (b / gcm) };
	}
	
	public boolean isCompleted() {
		return (!name.isEmpty() && item.getType() != Material.AIR && distance != 0.0 && magazine != 0 && damage != 0.0 && headshotDamage != 0.0 && yawRecoil != 0.0 && pitchRecoil != 0.0
				&& bulletParticle != null);
	}
	
	@Override
	public Gun clone() {
		return new Gun(id, defaultGun, name, getLore(), item.getType(), coin, magazine, reloadTime, distance, damage, headshotDamage, yawRecoil, pitchRecoil, zoomYawRecoil, zoomPitchRecoil,
				soundVolume, soundPitch, hitSoundVolume, hitSoundPitch, headshotHitSoundVolume, headshotHitSoundPitch, bulletParticle, hasZoom, zoomRequired, cooldown, sound, hitSound,
				headshotHitSound);
	}
	
}