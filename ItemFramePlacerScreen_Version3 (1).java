package com.insidio.itemframeplacer.gui;

import meteordevelopment.meteorclient.gui.screen.Screen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.WButton;
import meteordevelopment.meteorclient.gui.widgets.WCheckbox;
import meteordevelopment.meteorclient.gui.widgets.containers.WVerticalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.WSlider;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import com.insidio.itemframeplacer.module.ItemFramePlacerModule;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;

public class ItemFramePlacerScreen extends Screen {
    private final ItemFramePlacerModule module;
    private WTextBox searchBox;
    private WVerticalList whitelistContainer;
    private WSlider speedSlider;
    private WTextBox keybindInput;
    private WLabel speedLabel;
    private WLabel keybindLabel;
    private boolean recordingKeybind = false;

    public ItemFramePlacerScreen(Screen parent) {
        super(new LiteralText("ItemFramePlacer Configuration"));
        this.module = null; // Will be set by addon
    }

    public ItemFramePlacerScreen(ItemFramePlacerModule module) {
        super(new LiteralText("ItemFramePlacer Configuration"));
        this.module = module;
    }

    @Override
    public void init() {
        super.init();
        clear();

        // Main container
        WVerticalList main = add(new WVerticalList()).widget();
        main.spacing = 12;

        // Title
        WLabel title = main.add(new WLabel(new LiteralText("ItemFramePlacer Configuration"))).widget();
        title.color = 0xFFFFFF;

        // ===== SPEED SECTION =====
        main.add(createSeparator());
        WVerticalList speedSection = main.add(new WVerticalList()).widget();
        speedSection.spacing = 8;

        WLabel speedTitle = speedSection.add(new WLabel(new LiteralText("⚙ Speed Settings"))).widget();
        speedTitle.color = 0xAAAAAA;

        WVerticalList speedRow = speedSection.add(new WVerticalList()).widget();
        speedRow.spacing = 5;

        speedLabel = speedRow.add(new WLabel(new LiteralText("Placement Speed: " + module.speed.get() + " ticks"))).widget();
        speedLabel.color = 0xDDDDDD;

        speedSlider = speedRow.add(new WSlider(5, 200, module.speed.get())).widget();
        speedSlider.action = this::updateSpeedLabel;
        speedSlider.expandX();

        WLabel speedInfo = speedRow.add(new WLabel(new LiteralText("5 = Fastest | 200 = Slowest"))).widget();
        speedInfo.color = 0x888888;

        // ===== AUTO TOGGLE SECTION =====
        WVerticalList autoToggleSection = speedSection.add(new WVerticalList()).widget();
        autoToggleSection.spacing = 5;

        WCheckbox autoToggleCheckbox = autoToggleSection.add(new WCheckbox(module.autoToggle.get())).widget();
        autoToggleCheckbox.action = () -> {
            module.setAutoToggle(autoToggleCheckbox.checked);
        };

        WLabel autoToggleLabel = autoToggleSection.add(new WLabel(new LiteralText("Auto Toggle: Disable when inventory is empty"))).widget();
        autoToggleLabel.color = 0xCCCCCC;

        // ===== KEYBIND SECTION =====
        main.add(createSeparator());
        WVerticalList keybindSection = main.add(new WVerticalList()).widget();
        keybindSection.spacing = 8;

        WLabel keybindTitle = keybindSection.add(new WLabel(new LiteralText("⌨ Keybind Settings"))).widget();
        keybindTitle.color = 0xAAAAAA;

        WVerticalList keybindRow = keybindSection.add(new WVerticalList()).widget();
        keybindRow.spacing = 5;

        keybindLabel = keybindRow.add(new WLabel(new LiteralText("Toggle Keybind: " + module.getToggleKeybind()))).widget();
        keybindLabel.color = 0xDDDDDD;

        keybindInput = keybindRow.add(new WTextBox(module.getToggleKeybind())).widget();
        keybindInput.setPlaceholder("e.g., X, V, G");
        keybindInput.expandX();

        WButton recordKeybindBtn = keybindRow.add(new WButton(new LiteralText("Record Keybind"))).widget();
        recordKeybindBtn.action = this::toggleRecordingKeybind;
        recordKeybindBtn.expandX();

        WButton applyKeybindBtn = keybindRow.add(new WButton(new LiteralText("Apply Keybind"))).widget();
        applyKeybindBtn.action = this::applyKeybind;
        applyKeybindBtn.expandX();

        WLabel keybindInfo = keybindRow.add(new WLabel(new LiteralText("Press the Record button and then press your desired key"))).widget();
        keybindInfo.color = 0x888888;

        // ===== WHITELIST SECTION =====
        main.add(createSeparator());
        WVerticalList whitelistSection = main.add(new WVerticalList()).widget();
        whitelistSection.spacing = 8;

        WLabel whitelistTitle = whitelistSection.add(new WLabel(new LiteralText("🎨 Whitelist Configuration"))).widget();
        whitelistTitle.color = 0xAAAAAA;

        // Search box
        WVerticalList searchRow = whitelistSection.add(new WVerticalList()).widget();
        searchRow.spacing = 5;

        WLabel searchLabel = searchRow.add(new WLabel(new LiteralText("Search Blocks:"))).widget();
        searchLabel.color = 0xCCCCCC;

        searchBox = searchRow.add(new WTextBox("")).widget();
        searchBox.setPlaceholder("Type to search...");
        searchBox.expandX();
        searchBox.action = this::updateWhitelistDisplay;

        // Buttons
        WVerticalList buttonRow = whitelistSection.add(new WVerticalList()).widget();
        buttonRow.spacing = 5;

        WButton selectAllBtn = buttonRow.add(new WButton(new LiteralText("Select All"))).widget();
        selectAllBtn.action = this::selectAllBlocks;
        selectAllBtn.expandX();

        WButton deselectAllBtn = buttonRow.add(new WButton(new LiteralText("Deselect All"))).widget();
        deselectAllBtn.action = this::deselectAllBlocks;
        deselectAllBtn.expandX();

        // Whitelist items container
        whitelistContainer = whitelistSection.add(new WVerticalList()).widget();
        whitelistContainer.spacing = 3;
        updateWhitelistDisplay();

        // ===== CLOSE SECTION =====
        main.add(createSeparator());
        WButton closeBtn = main.add(new WButton(new LiteralText("Close & Save"))).widget();
        closeBtn.action = this::close;
        closeBtn.expandX();
    }

    private WWidget createSeparator() {
        WVerticalList separator = new WVerticalList();
        separator.spacing = 1;
        return separator;
    }

    private void updateSpeedLabel() {
        int newSpeed = (int) speedSlider.getValue();
        module.setSpeed(newSpeed);
        speedLabel.text = new LiteralText("Placement Speed: " + newSpeed + " ticks");
    }

    private void toggleRecordingKeybind() {
        recordingKeybind = !recordingKeybind;
        if (recordingKeybind) {
            keybindInput.setText("[Press a key...]");
            keybindInput.setEditable(false);
        }
    }

    private void applyKeybind() {
        String keybind = keybindInput.get().toUpperCase();
        if (!keybind.isEmpty() && !keybind.contains("[")) {
            module.setToggleKeybind(keybind);
            keybindLabel.text = new LiteralText("Toggle Keybind: " + keybind);
        }
    }

    private void updateWhitelistDisplay() {
        whitelistContainer.clear();
        String search = searchBox.get().toLowerCase();

        module.getWhitelistSettings().forEach((blockName, setting) -> {
            if (blockName.toLowerCase().contains(search)) {
                WVerticalList itemRow = whitelistContainer.add(new WVerticalList()).widget();
                itemRow.spacing = 0;

                WCheckbox checkbox = itemRow.add(new WCheckbox(setting.get())).widget();
                checkbox.action = () -> {
                    setting.set(checkbox.checked);
                    module.saveSettings();
                };

                WLabel label = itemRow.add(new WLabel(new LiteralText(formatBlockName(blockName)))).widget();
                label.color = 0xDDDDDD;

                itemRow.expandX();
            }
        });
    }

    private void selectAllBlocks() {
        module.getWhitelistSettings().forEach((blockName, setting) -> {
            if (blockName.toLowerCase().contains(searchBox.get().toLowerCase())) {
                setting.set(true);
            }
        });
        module.saveSettings();
        updateWhitelistDisplay();
    }

    private void deselectAllBlocks() {
        module.getWhitelistSettings().forEach((blockName, setting) -> {
            if (blockName.toLowerCase().contains(searchBox.get().toLowerCase())) {
                setting.set(false);
            }
        });
        module.saveSettings();
        updateWhitelistDisplay();
    }

    private String formatBlockName(String block) {
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : block.toCharArray()) {
            if (c == '_') {
                sb.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                sb.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    @Override
    public void close() {
        super.close();
        module.saveSettings();
    }
}