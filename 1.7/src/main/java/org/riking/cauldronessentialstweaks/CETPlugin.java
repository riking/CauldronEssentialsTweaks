package org.riking.cauldronessentialstweaks;

import net.ess3.api.IEssentials;
import net.minecraftforge.cauldron.api.Cauldron;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class CETPlugin extends JavaPlugin {
    private IEssentials essentialsPlugin;

    @Override
    public void onEnable() {
        essentialsPlugin = (IEssentials) getServer().getPluginManager().getPlugin("Essentials");
        try {

            setupOredict();
        } catch (Throwable t) {
            t.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    @Override
    public void onDisable() {

    }

    private void setupCommandOverrides() {

    }

    private void setupOredict() throws Throwable {
        Field itemDbField = essentialsPlugin.getClass().getDeclaredField("itemDb");
        ReplacementItemDb replacement = new ReplacementItemDb(essentialsPlugin, Cauldron.getOreDictionary());
        itemDbField.setAccessible(true);
        itemDbField.set(essentialsPlugin, replacement);
    }
}
