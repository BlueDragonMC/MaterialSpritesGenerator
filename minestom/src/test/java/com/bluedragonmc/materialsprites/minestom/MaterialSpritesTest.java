package com.bluedragonmc.materialsprites.minestom;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ObjectComponent;
import net.kyori.adventure.text.object.SpriteObjectContents;
import net.minestom.server.item.Material;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class MaterialSpritesTest {
	/** The only Minestom materials that have no sprite. */
	private static final TreeSet<String> UNRESOLVABLE = new TreeSet<>(List.of("air"));

	@Test
	void onlyAirHasNoSprite() {
		TreeSet<String> unresolved = new TreeSet<>();
		for (Material material : Material.values()) {
			if (!isResolvable(MaterialSprites.get(material)))
				unresolved.add(material.key().value());
		}
		assertEquals(UNRESOLVABLE, unresolved,
				() -> "Expected only " + UNRESOLVABLE + " to have no sprite, but got " + unresolved);
	}

	@Test
	void airReturnsThePlaceholder() {
		assertEquals(false, isResolvable(MaterialSprites.get(Material.AIR)));
	}

	@Test
	void stoneResolvesToItsBlockSprite() {
		SpriteObjectContents sprite = assertInstanceOf(SpriteObjectContents.class,
				assertInstanceOf(ObjectComponent.class, MaterialSprites.get(Material.STONE)).contents());
		assertEquals(SpriteObjectContents.DEFAULT_ATLAS, sprite.atlas());
		assertEquals(Key.key("minecraft:block/stone"), sprite.sprite());
	}

	@Test
	void chestResolvesToAHead() {
		// Chests use a player head texture, so this exercises the head path.
		assertInstanceOf(ObjectComponent.class, MaterialSprites.get(Material.CHEST));
	}

	/**
	 * A material is resolvable when its sprite is an object component (a sprite or a
	 * player head). Materials with no sprite return the {@code NOTHING} placeholder,
	 * which is a plain text component.
	 */
	private static boolean isResolvable(Component component) {
		return component instanceof ObjectComponent;
	}
}
