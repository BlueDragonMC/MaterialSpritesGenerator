package com.bluedragonmc.materialsprites.generator;

import com.bluedragonmc.materialsprites.generator.rules.HeadMappingRule;
import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SpritesCommand implements CommandExecutor {
	@SuppressWarnings("UnstableApiUsage")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length != 0) {
			sender.sendMessage(HeadMappingRule.head(args[0], args.length > 1 && Boolean.parseBoolean(args[1])));
			return true;
		}

		sender.showDialog(Dialog.create(dialog -> dialog.empty()
				.base(DialogBase.create(
						Component.text("Material sprites list"),
						null,
						true,
						false,
						DialogBase.DialogAfterAction.CLOSE,
						MaterialSpritesGenerator.SPRITES.entrySet().stream()
								.map(entry -> {
									Material material = entry.getKey();
									return DialogBody.plainMessage(Component.text()
											.append(entry.getValue().hoverEvent(material.isItem() && !material.isAir() ? ItemStack.of(material) : null))
											.appendSpace()
											.append(Component.translatable(material).hoverEvent(Component.text(material.name())))
											.build());
								})
								.toList(),
						List.of()
				))
				.type(DialogType.notice())));
		return true;
	}
}
