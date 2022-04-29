package me.zelha.thepit.mainpkg.data;

import me.zelha.thepit.Main;
import me.zelha.thepit.events.PitDamageEvent;
import me.zelha.thepit.mainpkg.listeners.ArrowListener;
import me.zelha.thepit.utils.ZelLogic;
import net.minecraft.server.MinecraftServer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DamageLog {

    private final ZelLogic zl = Main.getInstance().getZelLogic();
    private final boolean hasPlayer;
    private final ItemStack item;
    private final double damage;
    private final int time;
    private final String mainName;
    private final String subName;
    private final String prestigeToShow;
    private final double damagedHealth;
    private final boolean isAttacker;
    private final String pitDamageType;
    private final boolean environmental;

    public DamageLog(PitDamageEvent event, boolean isAttacker) {
        Player damaged = event.getDamaged();
        Player damager = event.getDamager();

        this.hasPlayer = true;

        if (event.getArrow() != null) {
            this.item = ArrowListener.getArrowItem(event.getArrow());
        } else {
            this.item = damager.getInventory().getItemInMainHand();
        }

        this.damage = event.getDamage() * event.getBoost();
        this.time = MinecraftServer.currentTick;
        this.subName = damaged.getName();

        if (isAttacker) {
            this.mainName = damaged.getName();
            this.prestigeToShow = zl.getColorBracketAndLevel(damager);
        } else {
            this.mainName = damager.getName();
            this.prestigeToShow = zl.getColorBracketAndLevel(damaged);
        }

        this.damagedHealth = damaged.getHealth() - event.getDamage();
        this.isAttacker = isAttacker;

        if (event.getArrow() != null) {
            this.pitDamageType = "§6Arrow";
        } else if (zl.itemCheck(item)) {
            this.pitDamageType = "§cMelee";
        } else {
            this.pitDamageType = "§rHand";
        }

        this.environmental = false;
    }

    public DamageLog(Player damaged, Player damager, boolean isAttacker, double damage, String damageType) {
        this.hasPlayer = true;
        this.item = null;
        this.damage = damage;
        this.time = MinecraftServer.currentTick;
        this.subName = damaged.getName();

        if (isAttacker) {
            this.mainName = damaged.getName();
            this.prestigeToShow = zl.getColorBracketAndLevel(damager);
        } else {
            this.mainName = damager.getName();
            this.prestigeToShow = zl.getColorBracketAndLevel(damaged);
        }

        this.damagedHealth = damaged.getHealth() - damage;
        this.isAttacker = isAttacker;
        this.pitDamageType = damageType;
        this.environmental = false;
    }

    public DamageLog(double damage, String damageType, boolean environmental) {
        this.hasPlayer = false;
        this.item = null;
        this.damage = damage;
        this.time = MinecraftServer.currentTick;
        this.mainName = null;
        this.subName = null;
        this.prestigeToShow = null;
        this.damagedHealth = 1313;
        this.isAttacker = false;
        this.pitDamageType = damageType;
        this.environmental = environmental;
    }

    public boolean hasPlayer() {
        return hasPlayer;
    }

    public ItemStack item() {
        return item;
    }

    public double damage() {
        return damage;
    }

    public int time() {
        return time;
    }

    public String mainName() {
        return mainName;
    }

    public String subName() {
        return subName;
    }

    public String prestigeToShow() {
        return prestigeToShow;
    }

    public Double damagedHealth() {
        return damagedHealth;
    }

    public boolean isAttacker() {
        return isAttacker;
    }

    public String pitDamageType() {
        return pitDamageType;
    }

    public boolean environmental() {
        return environmental;
    }
}
