package com.insidio.itemframeplacer;

import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.orbit.EventManager;
import com.insidio.itemframeplacer.module.ItemFramePlacerModule;
import com.insidio.itemframeplacer.util.KeyBindHandler;

public class ItemFramePlacer extends MeteorAddon {
    private static ItemFramePlacerModule itemFramePlacerModule;
    private static KeyBindHandler keyBindHandler;

    @Override
    public void onInitialize() {
        itemFramePlacerModule = new ItemFramePlacerModule();
        Modules.register(itemFramePlacerModule);
        
        keyBindHandler = new KeyBindHandler(itemFramePlacerModule);
        EventManager.subscribe(keyBindHandler);
    }

    @Override
    public String getPackage() {
        return "com.insidio.itemframeplacer";
    }
}