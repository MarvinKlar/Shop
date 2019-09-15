package mr.minecraft15.shop.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import mr.minecraft15.shop.Main;
import mr.minecraft15.shop.managers.MessageManager;

public class StringUtil {
    public static String getCurrency(double money) {
	MessageManager messageManager = Main.getMessageManager();
	String moneyString = money + "";
	if (moneyString.endsWith(".0")) {
	    moneyString = moneyString.substring(0, moneyString.length() - 2);
	}
	if (money == 1) {
	    return moneyString + " " + messageManager.getMessage("Currency_Singular");
	} else {
	    if (moneyString.contains(".")) {
		moneyString = moneyString.replace(".", ",");
		String[] parts = moneyString.split(",");
		String end = parts[1];
		if (parts[1].length() > 3) {
		    end = end.substring(0, 2);
		    while (end.endsWith("0")) {
			end = end.substring(0, end.length() - 2);
		    }
		}
		if (end.length() == 0) {
		    return parts[0] + " " + messageManager.getMessage("Currency_Plural");
		} else {
		    return parts[0] + "," + end + " " + messageManager.getMessage("Currency_Plural");
		}
	    } else {
		return moneyString + " " + messageManager.getMessage("Currency_Plural");
	    }
	}
    }

    public static String getShopNameSuffix(ItemStack currentItem) {
	if (currentItem.hasItemMeta() && currentItem.getItemMeta().getDisplayName() != null
		&& !currentItem.getItemMeta().getDisplayName().equals("")) {
	    return currentItem.getItemMeta().getDisplayName();
	} else {
	    return getItemName(currentItem);
	}
    }

    public static void noValidNumber(Player p, String invalidNumber) {
	p.sendMessage(Main.getMessageManager().getMessage("No_Valid_Number", "number", invalidNumber));
    }

    public static String getItemName(ItemStack is) {
	return getItemName(is.getType()) + (((Damageable) is.getItemMeta()).getDamage() == 0 ? ""
		: ":" + ((Damageable) is.getItemMeta()).getDamage());
    }

    public static String getItemName(Material type) {
	return getItemName(type.name());
    }

    public static String getItemName(String name) {
	String result = "";
	for (String part : name.split("_")) {
	    result += " " + part.substring(0, 1).toUpperCase() + part.substring(1).toLowerCase();
	}
	return result.trim();
    }

}
