package fr.theoszanto.mc.generated; // Package name can be overridden in config.yml

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.KeyPattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.object.ObjectContents;
import net.kyori.adventure.text.object.SpriteObjectContents;
import org.bukkit.Material;

import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;

// This file has been auto-generated and should not be modified directly.
// If changes are needed, please modify the source template instead!
public class MaterialSprites {
	private static final Component NOTHING = Component.text("  ");

	private static final Map<Material, Component> SPRITES = new EnumMap<>(Material.class);
	static {
		//<editor-fold desc="Mappings" defaultstate="collapsed">
		// GENERATE HERE - Mappings will replace this line
		//</editor-fold>
	}

	public static Component get(Material material) {
		return SPRITES.getOrDefault(material, NOTHING);
	}

	private static Component sprite(@KeyPattern String key) {
		return sprite(SpriteObjectContents.DEFAULT_ATLAS, Key.key(key), NamedTextColor.WHITE);
	}

	private static Component sprite(@KeyPattern String key, int color) {
		return sprite(SpriteObjectContents.DEFAULT_ATLAS, Key.key(key), TextColor.color(color));
	}

	private static Component sprite(@KeyPattern String atlas, @KeyPattern String key) {
		return sprite(Key.key(atlas), Key.key(key), NamedTextColor.WHITE);
	}

	private static Component sprite(@KeyPattern String atlas, @KeyPattern String key, int color) {
		return sprite(Key.key(atlas), Key.key(key), TextColor.color(color));
	}

	private static Component sprite(Key atlas, Key sprite, TextColor color) {
		return Component.object(ObjectContents.sprite(atlas, sprite)).color(color);
	}

	private static Component head(String textures) {
		return head(textures, true);
	}

	@SuppressWarnings("UnstableApiUsage") // ResolvableProfile
	private static Component head(String textures, boolean hat) {
		return Component.object(ObjectContents.playerHead().skin(ResolvableProfile.resolvableProfile()
				.addProperty(new ProfileProperty("textures", Base64.getEncoder().encodeToString(
						("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + textures + "\"}}}").getBytes()
				)))
				.build()).hat(hat).build()).color(NamedTextColor.WHITE);
	}
}
