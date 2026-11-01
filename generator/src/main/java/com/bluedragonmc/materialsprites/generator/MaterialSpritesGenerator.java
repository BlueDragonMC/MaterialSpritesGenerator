package com.bluedragonmc.materialsprites.generator;

import com.bluedragonmc.materialsprites.generator.rules.MappingRule;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static com.bluedragonmc.materialsprites.generator.rules.MappingRule.rule;

public final class MaterialSpritesGenerator {
	/** Atlas name to the sprite keys it contains. */
	public static final Map<String, List<String>> ATLASES = new HashMap<>();

	private static final String DEFAULT_OUTPUT = "material-sprites.json";
	private static final String MATERIALS_RESOURCE = "/materials.txt";
	private static final String ATLAS_INDEX_RESOURCE = "/atlases.txt";
	private static final String ATLAS_RESOURCE_DIRECTORY = "/atlases/";

	private static final String MINECRAFT = "minecraft:";
	private static final String ITEM = MINECRAFT + "item/";
	private static final String BLOCK = MINECRAFT + "block/";

	private static final String ITEMS_ATLAS = "items";

	private MaterialSpritesGenerator() {}

	public static void main(String[] args) throws IOException {
		Path output = Paths.get(args.length > 0 ? args[0] : DEFAULT_OUTPUT);

		for (String atlas : readLines(ATLAS_INDEX_RESOURCE)) {
			ATLASES.put(atlas, readAtlas(atlas));
		}

		TreeSet<String> materials = new TreeSet<>(readLines(MATERIALS_RESOURCE));
		if (materials.isEmpty())
			throw new IOException("No materials listed in " + MATERIALS_RESOURCE + "; run 'mise run generate' first");

		Map<String, JsonObject> data = new LinkedHashMap<>();
		List<String> unmapped = new ArrayList<>();
		for (String material : materials) {
			for (MappingRule rule : RULES) {
				if (rule.matches(material)) {
					data.put(material, rule.componentData(material));
					break;
				}
			}
			if (!data.containsKey(material))
				unmapped.add(material);
		}

		Path parent = output.toAbsolutePath().getParent();
		if (parent != null)
			Files.createDirectories(parent);
		Files.writeString(output, new GsonBuilder().setPrettyPrinting().create().toJson(data) + "\n");

		for (String material : unmapped)
			System.err.println("No sprite found for material: " + material);

		System.out.println("Materials found: " + materials.size());
		System.out.println("Sprites found: " + data.size());
		System.out.println("Sprites not found: " + unmapped.size());
		System.out.println("Mappings written to " + output.toAbsolutePath());
	}

	private static List<String> readAtlas(String atlas) throws IOException {
		return readLines(ATLAS_RESOURCE_DIRECTORY + atlas + ".txt").stream()
				.map(line -> line.split("\t")[0].trim())
				.filter(line -> !line.isEmpty())
				.toList();
	}

	private static List<String> readLines(String resource) throws IOException {
		try (InputStream stream = MaterialSpritesGenerator.class.getResourceAsStream(resource)) {
			if (stream == null)
				throw new IOException("Missing generated resource " + resource + "; run 'mise run generate' first");

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
				return reader.lines()
						.map(String::trim)
						.filter(line -> !line.isEmpty() && !line.startsWith("#"))
						.toList();
			}
		}
	}

	private static final List<MappingRule> RULES = List.of(
			//<editor-fold desc="Mapping rules">
			rule().suffix("air").overwrite("NOTHING").code(),

			rule().exact("heavy_core").overwrite("5069a0f8bdd8c08693e60b93bdfe800abdf96eb1dc9e7efc38987311da4e0f34").head(),
			rule().exact("dried_ghast").overwrite("4650955792174fc48143643f2d9ddcedaabc822d651631a81c9f014a0c6181f7").head(),
			rule().either("end_gateway", "end_portal").overwrite("5639d4079d6b7c0a913cff608ec43fa39a72fca5d64fb22700b1cc6c46cc69c2").head(),
			rule().exact("chest").overwrite("758305e527fcf08525fdff07e4cd6e79774acdd426cc27327d45df8a5ff86419").head(),
			rule().exact("trapped_chest").overwrite("275bcff2e74deed37a319a1f404e70d06a5f360cacee99c71346f38560cbd72a").head(),
			rule().exact("ender_chest").overwrite("87acb8b99d478ba35053e9f212acb5c55cc144840468fd0242b39f5bd75acb41").head(),
			rule().suffix("oxidized_copper_chest").overwrite("33af3bfdf486eeff78c49da400907b45a4c4db4d6fb159909a3217e069c640b1").head(),
			rule().suffix("weathered_copper_chest").overwrite("7b4b972335ae434b004ec7ad2e3706c757c14f210b2b1132d08e384ea722e45b").head(),
			rule().suffix("exposed_copper_chest").overwrite("5c092ddd68df26a30afbab8247a581cb981fb3ec8114ab95519e15083c480139").head(),
			rule().suffix("copper_chest").overwrite("2a7848dfe597995d04e822ac53878a29ed81e1f152848f1823cb3529c47e1cef").head(),
			rule().suffix("oxidized_copper_golem_statue").overwrite("d1e2791f850af92271763f32e3d440573952135c73083715665d7adcff9b06c9").head(),
			rule().suffix("weathered_copper_golem_statue").overwrite("ff401456e0b4da388e6f21a8683608501ecb4988af76dc4c4ace5c92fe34e9f1").head(),
			rule().suffix("exposed_copper_golem_statue").overwrite("ad8cf824534a3e63cb2278c3a6ec5b3b17d7c8e178ac67d793e63d0eea9b048c").head(),
			rule().suffix("copper_golem_statue").overwrite("d998651718b318a2ca6a4a21ed201f0c65a14d0fcde7dd044bad33116cd5e026").head(),

			rule().regex("creeper_(wall_)?head").overwrite("ba5e95735a3f3772b1b485e1502807ae396a72c61bfd36ab41fa71bec2f64aa2").head(),
			rule().regex("dragon_(wall_)?head").overwrite("f2191029c8ccd1ebd95207720ceae6944e5e20d8848bdf3d67e0d0e1101b78a1").head(),
			rule().regex("piglin_(wall_)?head").overwrite("90bc9dbb4404b800f8cf0256220ff74b0b71dba8b66600b6734f4d63361618f5").head(),
			rule().regex("player_(wall_)?head").overwrite("d5c4ee5ce20aed9e33e866c66caa37178606234b3721084bf01d13320fb2eb3f").head(),
			rule().regex("skeleton_(wall_)?skull").overwrite("22795c3c6f36d67decf9a3195e128040bec5226b055f2b16d46fa19a9180e023").head(),
			rule().regex("wither_skeleton_(wall_)?skull").overwrite("ba96e9d76bed30090ce6e2d8425996594eec6d68ac88cf07356e9814834243ec").head(),
			rule().regex("zombie_(wall_)?head").overwrite("d97e4259379a06f24843c1bb42f2df35c13f801ad079f715bded488db8f57c3").head(),

			rule().exact("water").prepend(BLOCK).append("_still").sprite().color(0x3F76E4),
			rule().exact("lava").prepend(BLOCK).append("_still").sprite(),
			rule().exact("tripwire").overwrite(ITEM + "string").sprite().atlas(ITEMS_ATLAS),
			rule().exact("light").prepend(ITEM).append("_15").sprite().atlas(ITEMS_ATLAS),
			rule().either("fire", "soul_fire", "suspicious_gravel", "suspicious_sand").prepend(BLOCK).append("_0").sprite(),
			rule().suffix("anvil").prepend(BLOCK).append("_top").sprite(),
			rule().suffix("_dripleaf").prepend(BLOCK).append("_top").sprite(),
			rule().either("daylight_detector", "scaffolding", "lilac", "rose_bush", "peony", "tall_seagrass", "cartography_table").prepend(BLOCK).append("_top").sprite(),
			rule().exact("grindstone").prepend(BLOCK).append("_round").sprite(),
			rule().exact("crafter").prepend(BLOCK).append("_north").sprite(),
			rule().exact("wheat").prepend(BLOCK).append("_stage7").sprite(),
			rule().exact("torchflower_crop").prepend(BLOCK).append("_stage1").sprite(),
			rule().exact("frosted_ice").prepend(BLOCK).append("_3").sprite(),
			rule().exact("trial_spawner").prepend(BLOCK).append("_side_inactive").sprite(),
			rule().exact("vault").prepend(BLOCK).append("_front_off").sprite(),
			rule().exact("test_block").prepend(BLOCK).append("_start").sprite(),
			rule().exact("cocoa").prepend(ITEM).append("_beans").sprite().atlas(ITEMS_ATLAS),
			rule().exact("crossbow").prepend(ITEM).append("_standby").sprite().atlas(ITEMS_ATLAS),
			rule().exact("chiseled_bookshelf").prepend(BLOCK).append("_occupied").sprite(),
			rule().exact("calibrated_sculk_sensor").prepend(BLOCK).append("_input_side").sprite(),
			rule().regex("smooth_quartz(_slab|_stairs)?").overwrite(BLOCK + "quartz_block_bottom").sprite(),
			rule().regex("smooth_sandstone(_slab|_stairs)?").overwrite(BLOCK + "sandstone_top").sprite(),
			rule().regex("smooth_red_sandstone(_slab|_stairs)?").overwrite(BLOCK + "red_sandstone_top").sprite(),
			rule().exact("heavy_weighted_pressure_plate").overwrite(BLOCK + "iron_block").sprite(),
			rule().exact("light_weighted_pressure_plate").overwrite(BLOCK + "gold_block").sprite(),
			rule().exact("potted_cactus").overwrite(BLOCK + "cactus_side").sprite(),
			rule().exact("nether_brick_fence").overwrite(BLOCK + "nether_bricks").sprite(),
			rule().exact("moss_carpet").overwrite(BLOCK + "moss_block").sprite(),
			rule().either("piston_head", "moving_piston").overwrite(BLOCK + "piston_top").sprite(),
			rule().exact("sticky_piston").overwrite(BLOCK + "piston_top_sticky").sprite(),
			rule().exact("petrified_oak_slab").overwrite(BLOCK + "oak_planks").sprite(),
			rule().exact("debug_stick").overwrite(ITEM + "stick").sprite().atlas(ITEMS_ATLAS),
			rule().exact("enchanted_golden_apple").overwrite(ITEM + "golden_apple").sprite().atlas(ITEMS_ATLAS),
			rule().exact("tipped_arrow").overwrite(ITEM + "arrow").sprite().atlas(ITEMS_ATLAS),
			rule().exact("bamboo_sapling").overwrite(BLOCK + "bamboo_stage0").sprite(),
			rule().exact("shelf_mushroom").prepend(BLOCK).append("_stage0").sprite(),
			rule().exact("dried_kelp_block").overwrite(BLOCK + "dried_kelp_side").sprite(),
			rule().exact("lectern").overwrite(BLOCK + "bookshelf").sprite(),
			rule().either("lava_cauldron", "powder_snow_cauldron", "water_cauldron").overwrite(ITEM + "cauldron").sprite().atlas(ITEMS_ATLAS),
			rule().exact("decorated_pot").overwrite(MINECRAFT + "entity/decorated_pot/danger_pottery_pattern").sprite().atlas("decorated_pot"),
			rule().exact("bubble_column").overwrite(MINECRAFT + "bubble").sprite().atlas("particles"),
			rule().exact("shield").prepend(MINECRAFT + "container/slot/").sprite().atlas("gui"),

			rule().exact("redstone_wire").overwrite(BLOCK + "redstone_dust_line0").sprite().color(0xAA0000),
			rule().exact("vine").prepend(BLOCK).sprite().color(0x48b518),
			rule().exact("lily_pad").prepend(BLOCK).sprite().color(0x71c35c),
			rule().either("short_grass", "fern", "bush").prepend(BLOCK).sprite().color(0x91bd59),
			rule().either("tall_grass", "large_fern").prepend(BLOCK).append("_top").sprite().color(0x91bd59),
			rule().exact("potted_fern").overwrite(BLOCK + "fern").sprite().color(0x91bd59),
			rule().regex("(attached_)?(melon|pumpkin)_stem").prepend(BLOCK).sprite().color(0xa9960a),
			rule().regex("((dark_)?oak|jungle|acacia)_leaves").prepend(BLOCK).sprite().color(0x48b518),
			rule().exact("mangrove_leaves").prepend(BLOCK).sprite().color(0x92c648),
			rule().exact("spruce_leaves").prepend(BLOCK).sprite().color(0x619961),
			rule().exact("birch_leaves").prepend(BLOCK).sprite().color(0x80a755),
			rule().prefix("leather_").prepend(ITEM).sprite().atlas(ITEMS_ATLAS).color(0xA06540),

			rule().suffix("_fence", true).prepend(BLOCK).append("_planks").sprite(),
			rule().suffix("_shelf", true).prepend(BLOCK).append("_planks").sprite(),

			rule().prepend(ITEM).sprite().atlas(ITEMS_ATLAS),
			rule().prepend(ITEM).append("_00").sprite().atlas(ITEMS_ATLAS),
			rule().prepend(BLOCK).sprite(),
			rule().prepend(BLOCK).append("_front").sprite(),
			rule().prepend(BLOCK).append("_side").sprite(),
			rule().prepend(BLOCK).append("_side0").sprite(),
			rule().prepend(BLOCK).append("_stage3").sprite(),

			rule().prefix("waxed_", true).prepend(ITEM).sprite().atlas(ITEMS_ATLAS),
			rule().prefix("waxed_", true).prepend(BLOCK).sprite(),
			rule().prefix("potted_", true).prepend(ITEM).sprite().atlas(ITEMS_ATLAS),
			rule().prefix("potted_", true).prepend(BLOCK).sprite(),
			rule().prefix("infested_", true).prepend(BLOCK).sprite(),

			rule().suffix("_block", true).prepend(BLOCK).sprite(),
			rule().suffix("_wall", true).prepend(BLOCK).append("s").sprite(),
			rule().suffix("_wall", true).prepend(BLOCK).sprite(),
			rule().suffix("_slab", true).prepend(BLOCK).multi(rule -> List.of(
					rule.append("_planks").sprite(),
					rule.append("_block").sprite(),
					rule.append("_block_side").sprite(),
					rule.append("s").sprite(),
					rule.sprite()
			)),
			rule().suffix("_stairs", true).prepend(BLOCK).multi(rule -> List.of(
					rule.append("_planks").sprite(),
					rule.append("_block").sprite(),
					rule.append("_block_side").sprite(),
					rule.append("s").sprite(),
					rule.sprite()
			)),
			rule().suffix("_pressure_plate", true).prepend(BLOCK).append("_planks").sprite(),
			rule().suffix("_pressure_plate", true).prepend(BLOCK).sprite(),
			rule().suffix("_button", true).prepend(BLOCK).append("_planks").sprite(),
			rule().suffix("_button", true).prepend(BLOCK).sprite(),
			rule().suffix("_fence_gate", true).prepend(BLOCK).append("_planks").sprite(),
			rule().suffix("_wood", true).prepend(BLOCK).append("_log").sprite(),
			rule().suffix("_hyphae", true).prepend(BLOCK).append("_stem").sprite(),
			rule().suffix("_cake", true).prepend(ITEM).sprite().atlas(ITEMS_ATLAS),
			rule().suffix("_pane", true).prepend(BLOCK).sprite(),
			rule().suffix("_carpet", true).prepend(BLOCK).append("_wool").sprite(),
			rule().suffix("_bed", true).prepend(BLOCK).append("_wool").sprite(),

			rule().suffix("wall_torch", true).prepend(BLOCK).append("torch").sprite(),
			rule().suffix("_wall_sign", true).prepend(ITEM).append("_sign").sprite().atlas(ITEMS_ATLAS),
			rule().suffix("_wall_hanging_sign", true).prepend(ITEM).append("_hanging_sign").sprite().atlas(ITEMS_ATLAS),
			rule().suffix("_wall_fan", true).prepend(BLOCK).append("_fan").sprite(),

			rule().prefix("waxed_", true).suffix("_slab", true).prepend(BLOCK).sprite(),
			rule().prefix("waxed_", true).suffix("_stairs", true).prepend(BLOCK).sprite(),

			rule().suffix("_wall_banner", true).prepend(MINECRAFT).append("_banner").sprite().atlas("map_decorations"),
			rule().suffix("_banner").prepend(MINECRAFT).sprite().atlas("map_decorations")
			//</editor-fold>
	);
}
