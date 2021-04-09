package me.bertek41.wanted.gun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GunManager {
	private static List<Gun> guns = new ArrayList<>();
	
	public static void addGun(Gun gun) {
		ItemStack item = gun.getItem();
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		item.setItemMeta(itemMeta);
		gun.setItem(item);
		if(!guns.contains(gun))
			guns.add(gun);
	}
	
	public static int getNewGunId() {
		int i = 0;
		if(guns.isEmpty())
			return i;
		for(Gun gun : guns) {
			if(gun.getId() >= i)
				i = gun.getId();
		}
		return i++;
	}
	
	public static void removeGun(Gun gun) {
		if(guns.contains(gun))
			guns.remove(gun);
	}
	
	public static List<Gun> getGuns() {
		return guns;
	}
	
	public static void setGuns(List<Gun> guns) {
		GunManager.guns = guns;
	}
	
	public static Gun getGun(int id) {
		if(guns.isEmpty())
			return null;
		for(Gun gun : guns)
			if(gun.getId() == id)
				return gun.clone();
		return null;
	}
	
	public static Gun getGun(String name) {
		if(guns.isEmpty())
			return null;
		for(Gun gun : guns)
			if(gun.getName().equals(name))
				return gun.clone();
		return null;
	}
	
	public static Gun getDefaultGun() {
		if(guns.isEmpty())
			return null;
		for(Gun gun : guns)
			if(gun.isDefault())
				return gun.clone();
		return null;
	}
	
}
