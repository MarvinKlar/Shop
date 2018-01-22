package mr.minecraft15.shop;

import java.io.File;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import mr.minecraft15.shop.commands.ShopCommand;
import mr.minecraft15.shop.managers.MessageManager;
import mr.minecraft15.shop.utils.MoneyUtil;

public class Main extends JavaPlugin {
    private static MessageManager messageManager;

    @Override
    public void onEnable() {
	final FileConfiguration cfg = getConfig();
	cfg.options().copyDefaults(true);

	cfg.addDefault("Currency", -1);
	cfg.addDefault("Prefix", "&8[&6Shop&8]&7");

	cfg.addDefault("Messages.No_Valid_Number", "%prefix% &6%number% &7is no valid number.");
	cfg.addDefault("Messages.Select_Item_For_Category",
		"%prefix% Please take an item in your hand, which should be added as a category.");
	cfg.addDefault("Messages.Edited_Category", "%prefix% The category was successfully edited.");
	cfg.addDefault("Messages.Created_Category", "%prefix% The category was successfully created.");
	cfg.addDefault("Messages.Deleted_Category", "%prefix% The category was successfully deleted.");
	cfg.addDefault("Messages.Select_Item",
		"%prefix% Please take an item in your hand, which you want to add to the shop.");
	cfg.addDefault("Messages.Added_Item",
		"%prefix% The item &6%item_name% &7was added to the category &6%category_slot% &7(&6%category_name%&7).");
	cfg.addDefault("Messages.Category_Does_Not_Exist", "&cThe category &6%category_slot% &cdoes not exist!");
	cfg.addDefault("Messages.Invalid_Number",
		"%prefix% The category, slot and prices need to be numbers and have to be valid.");
	cfg.addDefault("Messages.Removed_Item",
		"%prefix% The item &6%item_slot% &7(&6%item_name%&7) was removed from the category &6%category_slot% &7(&6category_name&7).");
	cfg.addDefault("Messages.Item_Does_Not_Exist", "%prefix% The item &6%item_slot% &7does not exist.");
	cfg.addDefault("Messages.Help",
		Arrays.asList(new String[] { "&6/Shop addcategory <slot> &7- Adds a category",
			"&6/Shop removecategory <slot> &7- Removes a category",
			"&6/Shop additem <category> <slot> <buy price> <sell price> &7- Adds a item",
			"&6/Shop removeitem <category> <slot> &7- Removes a item" }));
	cfg.addDefault("Messages.Coming_Soon", "%prefix% Coming soon...");
	cfg.addDefault("Messages.Shop_Item_Description",
		Arrays.asList(new String[] { "&7Buy price: &6%buy_price% &7(Left click + Shift = Buy 64 items)",
			"&7Sell price: &6%sell_price% &7(Right click + Shift = Sell 64 items)" }));
	cfg.addDefault("Messages.Shop_Name", "&6Shop");
	cfg.addDefault("Messages.No_Money_For_This_Item", "%prefix% You don't get money for this item.");
	cfg.addDefault("Messages.Not_Enough_Free_Slots",
		"%prefix% You do not have enough free slots in you inventory to buy this item &6%item_amount% times&7.");
	cfg.addDefault("Messages.Currency_Singular", "Coin");
	cfg.addDefault("Messages.Currency_Plural", "Coins");
	cfg.addDefault("Messages.Bought_Item", "%prefix% You bought &6%item_amount% %item_name% &7for &6%money%&7.");
	cfg.addDefault("Messages.Not_Enough_Money_To_Buy",
		"%prefix% You need &6%money% &7more to buy &6%item_amount% %item_name%&7.");
	cfg.addDefault("Messages.Sold_Item", "%prefix% You sold &6%item_amount% %item_name% &7for &6%money%&7.");
	cfg.addDefault("Messages.Not_Enough_Items_To_Sell",
		"%prefix% You need &6%item_amount_missing% %item_name% &7more to sell &6%item_amount% %item_name% &7for &6%money%.");

	saveConfig();

	setMessageManager(new MessageManager(cfg, cfg.getString("Prefix")));

	ShopCommand.SetShopTitle(messageManager.getMessage("Shop_Name"));

	ShopCommand.setCurrency(cfg.getInt("Currency"));

	ShopCommand.setFile(new File(getDataFolder(), "Shop.yml"));
	ShopCommand.setConfig(YamlConfiguration.loadConfiguration(ShopCommand.getFile()));

	getCommand("shop").setExecutor(new ShopCommand());
	Bukkit.getPluginManager().registerEvents(new ShopCommand(), this);

	if (ShopCommand.getCurrency() == -1) {
	    if (!MoneyUtil.setupEconomy()) {
		System.err.println("##############################");
		System.err.println("##############################");
		System.err.println(
			"Error: Vault and/or Economy plugin not found! Please provide an economy plugin with a Vault integration and Vault itself to use the Vault money! Otherwise you will only be able to use items as currency!!!");
		System.err.println("##############################");
		System.err.println("##############################");
	    }
	}
    }

    public static MessageManager getMessageManager() {
	return messageManager;
    }

    public static void setMessageManager(MessageManager messageManager) {
	Main.messageManager = messageManager;
    }
}
