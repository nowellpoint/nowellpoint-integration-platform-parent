package com.nowellpoint.console.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Value.Immutable
@Value.Modifiable
@Value.Style(typeImmutable = "*", jdkOnly=true, create = "new")
@JsonSerialize(as = Preferences.class)
@JsonDeserialize(as = Preferences.class)
public abstract class AbstractPreferences {
	
	@Value.Default
	public Locale getLocale() {
		return Locale.getDefault();
	}
	
	@Value.Default
	public String getTimeZone() {
		return TimeZone.getDefault().getID();
	}
	
	@Value.Derived
	public String getLocaleDisplayName() {
		Optional<Locale> locale = Arrays.asList(Locale.getAvailableLocales()).stream()
				.filter(l -> l.equals(getLocale()))
				.findFirst();
		
		if (locale.isPresent()) {
			return locale.get().getDisplayLanguage()
					.concat(! locale.get().getCountry().isEmpty() ? " (".concat(locale.get().getDisplayCountry().concat(")")) : "");
		}
		
		return null;
	}
	
	public static Preferences of(com.nowellpoint.console.entity.Preferences source) {
		return source == null ? null : Preferences.builder()
				.locale(source.getLocale())
				.timeZone(source.getTimeZone())
				.build();
	}
}