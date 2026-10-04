package com.bluedragonmc.materialsprites.atlas;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Downloads a vanilla Minecraft client jar and regenerates the generator's
 * atlas sprite lists, which are used to validate that a generated sprite key
 * actually exists in a given atlas.
 *
 * Each atlas text file is a plain list of sprite keys, one per line.
 *
 * Usage: {@code java -jar atlas-generator.jar <minecraft version> [project root]}
 */
public final class GenerateAtlases {
	private static final String VERSION_MANIFEST =
			"https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
	private static final String TEXTURE_PREFIX = "assets/minecraft/textures/";
	private static final String ATLAS_PREFIX = "assets/minecraft/atlases/";

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: java -jar atlas-generator.jar <minecraft version> [project root]");
			System.exit(2);
		}
		String version = args[0];
		Path root = Paths.get(args.length > 1 ? args[1] : ".");
		Path config = root.resolve("plugin/src/main/resources/config.yml");
		Path outputDir = root.resolve("plugin/src/main/resources/atlases");
		List<String> atlases = readAtlasNames(config);

		HttpClient http = HttpClient.newBuilder()
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();

		Path clientJar = downloadClient(http, version, root.resolve("build/minecraft"));
		System.out.println("Client jar: " + clientJar.toAbsolutePath());

		Files.createDirectories(outputDir);
		try (ZipFile zip = new ZipFile(clientJar.toFile())) {
			TreeSet<String> textures = collectTextures(zip);
			for (String atlas : atlases) {
				Set<String> sprites = resolveAtlas(zip, atlas, textures);
				Path output = outputDir.resolve(atlas + ".txt");
				Files.write(output, String.join("\n", sprites).concat("\n").getBytes(StandardCharsets.UTF_8));
				System.out.println("Wrote " + sprites.size() + " sprites to " + output);
			}
		}
	}

	/** Reads the {@code atlases} list from the generator configuration. */
	private static List<String> readAtlasNames(Path config) throws IOException {
		List<String> names = new ArrayList<>();
		boolean inAtlases = false;
		for (String line : Files.readAllLines(config, StandardCharsets.UTF_8)) {
			if (line.startsWith("atlases:")) {
				inAtlases = true;
				continue;
			}
			if (!inAtlases)
				continue;
			String trimmed = line.trim();
			if (trimmed.startsWith("- "))
				names.add(trimmed.substring(2).trim());
			else if (!trimmed.isEmpty() && !trimmed.startsWith("#"))
				break;
		}
		if (names.isEmpty())
			throw new IOException("No atlases found in " + config.toAbsolutePath());
		return names;
	}

	private static Path downloadClient(HttpClient http, String version, Path cacheDir) throws Exception {
		JsonObject manifest = JsonParser.parseString(get(http, VERSION_MANIFEST)).getAsJsonObject();
		String versionUrl = null;
		for (JsonElement entry : manifest.getAsJsonArray("versions")) {
			JsonObject candidate = entry.getAsJsonObject();
			if (version.equals(candidate.get("id").getAsString())) {
				versionUrl = candidate.get("url").getAsString();
				break;
			}
		}
		if (versionUrl == null)
			throw new IllegalArgumentException("Unknown Minecraft version: " + version);

		String clientUrl = JsonParser.parseString(get(http, versionUrl)).getAsJsonObject()
				.getAsJsonObject("downloads").getAsJsonObject("client").get("url").getAsString();

		Path cache = cacheDir.resolve("client-" + version + ".jar");
		if (isValidZip(cache)) {
			System.out.println("Using cached client jar");
			return cache;
		}
		Files.createDirectories(cacheDir);
		Files.deleteIfExists(cache);
		System.out.println("Downloading client jar from " + clientUrl);
		HttpResponse<Path> response = http.send(
				HttpRequest.newBuilder(URI.create(clientUrl)).GET().build(),
				HttpResponse.BodyHandlers.ofFile(cache));
		if (response.statusCode() != 200)
			throw new IOException("HTTP " + response.statusCode() + " downloading client jar");
		return cache;
	}

	private static boolean isValidZip(Path path) {
		try {
			if (!Files.isRegularFile(path) || Files.size(path) == 0)
				return false;
			try (ZipFile ignored = new ZipFile(path.toFile())) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
	}

	private static String get(HttpClient http, String url) throws IOException, InterruptedException {
		HttpResponse<String> response = http.send(
				HttpRequest.newBuilder(URI.create(url)).GET().build(),
				HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() != 200)
			throw new IOException("HTTP " + response.statusCode() + " for " + url);
		return response.body();
	}

	/** Collects every texture path (without namespace or extension) in the client jar. */
	private static TreeSet<String> collectTextures(ZipFile zip) {
		TreeSet<String> textures = new TreeSet<>();
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			String name = entries.nextElement().getName();
			if (name.startsWith(TEXTURE_PREFIX) && name.endsWith(".png"))
				textures.add(name.substring(TEXTURE_PREFIX.length(), name.length() - ".png".length()));
		}
		return textures;
	}

	/** Resolves all sprite keys produced by an atlas definition. */
	private static Set<String> resolveAtlas(ZipFile zip, String atlas, TreeSet<String> textures) throws IOException {
		ZipEntry entry = zip.getEntry(ATLAS_PREFIX + atlas + ".json");
		if (entry == null)
			throw new IOException("No atlas definition for " + atlas);
		JsonObject json = JsonParser.parseString(
				new String(zip.getInputStream(entry).readAllBytes(), StandardCharsets.UTF_8)).getAsJsonObject();

		Set<String> sprites = new TreeSet<>();
		for (JsonElement sourceElement : json.getAsJsonArray("sources")) {
			JsonObject source = sourceElement.getAsJsonObject();
			String type = stripNamespace(source.get("type").getAsString());
			switch (type) {
				case "directory" -> {
					String directory = source.get("source").getAsString();
					String prefix = source.has("prefix") ? source.get("prefix").getAsString() : "";
					for (String texture : textures.tailSet(directory + "/", true)) {
						if (!texture.startsWith(directory + "/"))
							break;
						sprites.add("minecraft:" + prefix + texture.substring(directory.length() + 1));
					}
				}
				case "single" -> sprites.add("minecraft:" + stripNamespace(source.get("resource").getAsString()));
				case "paletted_permutations" -> {
					String separator = source.has("separator") ? source.get("separator").getAsString() : "_";
					JsonObject permutations = source.getAsJsonObject("permutations");
					for (JsonElement texture : source.getAsJsonArray("textures")) {
						String base = stripNamespace(texture.getAsString());
						for (String key : permutations.keySet())
							sprites.add("minecraft:" + base + separator + key);
					}
				}
				default -> throw new IOException("Unsupported atlas source type: " + type);
			}
		}
		return sprites;
	}

	private static String stripNamespace(String key) {
		int colon = key.indexOf(':');
		return colon >= 0 ? key.substring(colon + 1) : key;
	}
}
