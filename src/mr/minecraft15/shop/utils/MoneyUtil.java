package mr.minecraft15.shop.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class MoneyUtil {

    public static double getMoney(Player p) {
	return econ.getBalance(p);
    }

    public static void withdrawMoney(Player p, double coins) {
	EconomyResponse r = econ.withdrawPlayer(p, coins);
	if (!r.transactionSuccess()) {
	    System.err.println(String.format(
		    "An error occured while withdrawing " + p.getName() + " " + coins + " money : %s", r.errorMessage));
	}
    }

    public static void depositMoney(Player p, double coins) {
	EconomyResponse r = econ.depositPlayer(p, coins);
	if (!r.transactionSuccess()) {
	    System.err.println(String.format(
		    "An error occured while depositing " + p.getName() + " " + coins + " money : %s", r.errorMessage));
	}
    }

    private static Economy econ = null;

    public static boolean setupEconomy() {
	if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
	    return false;
	}
	RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
	if (rsp == null) {
	    return false;
	}
	econ = rsp.getProvider();
	return econ != null;
    }

}
