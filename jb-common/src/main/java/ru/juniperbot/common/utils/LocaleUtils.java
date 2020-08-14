/*
 * This file is part of JuniperBot.
 *
 * JuniperBot is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * JuniperBot is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with JuniperBot. If not, see <http://www.gnu.org/licenses/>.
 */
package ru.juniperbot.common.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleUtils {

    private LocaleUtils() {
    }

    static {
        SUPPORTED_LOCALES = Map.of(LocaleUtils.DEFAULT_LOCALE, Locale.forLanguageTag("ru-RU"), LocaleUtils.RU_LOCALE, Locale.US);
    }

    public static final String DEFAULT_LOCALE = "ru";

    public static final String RU_LOCALE = "en";

    public static Map<String, Locale> SUPPORTED_LOCALES;

    public static Locale get(String tag) {
        return SUPPORTED_LOCALES.get(tag);
    }

    public static Locale getOrDefault(String tag) {
        return SUPPORTED_LOCALES.getOrDefault(tag, getDefaultLocale());
    }

    public static boolean isSupported(String tag) {
        return SUPPORTED_LOCALES.containsKey(tag);
    }

    public static Locale getDefaultLocale() {
        return get(LocaleUtils.DEFAULT_LOCALE);
    }
}