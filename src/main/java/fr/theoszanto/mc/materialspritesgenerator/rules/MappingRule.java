package fr.theoszanto.mc.materialspritesgenerator.rules;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface MappingRule {
	boolean matches(String name);

	String componentCode(String name);

	Component component(String name);

	@Contract(pure = true)
	static MappingRule.Abstract rule() {
		return MappingRule.Default.INSTANCE;
	}

	interface Abstract {
		boolean test(String name);

		String process(String name);

		@Contract(value = " -> new", pure = true)
		default SpriteMappingRule sprite() {
			return new SpriteMappingRule(this);
		}

		@Contract(value = " -> new", pure = true)
		default HeadMappingRule head() {
			return new HeadMappingRule(this);
		}

		@Contract(value = " -> new", pure = true)
		default CodeMappingRule code() {
			return new CodeMappingRule(this);
		}

		@Contract(value = "_ -> new", pure = true)
		default MultiMappingRule multi(Function<MappingRule.Abstract, List<MappingRule>> rules) {
			return new MultiMappingRule(this, rules);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract exact(Material exact) {
			return this.exact(exact.key().value());
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract exact(String exact) {
			return new ExactMappingRule(this, exact);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract either(Material... either) {
			return this.regex(Arrays.stream(either).map(m -> m.key().value()).collect(Collectors.joining("|")));
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract regex(@Language("RegExp") String regex) {
			return new RegExpMappingRule(this, regex);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract prefix(String prefix) {
			return this.prefix(prefix, false);
		}

		@Contract(value = "_, _ -> new", pure = true)
		default MappingRule.Abstract prefix(String prefix, boolean strip) {
			return new PrefixMappingRule(this, prefix, strip);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract suffix(String suffix) {
			return this.suffix(suffix, false);
		}

		@Contract(value = "_, _ -> new", pure = true)
		default MappingRule.Abstract suffix(String suffix, boolean strip) {
			return new SuffixMappingRule(this, suffix, strip);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract overwrite(String overwrite) {
			return new OverwriteMappingRule(this, overwrite);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract prepend(String prepend) {
			return new PrependMappingRule(this, prepend);
		}

		@Contract(value = "_ -> new", pure = true)
		default MappingRule.Abstract append(String append) {
			return new AppendMappingRule(this, append);
		}
	}

	class Delegating implements Abstract {
		private final MappingRule.Abstract delegate;

		public Delegating(MappingRule.Abstract delegate) {
			this.delegate = delegate;
		}

		@Override
		public boolean test(String name) {
			return this.delegate.test(name);
		}

		@Override
		public String process(String name) {
			return this.delegate.process(name);
		}
	}

	class Default implements Abstract {
		private static final Default INSTANCE = new Default();

		private Default() {}

		@Override
		public boolean test(String name) {
			return true;
		}

		@Override
		public String process(String name) {
			return name;
		}
	}
}
