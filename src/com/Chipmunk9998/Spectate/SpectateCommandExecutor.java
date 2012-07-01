package com.Chipmunk9998.Spectate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class SpectateCommandExecutor implements CommandExecutor {

    public Spectate plugin;

    public Map<Player, Boolean> isSpectating = new HashMap<Player, Boolean>();
    public Map<Player, Boolean> isBeingSpectated = new HashMap<Player, Boolean>();
    public Map<Player, HashSet<Player>> spectator = new HashMap<Player, HashSet<Player>>();
    public Map<Player, Player> target = new HashMap<Player, Player>();
    public Map<Player, ItemStack[]> senderInv = new HashMap<Player, ItemStack[]>();
    public Map<Player, ItemStack[]> senderArm = new HashMap<Player, ItemStack[]>();
    public Map<Player, Integer> senderHunger = new HashMap<Player, Integer>();
    public Map<Player, Integer> senderHealth = new HashMap<Player, Integer>();
    public HashMap<Player, Location> origLocation = new HashMap<Player, Location>();

    String cmdtarget;
    Player targetPlayer;


    public SpectateCommandExecutor(Spectate plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        final Player cmdsender = (Player) sender;

        if (!cmdsender.hasPermission("spectate.use")) {
            cmdsender.sendMessage("\u00a7cYou do not have permission to spectate.");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("spectate") || cmd.getName().equalsIgnoreCase("spec")) {
            if (args.length > 0) {
                cmdtarget = args[0];
                targetPlayer = Bukkit.getPlayer(cmdtarget);

                if (targetPlayer != null) {
                    if (cmdsender.getName().equals(targetPlayer.getName())) {
                        cmdsender.sendMessage("\u00a77Did you really just try to spectate yourself?");
                        return true;
                    }

                    if (isSpectating.get(cmdsender) != null) {
                        if (targetPlayer.getName().equals(target.get(cmdsender).getName())) {
                            cmdsender.sendMessage("\u00a77You are already spectating them.");
                            return true;
                        }
                    }

                    if (isSpectating.get(targetPlayer) != null) {
                        if (isSpectating.get(targetPlayer)) {
                            cmdsender.sendMessage("\u00a77They are currently spectating someone.");
                            return true;
                        }
                    }

                    if (targetPlayer.isDead()) {
                        cmdsender.sendMessage("\u00a77They are currently dead.");
                        return true;
                    }

                    if (plugin.conf.getBoolean("permission-canspectate")) {
                        if (targetPlayer.hasPermission("spectate.cantspectate")) {
                            cmdsender.sendMessage("\u00a77They can not be spectated.");
                            return true;
                        }
                    }

                    if (isSpectating.get(cmdsender) != null) {
                        if (isSpectating.get(cmdsender)) {
                            plugin.SpectateOff.spectateOff(cmdsender);
                        }
                    }


                    cmdsender.sendMessage("\u00a77You are now spectating " + targetPlayer.getName() + ".");
                    origLocation.put(cmdsender, cmdsender.getLocation());
                    isSpectating.put(cmdsender, true);
                    isBeingSpectated.put(targetPlayer, true);

                    if (spectator.get(targetPlayer) == null) {
                        spectator.put(targetPlayer, new HashSet<Player>());
                    }

                    spectator.get(targetPlayer).add(cmdsender);


                    target.put(cmdsender, targetPlayer);
                    cmdsender.getPlayer().teleport(target.get(cmdsender));
                    senderInv.put(cmdsender, cmdsender.getInventory().getContents());
                    senderArm.put(cmdsender, cmdsender.getInventory().getArmorContents());
                    senderHunger.put(cmdsender, cmdsender.getFoodLevel());
                    senderHealth.put(cmdsender, cmdsender.getHealth());
                    cmdsender.getInventory().clear();
                    cmdsender.getInventory().setContents(targetPlayer.getInventory().getContents());
                    cmdsender.getInventory().setArmorContents(targetPlayer.getInventory().getArmorContents());

                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.hidePlayer(cmdsender);
                    }

                    targetPlayer.hidePlayer(cmdsender);
                    cmdsender.hidePlayer(targetPlayer);

                    return true;

                }

                cmdsender.sendMessage("\u00a7cError: Player is not online\u00a7f");
                return true;

            }

            cmdsender.sendMessage("\u00a7cError: No player target\u00a7f");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("spectateoff") || cmd.getName().equalsIgnoreCase("specoff")) {
            if (isSpectating.get(cmdsender) != null) {
                if (isSpectating.get(cmdsender)) {
                    plugin.SpectateOff.spectateOff(cmdsender);
                    return true;
                }
            }
            cmdsender.sendMessage("\u00a77You are currently not spectating anyone\u00a7f");
            return true;
        }

        return true;
    }
}
