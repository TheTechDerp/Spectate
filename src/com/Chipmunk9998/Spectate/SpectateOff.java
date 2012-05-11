package com.Chipmunk9998.Spectate;

import org.bukkit.entity.Player;

import java.util.HashSet;

public class SpectateOff {
	
	public Spectate plugin;
	
	public SpectateOff(Spectate plugin) {
		this.plugin = plugin;
	}
	
	public void spectateOff(final Player player) {
		player.teleport(plugin.CommandExecutor.origLocation.get(player));
		plugin.CommandExecutor.isSpectating.put(player, false);
		player.getInventory().clear();
		player.getInventory().setContents(plugin.CommandExecutor.senderInv.get(player));
		player.getInventory().setArmorContents(plugin.CommandExecutor.senderArm.get(player));
		player.setHealth(plugin.CommandExecutor.senderHealth.get(player));
		player.setFoodLevel(plugin.CommandExecutor.senderHunger.get(player));

		Player target = plugin.CommandExecutor.target.get(player);

		HashSet<Player> spectators = plugin.CommandExecutor.spectator.get(target);
		
		plugin.CommandExecutor.spectator.put(target, null);

		if (spectators != null) {
			for (Player p : spectators) {
				if (!p.equals(player)) {
					if (plugin.CommandExecutor.spectator.get(target) == null) {
						plugin.CommandExecutor.spectator.put(target, new HashSet<Player>());
					}
					plugin.CommandExecutor.spectator.get(target).add(p);
				}
			}
		} else {
			plugin.CommandExecutor.isBeingSpectated.put(plugin.CommandExecutor.target.get(player), false);
		}
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				for (Player p : plugin.getServer().getOnlinePlayers()) {
					p.showPlayer(player);
				}
				
				player.showPlayer(plugin.CommandExecutor.target.get(player));
				plugin.CommandExecutor.target.get(player).showPlayer(player);
			}
		}, 10L);
	}
}
