package me.bertek41.wanted.arenamanager;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.gun.Gun;
import me.bertek41.wanted.gun.GunManager;

public class ArenaPlayer {
	private OfflinePlayer player;
	private int coin, nextCoin, taskId = -1;
	private Gun gun, nextGun;
	private boolean freezed, isZoom, disconnect, invincible;
	private Set<Integer> guns = Sets.newHashSet();
	private long cooldown;
	private BossBar bossBar;
	
	public ArenaPlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	public ArenaPlayer(OfflinePlayer player, int coin, Gun gun) {
		this.player = player;
		this.coin = coin;
		this.gun = gun;
	}
	
	public OfflinePlayer getOfflinePlayer() {
		return player;
	}
	
	public Player getPlayer() {
		return player.getPlayer();
	}
	
	public void setPlayer(OfflinePlayer player) {
		this.player = player;
	}
	
	public int getCoin() {
		return coin;
	}
	
	public void setCoin(int coin) {
		this.coin = coin;
	}
	
	public void addCoin(int coin) {
		this.coin += coin;
	}
	
	public void removeCoin(int coin) {
		this.coin -= coin;
	}
	
	public int getNextCoin() {
		return nextCoin;
	}
	
	public void setNextCoin(int nextCoin) {
		this.nextCoin = nextCoin;
	}
	
	public Gun getGun() {
		if(gun == null)
			setGun(GunManager.getDefaultGun());
		return gun;
	}
	
	public void setGun(Gun gun) {
		this.gun = gun;
		guns.add(gun.getId());
	}
	
	public Gun getNextGun() {
		if(nextGun != null) {
			gun = nextGun.clone();
			if(!guns.contains(nextGun.getId())) {
				guns.add(nextGun.getId());
			}
			nextCoin = 0;
			nextGun = null;
		}
		return getGun();
	}
	
	public void setNextGun(Gun nextGun) {
		this.nextGun = nextGun;
		nextCoin = nextGun.getCoin();
	}
	
	public boolean isFreezed() {
		return freezed;
	}
	
	public void setFreezed(boolean freezed) {
		this.freezed = freezed;
	}
	
	public boolean isZoom() {
		return isZoom;
	}
	
	public void setZoom(boolean isZoom) {
		this.isZoom = isZoom;
		if(!player.isOnline())
			return;
		Player plyr = getPlayer();
		setFreezed(isZoom);
		if(isZoom) {
			plyr.getInventory().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
			plyr.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 250, false, false));
			plyr.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 30, false, false));
		} else {
			plyr.getInventory().setHelmet(null);
			plyr.removePotionEffect(PotionEffectType.JUMP);
			plyr.removePotionEffect(PotionEffectType.SLOW);
		}
	}
	
	public Set<Integer> getGuns() {
		return guns;
	}
	
	public void addGun(int gun) {
		guns.add(gun);
	}
	
	public boolean isDisconnect() {
		return disconnect;
	}
	
	public void setDisconnect(boolean disconnect) {
		this.disconnect = disconnect;
	}
	
	public boolean isInvincible() {
		return invincible;
	}
	
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	public long getCooldown() {
		return cooldown;
	}
	
	public void setCooldown(long cooldown) {
		this.cooldown = cooldown;
	}
	
	public BossBar getBossBar() {
		return bossBar;
	}
	
	public void setBossBar(BossBar bossBar) {
		this.bossBar = bossBar;
		if(taskId != -1)
			Bukkit.getScheduler().cancelTask(taskId);
		if(bossBar == null)
			return;
		taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Wanted.getInstance(), () -> {
			bossBar.setVisible(false);
		}, 40l);
	}
	
	public boolean canShoot() {
		return System.currentTimeMillis() - cooldown >= gun.getCooldown();
	}
	
}
