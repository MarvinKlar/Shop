package mr.minecraft15.shop.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class MessageManager {
    private final HashMap<String, List<String>> hmpLists = new HashMap<>();
    private final HashMap<String, String> hmpMessages = new HashMap<>();
    private final String strPrefix;

    public MessageManager(final FileConfiguration filConfig, final String strPrefix) {
	this.strPrefix = ChatColor.translateAlternateColorCodes('&', strPrefix);

	for (final String strKey : filConfig.getConfigurationSection("Messages").getKeys(false)) {
	    if (filConfig.getStringList("Messages." + strKey).isEmpty()) {
		addMessage(strKey, filConfig.getString("Messages." + strKey));
	    } else {
		addList(strKey, filConfig.getStringList("Messages." + strKey));
	    }
	}
    }

    public String getMessage(String strKey, final Object... objParameters) {
	strKey = strKey.toLowerCase();
	if (!hmpMessages.containsKey(strKey)) {
	    throw new NullPointerException("The message '" + strKey + "' does not exist in the configuration.");
	}
	return replacePlaceholdersToParameters(hmpMessages.get(strKey), objParameters).replace("%prefix%", strPrefix);
    }

    public ArrayList<String> getList(String strKey, final Object... objParameters) {
	strKey = strKey.toLowerCase();
	if (!hmpLists.containsKey(strKey)) {
	    throw new NullPointerException("The list '" + strKey + "' does not exist in the configuration.");
	}
	final ArrayList<String> listToReturn = new ArrayList<>();
	for (final String strMessage : hmpLists.get(strKey)) {
	    listToReturn.add(replacePlaceholdersToParameters(strMessage, objParameters));
	}
	return listToReturn;
    }

    public void addMessage(final String strKey, final String strMessage) {
	hmpMessages.put(strKey.toLowerCase(), ChatColor.translateAlternateColorCodes('&', strMessage));
    }

    public void addList(final String strKey, final List<String> lstMessages) {
	final List<String> lstConvertedMessages = new ArrayList<>();
	for (final String strMessage : lstMessages) {
	    lstConvertedMessages.add(ChatColor.translateAlternateColorCodes('&', strMessage));
	}
	hmpLists.put(strKey.toLowerCase(), lstConvertedMessages);
    }

    public static String replacePlaceholdersToParameters(String strMessage, final Object... objParameters) {
	for (int i = 0; i < objParameters.length - 1; i = i + 2) {
	    strMessage = strMessage.replace("%" + objParameters[i] + "%", objParameters[i + 1].toString());
	}
	return strMessage;
    }

    public String getPrefix() {
	return strPrefix;
    }
}
