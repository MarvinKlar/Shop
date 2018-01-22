package mr.minecraft15.shop.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import mr.minecraft15.shop.Main;
import mr.minecraft15.shop.managers.MessageManager;
import mr.minecraft15.shop.utils.IntUtil;
import mr.minecraft15.shop.utils.MoneyUtil;
import mr.minecraft15.shop.utils.StringUtil;

public class ShopCommand implements CommandExecutor, Listener {
    private static File file;
    private static YamlConfiguration cfg;

    private static String shopTitle;
    private static int currency = -1;

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
	MessageManager messageManager = Main.getMessageManager();
	final Player p = (Player) s;
	if (a.length == 0) {
	    showShop(p);
	} else {
	    if (p.hasPermission("shop.edit")) {
		if (a.length >= 2) {
		    if (a[0].equalsIgnoreCase("addcategory")) {
			int slot;
			try {
			    slot = Integer.parseInt(a[1]);
			} catch (final Exception e) {
			    StringUtil.noValidNumber(p, a[1]);
			    return true;
			}
			if (slot >= 0 & slot <= 53) {
			    final ItemStack is = p.getItemInHand();
			    if (is == null || is.getType() == null || is.getType() == Material.AIR) {
				p.sendMessage(messageManager.getMessage("Select_Item_For_Category"));
			    } else {
				if (cfg.contains("Shop.categories." + slot)) {
				    p.sendMessage(messageManager.getMessage("Edited_Category"));
				} else {
				    p.sendMessage(messageManager.getMessage("Created_Category"));
				}
				cfg.set("Shop.categories." + slot, is);
				save(file, cfg);
			    }
			} else {
			    StringUtil.noValidNumber(p, a[1]);
			}
		    } else if (a[0].equalsIgnoreCase("removecategory")) {
			int slot;
			try {
			    slot = Integer.parseInt(a[1]);
			} catch (final Exception e) {
			    StringUtil.noValidNumber(p, a[1]);
			    return true;
			}
			if (slot >= 0 & slot <= 53) {
			    p.sendMessage(messageManager.getMessage("Deleted_Category"));
			    cfg.set("Shop.categories." + slot, null);
			    save(file, cfg);
			} else {
			    StringUtil.noValidNumber(p, a[1]);
			}
		    } else if (a[0].equalsIgnoreCase("additem")) {
			if (a.length >= 5) {
			    final ItemStack is = p.getItemInHand();
			    if (is == null || is.getType() == Material.AIR) {
				p.sendMessage(messageManager.getMessage("Select_Item"));
				return true;
			    }
			    is.setAmount(1);
			    if (IntUtil.isInt(a[1]) && IntUtil.isInt(a[2]) && IntUtil.isDouble(a[3])
				    && IntUtil.isDouble(a[4])) {
				final int category = IntUtil.getInt(a[1]);
				final int slot = IntUtil.getInt(a[2]);
				final double buyprice = IntUtil.getDouble(a[3]);
				final double sellprice = IntUtil.getDouble(a[4]);
				if (category >= 0 && slot >= 0 && buyprice > 0 && sellprice >= 0) {
				    cfg.set("Shop.items." + category + "." + slot + ".item", is);
				    cfg.set("Shop.items." + category + "." + slot + ".buy", buyprice);
				    cfg.set("Shop.items." + category + "." + slot + ".sell", sellprice);
				    save(file, cfg);
				    p.sendMessage(messageManager.getMessage("Added_Item", "item_name",
					    StringUtil.getItemName(is), "category_slot", category, "category_name",
					    (cfg.contains("Shop.categories." + category)
						    ? StringUtil.getItemName(
							    cfg.getItemStack("Shop.categories." + category))
						    : messageManager.getMessage("Category_Does_Not_Exist",
							    "category_slot", category))));
				    return true;
				}
			    }
			    p.sendMessage(messageManager.getMessage("Invalid_Number"));
			} else {
			    shopHelp(p);
			}
		    } else if (a[0].equalsIgnoreCase("removeitem")) {
			if (a.length >= 3) {
			    if (IntUtil.isInt(a[1]) && IntUtil.isInt(a[2])) {
				final int category = IntUtil.getInt(a[1]);
				final int item = IntUtil.getInt(a[2]);
				if (cfg.contains("Shop.categories." + category)) {
				    if (cfg.contains("Shop.items." + category + "." + item)) {
					ItemStack is = cfg.getItemStack("Shop.items." + item + ".item");
					cfg.set("Shop.items." + category + ".item", null);
					save(file, cfg);
					p.sendMessage(messageManager.getMessage("Removed_Item", "item_slot", item,
						"item_name", StringUtil.getItemName(is), "category_slot", category,
						"category_name",
						(cfg.contains("Shop.categories." + category)
							? StringUtil.getItemName(
								cfg.getItemStack("Shop.categories." + category))
							: messageManager.getMessage("Category_Does_Not_Exist",
								"category_slot", category))));
				    } else {
					p.sendMessage(
						messageManager.getMessage("Item_Does_Not_Exist", "item_slot", item));
				    }
				} else {
				    p.sendMessage(messageManager.getMessage("Category_Does_Not_Exist", "category_slot",
					    category));
				}
			    } else {
				p.sendMessage(messageManager.getMessage("Invalid_Number"));
			    }
			} else {
			    shopHelp(p);
			}
		    }
		} else {
		    shopHelp(p);
		}
	    } else {
		showShop(p);
	    }
	}
	return true;
    }

    private void shopHelp(final Player p) {
	for (String message : Main.getMessageManager().getList("Help")) {
	    p.sendMessage(message);
	}
    }

    private void showShop(final Player p) {
	if (cfg.contains("Shop.categories")) {
	    final Inventory i = Bukkit.createInventory(null, 54, shopTitle);
	    for (final String slot : cfg.getConfigurationSection("Shop.categories").getKeys(false)) {
		i.setItem(Integer.parseInt(slot), cfg.getItemStack("Shop.categories." + slot));
	    }
	    p.openInventory(i);
	} else {
	    p.sendMessage(Main.getMessageManager().getMessage("Coming_Soon"));
	}
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
	final Player p = (Player) e.getWhoClicked();
	if (e.isCancelled()) {
	    return;
	}
	if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR || e.getClick() == null
		|| e.getRawSlot() == -999) {
	    return;
	}
	final String ename = e.getInventory().getName();
	if (ename.equalsIgnoreCase(shopTitle)) {
	    e.setCancelled(true);
	    final int categoryslot = e.getSlot();
	    final Inventory i = Bukkit.createInventory(null, 54,
		    shopTitle + " - " + StringUtil.getShopNameSuffix(e.getCurrentItem()));
	    if (cfg.getConfigurationSection("Shop.items." + categoryslot) == null) {
		return;
	    }
	    for (final String itemslot : cfg.getConfigurationSection("Shop.items." + categoryslot).getKeys(false)) {
		final ItemStack is = cfg.getItemStack("Shop.items." + categoryslot + "." + itemslot + ".item").clone();
		final ItemMeta im = is.getItemMeta();
		final List<String> list = new ArrayList<>();
		for (String message : Main.getMessageManager().getList("Shop_Item_Description", "buy_price",
			StringUtil.getCurrency(cfg.getDouble("Shop.items." + categoryslot + "." + itemslot + ".buy")),
			"sell_price", StringUtil
				.getCurrency(cfg.getDouble("Shop.items." + categoryslot + "." + itemslot + ".sell")))) {
		    list.add(message);
		}
		im.setLore(list);
		is.setItemMeta(im);
		i.setItem(Integer.parseInt(itemslot), is);
	    }
	    p.openInventory(i);
	} else if (ename.contains(shopTitle + " - ")) {
	    e.setCancelled(true);
	    for (final String slotImCategorymenu : cfg.getConfigurationSection("Shop.categories").getKeys(false)) {
		if (e.getInventory().getName().contains(
			StringUtil.getShopNameSuffix(cfg.getItemStack("Shop.categories." + slotImCategorymenu)))) {
		    final double buyprice = cfg
			    .getDouble("Shop.items." + slotImCategorymenu + "." + e.getSlot() + ".buy");
		    final double sellprice = cfg
			    .getDouble("Shop.items." + slotImCategorymenu + "." + e.getSlot() + ".sell");
		    final ItemStack item = cfg
			    .getItemStack("Shop.items." + slotImCategorymenu + "." + e.getSlot() + ".item");
		    if (e.getClick() == ClickType.LEFT) {
			buyItem(p, item, 1, buyprice);
		    } else if (e.getClick() == ClickType.SHIFT_LEFT) {
			buyItem(p, item, 64, buyprice);
		    } else if (e.getClick() == ClickType.RIGHT) {
			if (sellprice == 0) {
			    p.sendMessage(Main.getMessageManager().getMessage("No_Money_For_This_Item"));
			} else {
			    sellItem(p, item, 1, sellprice);
			}
		    } else if (e.getClick() == ClickType.SHIFT_RIGHT) {
			if (sellprice == 0) {
			    p.sendMessage(Main.getMessageManager().getMessage("No_Money_For_This_Item"));
			} else {
			    sellItem(p, item, 64, sellprice);
			}
		    }
		    return;
		}
	    }
	}
    }

    private void buyItem(final Player p, final ItemStack is, final int amount, final double costs) {
	MessageManager messageManager = Main.getMessageManager();
	if (is == null) {
	    return;
	}
	int free = 0;
	for (final ItemStack current : p.getInventory().getContents()) {
	    if (current == null) {
		free += is.getMaxStackSize();
	    } else {
		if (current.getAmount() < current.getMaxStackSize()) {
		    free += current.getMaxStackSize() - current.getAmount();
		}
	    }
	}
	if (free < amount) {
	    p.sendMessage(messageManager.getMessage("Not_Enough_Free_slots", "item_amount", amount));
	    return;
	}

	final double totalcosts = costs * amount;
	final Material m = Material.getMaterial(currency);
	if (totalcosts <= money(p)) {
	    if (currency == -1) {
		MoneyUtil.withdrawMoney(p, totalcosts);
	    } else {
		for (int i = 0; i < totalcosts; i++) {
		    removeItem(p, m);
		}
	    }
	    p.sendMessage(messageManager.getMessage("Bought_Item", "item_amount", amount, "item_name",
		    StringUtil.getItemName(is), "money", StringUtil.getCurrency(totalcosts)));
	    for (int i = 0; i < amount; i++) {
		p.getInventory().addItem(is);
	    }
	    p.updateInventory();
	} else {
	    final double missing = totalcosts - money(p);
	    p.sendMessage(messageManager.getMessage("Not_Enough_Money_To_Buy", "money", StringUtil.getCurrency(missing),
		    "item_name", StringUtil.getItemName(is), "item_amount", amount));
	}
    }

    public static void sellItem(final Player p, final ItemStack is, final int amount, final double costs) {
	MessageManager messageManager = Main.getMessageManager();
	final double totalcosts = costs * amount;
	int item = 0;
	for (final ItemStack i : p.getInventory().getContents()) {
	    if (i != null) {
		if (is.isSimilar(i)) {
		    item += i.getAmount();
		}
	    }
	}
	final Material m = Material.getMaterial(currency);
	if (item >= amount) {
	    for (int i = 0; i < amount; i++) {
		removeItem(p, is);
	    }
	    p.sendMessage(messageManager.getMessage("Sold_Item", "item_amount", amount, "item_name",
		    StringUtil.getItemName(is), "money", StringUtil.getCurrency(totalcosts)));
	    p.updateInventory();
	    if (currency == -1) {
		MoneyUtil.depositMoney(p, totalcosts);
	    } else {
		for (int i = 0; i < totalcosts; i++) {
		    p.getInventory().addItem(new ItemStack(m));
		}
	    }
	} else {
	    final int missing = amount - item;
	    p.sendMessage(messageManager.getMessage("Not_Enough_Items_To_Sell", "item_name", StringUtil.getItemName(is),
		    "item_amount_missing", missing, "item_amount", amount, "money",
		    StringUtil.getCurrency(totalcosts)));
	}
    }

    public static void removeItem(final Player p, final ItemStack is) {
	for (int i = 0; i < p.getInventory().getSize(); i++) {
	    if (p.getInventory().getItem(i) != null) {
		if (p.getInventory().getItem(i).isSimilar(is)) {
		    if (p.getInventory().getItem(i).getAmount() == 1) {
			p.getInventory().setItem(i, new ItemStack(Material.AIR));
			p.updateInventory();
			return;
		    }
		    p.getInventory().getItem(i).setAmount(p.getInventory().getItem(i).getAmount() - 1);
		    p.updateInventory();
		    return;
		}
	    }
	}
    }

    public static void removeItem(final Player p, final Material m) {
	for (int i = 0; i < p.getInventory().getSize(); i++) {
	    if (p.getInventory().getItem(i) != null) {
		if (p.getInventory().getItem(i).getType() == m) {
		    if (p.getInventory().getItem(i).getAmount() == 1) {
			p.getInventory().setItem(i, new ItemStack(Material.AIR));
			p.updateInventory();
		    } else {
			p.getInventory().getItem(i).setAmount(p.getInventory().getItem(i).getAmount() - 1);
			p.updateInventory();
		    }
		}
	    }
	}
    }

    private double money(final Player p) {
	if (currency == -1) {
	    return MoneyUtil.getMoney(p);
	}

	int items = 0;
	final Material material = Material.getMaterial(currency);
	for (int i = 0; i < p.getInventory().getSize(); i++) {
	    if (p.getInventory().getItem(i) != null) {
		if (p.getInventory().getItem(i).getType() == material) {
		    items += p.getInventory().getItem(i).getAmount();
		}
	    }
	}
	return items;
    }

    public static void setCurrency(final int newCurrency) {
	currency = newCurrency;
    }

    public static void setFile(final File newFile) {
	file = newFile;
    }

    public static File getFile() {
	return file;
    }

    public static void setConfig(final YamlConfiguration newConfig) {
	cfg = newConfig;
    }

    public static void save(File file, YamlConfiguration cfg) {
	try {
	    cfg.save(file);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static int getCurrency() {
	return currency;
    }

    public static void SetShopTitle(String shopTitle) {
	ShopCommand.shopTitle = shopTitle;
    }
}
