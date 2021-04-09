package me.bertek41.wanted.runnables;

import org.bukkit.scheduler.BukkitRunnable;

import me.bertek41.wanted.Wanted;
import me.bertek41.wanted.misc.HdHologram;
import me.bertek41.wanted.utils.Utils;

public class HologramUpdate extends BukkitRunnable {
	private Wanted instance;
	
	public HologramUpdate(Wanted instance) {
		this.instance = instance;
	}
	
	@Override
	public void run() {
		if(instance.getHologramManager().getHolograms().isEmpty()) {
			cancel();
			return;
		}
		for(HdHologram hdHologram : instance.getHologramManager().getHolograms()) {
			if(hdHologram.getStatType() == null)
				continue;
			Utils.setLines(instance, hdHologram, hdHologram.getStatType());
		}
	}
}
