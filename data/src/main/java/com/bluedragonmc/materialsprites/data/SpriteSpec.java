package com.bluedragonmc.materialsprites.data;

/**
 * A platform-independent description of the sprite used to represent a material.
 * <p>
 * Material names are only known as strings here; the platform-specific modules
 * ({@code material-sprites-paper} and {@code material-sprites-minestom}) resolve
 * them to their own {@code Material} types.
 */
public sealed interface SpriteSpec {
	/** An invisible placeholder, used for materials that have no sprite. */
	record Nothing() implements SpriteSpec {}

	/**
	 * A sprite object component.
	 *
	 * @param atlas the atlas key, or {@code null} for the default (blocks) atlas
	 * @param key   the sprite key
	 * @param color the tint color as an RGB integer, or {@code null} for white
	 */
	record Sprite(String atlas, String key, Integer color) implements SpriteSpec {}

	/**
	 * A player head using a texture from {@code textures.minecraft.net}.
	 *
	 * @param texture the texture hash
	 * @param hat     whether the head should render its hat layer
	 */
	record Head(String texture, boolean hat) implements SpriteSpec {}
}
