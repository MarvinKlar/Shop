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
    private static String currency = "-1";

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
			if (IntUtil.isValidSlot(a[1])) {
			    int slot = IntUtil.getInt(a[1]);
			    final ItemStack is = p.getInventory().getItemInMainHand();
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
			if (IntUtil.isValidSlot(a[1])) {
			    p.sendMessage(messageManager.getMessage("Deleted_Category"));
			    cfg.set("Shop.categories." + IntUtil.getInt(a[1]), null);
			    save(file, cfg);
			} else {
			    StringUtil.noValidNumber(p, a[1]);
			}
		    } else if (a[0].equalsIgnoreCase("additem")) {
			if (a.length >= 5) {
			    final ItemStack is = p.getInventory().getItemInMainHand();
			    if (is == null || is.getType() == Material.AIR) {
				p.sendMessage(messageManager.getMessage("Select_Item"));
				return true;
			    }
			    is.setAmount(1);
			    if (IntUtil.isInt(a[1]) && IntUtil.isValidSlot(a[2]) && IntUtil.isDouble(a[3])
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
			    if (IntUtil.isInt(a[1]) && IntUtil.isValidSlot(a[2])) {
				final int category = IntUtil.getInt(a[1]);
				final int slot = IntUtil.getInt(a[2]);
				if (cfg.contains("Shop.categories." + category)) {
				    if (cfg.contains("Shop.items." + category + "." + slot)) {
					ItemStack is = cfg.getItemStack("Shop.items." + slot + ".item");
					cfg.set("Shop.items." + category + ".item", null);
					save(file, cfg);
					p.sendMessage(messageManager.getMessage("Removed_Item", "item_slot", slot,
						"item_name", StringUtil.getItemName(is), "category_slot", category,
						"category_name",
						(cfg.contains("Shop.categories." + category)
							? StringUtil.getItemName(
								cfg.getItemStack("Shop.categories." + category))
							: messageManager.getMessage("Category_Does_Not_Exist",
								"category_slot", category))));
				    } else {
					p.sendMessage(
						messageManager.getMessage("Item_Does_Not_Exist", "item_slot", slot));
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
		    } else if (a[0].equalsIgnoreCase("setbackbutton")) {
			if (a.length >= 3) {
			    if (IntUtil.isValidSlot(a[1]) && IntUtil.isValidSlot(a[2])) {
				final int slot = IntUtil.getInt(a[2]);
				final int category = IntUtil.getInt(a[1]);
				if (cfg.contains("Shop.categories." + category)) {
				    if (!cfg.contains("Shop.items." + category + "." + slot)) {
					cfg.set("Shop.backbuttons." + category, slot);
					save(file, cfg);
					p.sendMessage(
						messageManager.getMessage("Back_Button_Created", "item_slot", slot));
				    } else {
					p.sendMessage(messageManager.getMessage("Slot_Is_Already_Occupied", "item_slot",
						slot));
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
		    } else if (a[0].equalsIgnoreCase("removebackbutton")) {
			if (IntUtil.isValidSlot(a[1])) {
			    final int category = IntUtil.getInt(a[1]);
			    if (cfg.contains("Shop.categories." + category)) {
				cfg.set("Shop.backbuttons." + category, null);
				save(file, cfg);
				p.sendMessage(messageManager.getMessage("Back_Button_Removed"));
			    } else {
				p.sendMessage(messageManager.getMessage("Category_Does_Not_Exist", "category_slot",
					category));
			    }
			} else {
			    p.sendMessage(messageManager.getMessage("Invalid_Number"));
			}
		    } else if (a[0].equalsIgnoreCase("setrows") || a[0].equalsIgnoreCase("setsize")) {
			if (a.length >= 3) {
			    if (IntUtil.isInt(a[1]) && IntUtil.isInt(a[2]) && IntUtil.getInt(a[2]) >= 1
				    && IntUtil.getInt(a[2]) <= 7) {
				final int category = IntUtil.getInt(a[1]);
				final int rows = IntUtil.getInt(a[2]);
				if (cfg.contains("Shop.categories." + category)) {
				    cfg.set("Shop.rows." + category, rows);
				    save(file, cfg);
				    p.sendMessage(messageManager.getMessage("Category_Rows_Set", "category", category,
					    "amount_rows", rows));
				    int items_hidden = 0;
				    for (final String itemslot : cfg.getConfigurationSection("Shop.items." + category)
					    .getKeys(false)) {
					if (IntUtil.getInt(itemslot) >= rows * 9) {
					    items_hidden++;
					}
				    }
				    if (items_hidden > 0) {
					p.sendMessage(messageManager.getMessage("Items_Hidden_By_Size", "items_hidden",
						items_hidden, "category", category, "amount_rows", rows));
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
		i.setItem(IntUtil.getInt(slot), cfg.getItemStack("Shop.categories." + slot));
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

	MessageManager messageManager = Main.getMessageManager();
	if (e.getCurrentItem().getType() == Material.WOOD_DOOR && e.getCurrentItem().hasItemMeta()
		&& e.getCurrentItem().getItemMeta().hasDisplayName() && e.getCurrentItem().getItemMeta()
			.getDisplayName().equalsIgnoreCase(messageManager.getMessage("Back"))) {
	    showShop(p);
	}

	final String ename = e.getView().getTitle();
	if (ename.equalsIgnoreCase(shopTitle)) {
	    e.setCancelled(true);
	    final int categoryslot = e.getSlot();
	    if (cfg.getConfigurationSection("Shop.items." + categoryslot) == null) {
		return;
	    }
	    final Inventory i = Bukkit.createInventory(null, cfg.getInt("Shop.rows." + categoryslot, 6) * 9,
		    shopTitle + " - " + StringUtil.getShopNameSuffix(e.getCurrentItem()));
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
		i.setItem(IntUtil.getInt(itemslot), is);
	    }
	    if (cfg.contains("Shop.backbuttons." + categoryslot)) {
		final ItemStack is = new ItemStack(Material.WOOD_DOOR);
		final ItemMeta im = is.getItemMeta();
		im.setDisplayName(messageManager.getMessage("Back"));
		is.setItemMeta(im);
		i.setItem(cfg.getInt("Shop.backbuttons." + categoryslot), is);
		System.out.println(cfg.getInt("Shop.backbuttons." + categoryslot));
	    }
	    p.openInventory(i);
	} else if (ename.contains(shopTitle + " - ")) {
	    e.setCancelled(true);
	    for (final String slotInCategoryMenu : cfg.getConfigurationSection("Shop.categories").getKeys(false)) {
		if (e.getView().getTitle().contains(
			StringUtil.getShopNameSuffix(cfg.getItemStack("Shop.categories." + slotInCategoryMenu)))) {
		    final double buyprice = cfg
			    .getDouble("Shop.items." + slotInCategoryMenu + "." + e.getSlot() + ".buy");
		    final double sellprice = cfg
			    .getDouble("Shop.items." + slotInCategoryMenu + "." + e.getSlot() + ".sell");
		    final ItemStack item = cfg
			    .getItemStack("Shop.items." + slotInCategoryMenu + "." + e.getSlot() + ".item");
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
	    p.sendMessage(messageManager.getMessage("Not_Enough_Free_Slots", "item_amount", amount));
	    return;
	}

	final double totalcosts = costs * amount;
	final Material m = Material.getMaterial(currency);
	if (totalcosts <= money(p)) {
	    if (currency.equals("-1")) {
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
	    if (currency.equals("-1")) {
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
	if (currency.equals("-1")) {
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

    public static void setCurrency(final String newCurrency) {
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

    public static String getCurrency() {
	return currency;
    }

    public static void setShopTitle(String shopTitle) {
	ShopCommand.shopTitle = shopTitle;
    }
}
