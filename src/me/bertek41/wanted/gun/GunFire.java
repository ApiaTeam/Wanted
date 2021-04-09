package me.bertek41.wanted.gun;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.api.events.GunFireEvent;
import me.bertek41.wanted.api.events.GunHitEvent;
import me.bertek41.wanted.arenamanager.Arena;
import me.bertek41.wanted.arenamanager.ArenaPlayer;
import me.bertek41.wanted.misc.Lang;
import me.bertek41.wanted.misc.RayTrace;
import me.bertek41.wanted.misc.Settings;
import me.bertek41.wanted.misc.StatType;
import me.bertek41.wanted.utils.ReflectionUtils;
import me.bertek41.wanted.utils.Utils;
import net.minecraft.server.v1_16_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_16_R3.PacketPlayOutPosition;
import net.minecraft.server.v1_16_R3.PacketPlayOutPosition.EnumPlayerTeleportFlags;

public class GunFire {
	private static final Set<EnumPlayerTeleportFlags> FLAGS = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList(PacketPlayOutPosition.EnumPlayerTeleportFlags.X, PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT,
					PacketPlayOutPosition.EnumPlayerTeleportFlags.Y, PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT, PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)));
	
	public synchronized static void fire(Arena arena, ArenaPlayer arenaPlayer, Player player, Gun gun, ItemStack item, boolean zoomed) {
		if(gun.hasCooldown())
			arenaPlayer.setCooldown(System.currentTimeMillis());
		float yaw = gun.getYawRecoil(), pitch = gun.getPitchRecoil();
		GunFireEvent gunFireEvent = new GunFireEvent(player, gun, yaw, pitch);
		Bukkit.getPluginManager().callEvent(gunFireEvent);
		if(gunFireEvent.isCancelled())
			return;
		gun = gunFireEvent.getGun();
		yaw = gunFireEvent.getYaw();
		pitch = gunFireEvent.getPitch();
		gun.setCurrentBullet(player, item, gun.getCurrentBullet() - 1);
		if(gun.hasCooldown() && !gun.isReloading()) {
			new BukkitRunnable() {
				private long cooldown = gunFireEvent.getGun().getCooldown();
				
				@Override
				public void run() {
					if(cooldown < 1) {
						player.setExp(0);
						player.setLevel(0);
						cancel();
						return;
					}
					player.setExp((cooldown % 1000) / 1000f);
					player.setLevel((int) (cooldown > 999 ? (cooldown + 1000 - 1) / 1000 : 1));
					cooldown -= 50;
				}
			}.runTaskTimerAsynchronously(Wanted.getInstance(), 0, 1);
		}
		if(gun.getCurrentBullet() < 1)
			arenaPlayer.setCooldown(0);
		fire(arena, arenaPlayer, player, gun, yaw, pitch, zoomed);
	}
	
	private synchronized static void fire(Arena arena, ArenaPlayer arenaPlayer, Player player, Gun gun, float yaw, float pitch, boolean zoomed) {
		Wanted.getInstance().getStatsManager().addStats(player, StatType.SHOTS, 1);
		sendRecoilPacket(player, zoomed ? gun.getZoomYawRecoil() : yaw, zoomed ? gun.getZoomPitchRecoil() : pitch);
		if(gun.hasSound())
			arena.getOnlinePlayers().forEach(all -> {
				if(gun.getSound() instanceof Sound)
					all.playSound(player.getLocation(), (Sound) gun.getSound(), gun.getSoundVolume(), gun.getSoundPitch());
				else
					all.playSound(player.getLocation(), (String) gun.getSound(), gun.getSoundVolume(), gun.getSoundPitch());
			});
		Location location = player.getEyeLocation().clone().add(0, player.isSneaking() ? Settings.BULLET_HEIGHT_WHILE_SNEAKING.getInt() : Settings.BULLET_HEIGHT.getInt(), 0);
		Vector direction = player.getEyeLocation().getDirection();
		RayTrace rayTrace = new RayTrace(location.toVector(), direction);
		ArrayList<Vector> positions = rayTrace.traverse(gun.getDistance(), 0.25, player.getWorld());
		Set<Player> players = new HashSet<>();
		if(arena.isWanted(player)) {
			players = arena.getOnlinePlayers();
			players.remove(player);
		} else
			players.add(arena.getWanted());
		loop: for(Vector vector : positions) {
			Location particle = vector.toLocation(player.getWorld());
			location.getWorld().spawnParticle(gun.getBulletParticle(), particle, 1, 0.0F, 0.0F, 0.0F, 0);
			for(Player all : players) {
				if(!player.hasLineOfSight(all) || arena.isInvincible(all))
					continue;
				if(RayTrace.intersects(vector, all.getBoundingBox().getMin(), all.getBoundingBox().getMax())) {
					boolean isHeadshot = isHeadShot(all, vector);
					Object sound;
					float volume;
					float soundPitch;
					if(isHeadshot) {
						Wanted.getInstance().getStatsManager().addStats(player, StatType.HEADSHOTS, 1);
						sound = ReflectionUtils.getSound(gun.getHeadshotHitSound());
						volume = gun.getHeadshotHitSoundVolume();
						soundPitch = gun.getHeadshotHitSoundPitch();
					} else {
						sound = ReflectionUtils.getSound(gun.getHitSound());
						volume = gun.getHitSoundVolume();
						soundPitch = gun.getHitSoundPitch();
					}
					GunHitEvent gunHitEvent = new GunHitEvent(player, all, DamageCause.CUSTOM, isHeadshot ? gun.getHeadshotDamage() : gun.getDamage(), gun, isHeadshot);
					Bukkit.getPluginManager().callEvent(gunHitEvent);
					if(gunHitEvent.isCancelled())
						break loop;
					all.setLastDamageCause(gunHitEvent);
					all.setHealth(all.getHealth() - gunHitEvent.getDamage() <= 0 ? 0 : all.getHealth() - gunHitEvent.getDamage());
					PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus(((CraftPlayer) all).getHandle(), (byte) 2);
					PacketPlayOutAnimation packet1 = new PacketPlayOutAnimation(((CraftPlayer) all).getHandle(), 1);
					arena.getOnlinePlayers().forEach(p -> {
						ReflectionUtils.sendPacket(p, packet);
						ReflectionUtils.sendPacket(p, packet1);
					});
					if(sound != null) {
						if(sound instanceof Sound)
							player.playSound(player.getLocation(), (Sound) sound, volume, soundPitch);
						else
							player.playSound(player.getLocation(), (String) sound, volume, soundPitch);
					}
					Wanted.getInstance().getStatsManager().addStats(player, StatType.SHOTS_ON_TARGET, 1);
					BossBar bossBar = arenaPlayer.getBossBar() == null ? Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SEGMENTED_20) : arenaPlayer.getBossBar();
					arenaPlayer.setBossBar(bossBar);
					bossBar.setTitle(all.getName());
					bossBar.setProgress(all.getHealth() / 20.0f);
					bossBar.addPlayer(player);
					bossBar.setVisible(true);
					Utils.sendActionBar(player,
							Utils.convertUnicode(isHeadshot ? Lang.GUN_HEADSHOT_ACTIONBAR.getString() : Lang.GUN_HIT_ACTIONBAR.getString()).replace("<bullets>", gun.getCurrentBullet() + "")
									.replace("<magazine>", gun.getMagazine() + "").replace("<victimhealth>", (Math.round(all.getHealth() * 100.0) / 100.0) + "")
									.replace("<coin>", arena.getArenaPlayer(player).getCoin() + ""));
					return;
				}
			}
		}
		Utils.sendActionBar(player, Utils.convertUnicode(Lang.GUN_SHOT_ACTIONBAR.getString()).replace("<bullets>", gun.getCurrentBullet() + "").replace("<magazine>", gun.getMagazine() + "")
				.replace("<coin>", arena.getArenaPlayer(player).getCoin() + ""));
	}
	
	private static boolean isHeadShot(Player player, Vector position) {
		if(player == null || position == null)
			return false;
		BoundingBox playerBox = player.getBoundingBox();
		BoundingBox box = new BoundingBox(playerBox.getMinX(), playerBox.getMinY() + (player.isSneaking() ? Settings.HEAD_HEIGHT_WHILE_SNEAKING.getDouble() : Settings.HEAD_HEIGHT.getDouble()),
				playerBox.getMinZ(), playerBox.getMaxX(), playerBox.getMaxY(), playerBox.getMaxZ());
		return box != null && box.contains(position.getX(), position.getY(), position.getZ());
	}
	
	private static void sendRecoilPacket(Player player, float yaw, float pitch) {
		PacketPlayOutPosition packet = new PacketPlayOutPosition(0.0, 0.0, 0.0, yaw, pitch, FLAGS, 0);
		ReflectionUtils.sendPacket(player, packet);
	}
	
}
