package org.riking.cauldronessentialstweaks;

import com.earth2me.essentials.ItemDb;
import com.earth2me.essentials.utils.StringUtil;
import net.ess3.api.IEssentials;
import net.minecraftforge.cauldron.api.inventory.BukkitOreDictionary;
import net.minecraftforge.cauldron.api.inventory.OreDictionaryEntry;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReplacementItemDb extends ItemDb {
    private final transient BukkitOreDictionary dictionary;
    private final transient Pattern splitPattern = Pattern.compile("((.*)[:+',;.](\\d+))");

    public ReplacementItemDb(IEssentials ess, BukkitOreDictionary dictionary) {
        super(ess);
        this.dictionary = dictionary;
    }

    @Override
    public ItemStack get(final String id) throws Exception {
        String itemName;
        short itemDamage = 0;

        Matcher match = splitPattern.matcher(id);

        if (match.matches()) {
            itemName = match.group(2);
            itemDamage = Short.parseShort(match.group(3));
        } else {
            itemName = id;
        }

        OreDictionaryEntry entry = dictionary.getOreEntry(itemName);
        if (entry == null) {
            return super.get(id);
        }

        List<ItemStack> definitions = dictionary.getDefinitions(entry);
        if (definitions.size() == 0) {
            return super.get(id);
        }

        ItemStack stack = definitions.get(0);
        short providedDurability = stack.getDurability();
        if (providedDurability == 0 || providedDurability == -1 || providedDurability == Short.MAX_VALUE) {
            stack.setDurability(itemDamage);
        }

        return stack;
    }

    @Override
    public String names(ItemStack item) {
        List<String> nameList = new ArrayList<String>();
        String vanilla = super.names(item);
        if (vanilla != null) {
            Collections.addAll(nameList, vanilla.split(", "));
        }

        List<OreDictionaryEntry> entries = dictionary.getOreEntries(item);
        for (OreDictionaryEntry entry : entries) {
            String name = dictionary.getOreName(entry);
            if (name != null && !name.isEmpty()) {
                nameList.add(name);
            }
        }

        if (nameList.isEmpty()) {
            return null;
        }

        return StringUtil.joinList(", ", nameList);
    }

    @Override
    public String name(ItemStack item) {
        String vanilla = super.name(item);
        if (vanilla != null) {
            return vanilla;
        }

        List<OreDictionaryEntry> entries = dictionary.getOreEntries(item);
        OreDictionaryEntry best = null;
        int bestCount = Integer.MAX_VALUE;
        for (OreDictionaryEntry entry : entries) {
            List<ItemStack> definitions = dictionary.getDefinitions(entry);
            if (definitions.size() < bestCount) {
                best = entry;
                bestCount = definitions.size();
            }
        }

        if (best == null) {
            return null;
        }

        return dictionary.getOreName(best);
    }
}
