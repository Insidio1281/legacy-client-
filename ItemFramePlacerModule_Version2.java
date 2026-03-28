package com.insidio.itemframeplacer.module;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.ModuleCategory;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.Box;

import com.insidio.itemframeplacer.gui.ItemFramePlacerScreen;

import java.util.*;

public class ItemFramePlacerModule extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgWhitelist = settings.createGroup("Whitelist");

    // Speed setting
    public final Setting<Integer> speed = sgGeneral.add(new IntSetting.Builder()
        .name("Speed")
        .description("Delay in ticks between placements (5-200)")
        .default_(20)
        .min(5)
        .max(200)
        .build());

    // Auto toggle setting
    public final Setting<Boolean> autoToggle = sgGeneral.add(new BoolSetting.Builder()
        .name("Auto Toggle")
        .description("Automatically toggle off when no whitelisted items in inventory")
        .default_(true)
        .build());

    // GUI toggle setting
    public final Setting<Boolean> openGui = sgGeneral.add(new BoolSetting.Builder()
        .name("Open GUI")
        .description("Opens the configuration GUI")
        .default_(false)
        .build());

    // Keybind setting
    public final Setting<String> toggleKeybind = sgGeneral.add(new StringSetting.Builder()
        .name("Toggle Keybind")
        .description("Keybind to toggle the module on/off (e.g., X, V, G)")
        .default_("X")
        .build());

    private final Map<String, Setting<Boolean>> whitelistSettings = new HashMap<>();
    private int tickCounter = 0;
    private List<ItemFrameEntity> detectedFrames = new ArrayList<>();

    public ItemFramePlacerModule() {
        super(ModuleCategory.Misc, "ItemFramePlacer", "Automatically places whitelisted blocks in item frames");
    }

    @Override
    public void onActivate() {
        super.onActivate();
        initializeWhitelistSettings();
    }

    private void initializeWhitelistSettings() {
        String[] commonBlocks = {
            "dirt", "grass_block", "stone", "cobblestone", "oak_log", "oak_leaves",
            "sand", "gravel", "oak_planks", "spruce_planks", "birch_planks", "jungle_planks",
            "acacia_planks", "dark_oak_planks", "oak_sapling", "spruce_sapling", "birch_sapling",
            "jungle_sapling", "acacia_sapling", "dark_oak_sapling", "redstone_ore", "snow",
            "ice", "snow_block", "cactus", "clay", "sugar_cane", "oak_fence", "spruce_fence",
            "birch_fence", "jungle_fence", "acacia_fence", "dark_oak_fence", "iron_block",
            "gold_block", "diamond_block", "emerald_block", "lapis_block", "tuff", "deepslate"
        };

        for (String block : commonBlocks) {
            if (!whitelistSettings.containsKey(block)) {
                Setting<Boolean> setting = sgWhitelist.add(new BoolSetting.Builder()
                    .name(formatBlockName(block))
                    .description(block)
                    .default_(false)
                    .build());
                whitelistSettings.put(block, setting);
            }
        }
    }

    private String formatBlockName(String block) {
        return Arrays.stream(block.split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .reduce((a, b) -> a + " " + b)
            .orElse(block);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        // Check if GUI should open
        if (openGui.get()) {
            openGui.set(false);
            mc.setScreen(new ItemFramePlacerScreen(mc.currentScreen));
        }

        tickCounter++;
        if (tickCounter < speed.get()) return;
        tickCounter = 0;

        detectedFrames.clear();
        detectItemFrames();

        if (detectedFrames.isEmpty()) return;

        // Check if we have whitelisted items
        if (!hasWhitelistedItems()) {
            if (autoToggle.get()) {
                toggle();
            }
            return;
        }

        // Place items in frames
        for (ItemFrameEntity frame : detectedFrames) {
            if (frame.getHeldItemStack().isEmpty()) {
                placeItemInFrame(frame);
            } else {
                removeItemFromFrame(frame);
            }
        }
    }

    private void detectItemFrames() {
        double range = 36;
        Box searchBox = mc.player.getBoundingBox().expand(range);
        
        List<ItemFrameEntity> frames = mc.world.getEntitiesByClass(
            ItemFrameEntity.class,
            searchBox,
            frame -> true
        );

        detectedFrames.addAll(frames);
    }

    private boolean hasWhitelistedItems() {
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isWhitelistedBlock(stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean isWhitelistedBlock(ItemStack stack) {
        if (!(stack.getItem() instanceof BlockItem)) return false;
        
        String itemName = stack.getItem().getTranslationKey()
            .replace("block.", "")
            .replace("minecraft.", "")
            .replace(".", "_");
        
        Setting<Boolean> setting = whitelistSettings.get(itemName);
        return setting != null && setting.get();
    }

    private void placeItemInFrame(ItemFrameEntity frame) {
        ItemStack whitelistedItem = findWhitelistedItem();
        if (whitelistedItem.isEmpty()) return;

        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(
                new PlayerInteractEntityC2SPacket(frame, false)
            );
        }
    }

    private void removeItemFromFrame(ItemFrameEntity frame) {
        if (mc.getNetworkHandler() != null) {
            mc.getNetworkHandler().sendPacket(
                new PlayerInteractEntityC2SPacket(frame, false)
            );
        }
    }

    private ItemStack findWhitelistedItem() {
        for (int i = 0; i < mc.player.getInventory().size(); i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (isWhitelistedBlock(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public Map<String, Setting<Boolean>> getWhitelistSettings() {
        return whitelistSettings;
    }

    public void saveSettings() {
        // Settings are automatically saved by Meteor Client
    }

    public int getSpeed() {
        return speed.get();
    }

    public void setSpeed(int newSpeed) {
        speed.set(newSpeed);
    }

    public boolean isAutoToggleEnabled() {
        return autoToggle.get();
    }

    public void setAutoToggle(boolean enabled) {
        autoToggle.set(enabled);
    }

    public String getToggleKeybind() {
        return toggleKeybind.get();
    }

    public void setToggleKeybind(String keybind) {
        toggleKeybind.set(keybind);
    }
}