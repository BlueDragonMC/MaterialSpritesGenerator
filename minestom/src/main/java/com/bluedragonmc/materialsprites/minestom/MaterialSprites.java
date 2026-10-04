package com.bluedragonmc.materialsprites.minestom;

import com.bluedragonmc.materialsprites.data.MaterialSpriteData;
import com.bluedragonmc.materialsprites.data.SpriteSpec;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import net.minestom.server.item.Material;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.ResolvableProfile;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A Minestom {@link Material} to sprite {@link Component} mapping, backed by the
 * platform-independent {@code material-sprites-data} artifact.
 */
public final class MaterialSprites {
	private static final Component NOTHING = Component.text("  ");
	private static final Map<Material, Component> CACHE = new ConcurrentHashMap<>();

	private MaterialSprites() {}

	/** Returns the sprite component for a material, or an invisible placeholder if there is none. */
	public static Component get(Material material) {
		if (material == null)
			return NOTHING;
		return CACHE.computeIfAbsent(material, value -> build(MaterialSpriteData.get(value.key().value())));
	}

	private static Component build(SpriteSpec spec) {
		if (spec == null)
			return NOTHING;
		return switch (spec) {
			case SpriteSpec.Nothing ignored -> NOTHING;
			case SpriteSpec.Sprite sprite -> sprite(sprite);
			case SpriteSpec.Head head -> head(head);
		};
	}

	private static Component sprite(SpriteSpec.Sprite sprite) {
		Key atlas = sprite.atlas() == null ? SpriteObjectContents.DEFAULT_ATLAS : Key.key(sprite.atlas());
		Component component = Component.object(ObjectContents.sprite(atlas, Key.key(sprite.key())));
		return component.color(sprite.color() == null ? NamedTextColor.WHITE : TextColor.color(sprite.color()));
	}

	private static Component head(SpriteSpec.Head head) {
		ResolvableProfile profile = new ResolvableProfile(new ResolvableProfile.Partial(
				null, null, List.of(new GameProfile.Property("textures", Base64.getEncoder().encodeToString(
						("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + head.texture() + "\"}}}").getBytes()
				)))));
		return Component.object(ObjectContents.playerHead().skin(profile).hat(head.hat()).build())
				.color(NamedTextColor.WHITE);
	}
}
