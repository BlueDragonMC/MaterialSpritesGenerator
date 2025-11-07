package fr.theoszanto.mc.materialspritesgenerator.rules;

import com.destroystokyo.paper.profile.ProfileProperty;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.object.ObjectContents;
import org.jetbrains.annotations.Contract;

import java.util.Base64;

public final class HeadMappingRule implements MappingRule {
	private final MappingRule.Abstract base;
	private boolean hat = true;

	public HeadMappingRule(MappingRule.Abstract base) {
		this.base = base;
	}

	@Contract(" -> this")
	public HeadMappingRule noHat() {
		this.hat = false;
		return this;
	}

	@Override
	public boolean matches(String name) {
		return this.base.test(name);
	}

	@Override
	public String componentCode(String name) {
		StringBuilder code = new StringBuilder("head(\"").append(this.base.process(name)).append('"');
		if (!this.hat)
			code.append(", false");
		return code.append(')').toString();
	}

	@Override
	public Component component(String name) {
		return head(this.base.process(name), this.hat);
	}

	@SuppressWarnings("UnstableApiUsage") // ResolvableProfile
	public static Component head(String texture, boolean hat) {
		return Component.object(ObjectContents.playerHead().skin(ResolvableProfile.resolvableProfile()
				.addProperty(new ProfileProperty("textures", Base64.getEncoder().encodeToString(
						("{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + texture + "\"}}}").getBytes()
				)))
				.build()).hat(hat).build()).color(NamedTextColor.WHITE);
	}
}
