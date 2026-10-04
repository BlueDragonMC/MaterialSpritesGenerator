package com.bluedragonmc.materialsprites.generator.rules;

import org.intellij.lang.annotations.Language;

import java.util.regex.Pattern;

public class RegExpMappingRule extends MappingRule.Delegating {
	private final Pattern regex;

	public RegExpMappingRule(MappingRule.Abstract delegate, @Language("RegExp") String regex) {
		super(delegate);
		this.regex = Pattern.compile(regex);
	}

	@Override
	public boolean test(String name) {
		return super.test(name) && this.regex.matcher(name).matches();
	}
}
