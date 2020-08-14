package ru.juniperbot.module.wikifur.model;

import org.sweble.wikitext.engine.config.*;
import org.sweble.wikitext.engine.utils.DefaultConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class WikiFurConfig extends
        DefaultConfig {
    public static WikiConfigImpl generate() {
        WikiConfigImpl c = new WikiConfigImpl();
        new WikiFurConfig().configureWiki(c);
        return c;
    }

    protected void configureSiteProperties(WikiConfigImpl c) {
        c.setSiteName("ВикиФур");
        c.setWikiUrl("http://ru.wikifur.com");
        c.setContentLang("ru");
        c.setIwPrefix("ru");
    }

    protected ParserConfigImpl configureParser(WikiConfigImpl c) {
        ParserConfigImpl pc = super.configureParser(c);

        // --[ Link classification and parsing ]--

        pc.setInternalLinkPrefixPattern(null);
        pc.setInternalLinkPostfixPattern("[a-z]+");

        return pc;
    }

    protected void addNamespaces(WikiConfigImpl c) {
        c.addNamespace(new NamespaceImpl(
                -2,
                "Media",
                "Media",
                false,
                false,
                Collections.singletonList("Медиа")));

        c.addNamespace(new NamespaceImpl(
                -1,
                "Special",
                "Special",
                false,
                false,
                Collections.singletonList("Служебная")));

        c.addNamespace(new NamespaceImpl(
                0,
                "",
                "",
                false,
                false,
                new ArrayList<>()));

        c.addNamespace(new NamespaceImpl(
                1,
                "Talk",
                "Talk",
                false,
                false,
                Collections.singletonList("Обсуждение")));

        c.addNamespace(new NamespaceImpl(
                2,
                "User",
                "User",
                false,
                false,
                Collections.singletonList("Участник")));

        c.addNamespace(new NamespaceImpl(
                3,
                "User talk",
                "User talk",
                false,
                false,
                Collections.singletonList("Обсуждение участника")));

        c.addNamespace(new NamespaceImpl(
                4,
                "Wikipedia",
                "Project",
                false,
                false,
                Collections.singletonList("Проект")));

        c.addNamespace(new NamespaceImpl(
                5,
                "Wikipedia talk",
                "Project talk",
                false,
                false,
                Collections.singletonList("Обсуждение проекта")));

        c.addNamespace(new NamespaceImpl(
                6,
                "File",
                "File",
                false,
                true,
                Arrays.asList("Image", "Файл")));

        c.addNamespace(new NamespaceImpl(
                7,
                "File talk",
                "File talk",
                false,
                false,
                Arrays.asList("Image talk", "Обсуждение файла")));

        c.addNamespace(new NamespaceImpl(
                8,
                "MediaWiki",
                "MediaWiki",
                false,
                false,
                new ArrayList<>()));

        c.addNamespace(new NamespaceImpl(
                9,
                "MediaWiki talk",
                "MediaWiki talk",
                false,
                false,
                Collections.singletonList("Обсуждение MediaWiki")));

        c.addNamespace(new NamespaceImpl(
                10,
                "Template",
                "Template",
                false,
                false,
                Collections.singletonList("Шаблон")));

        c.addNamespace(new NamespaceImpl(
                11,
                "Template talk",
                "Template talk",
                false,
                false,
                Collections.singletonList("Обсуждение шаблона")));

        c.addNamespace(new NamespaceImpl(
                12,
                "Help",
                "Help",
                false,
                false,
                Collections.singletonList("Справка")));

        c.addNamespace(new NamespaceImpl(
                13,
                "Help talk",
                "Help talk",
                false,
                false,
                Collections.singletonList("Обсуждение справки")));

        c.addNamespace(new NamespaceImpl(
                14,
                "Category",
                "Category",
                false,
                false,
                Collections.singletonList("Категория")));

        c.addNamespace(new NamespaceImpl(
                15,
                "Category talk",
                "Category talk",
                false,
                false,
                Collections.singletonList("Обсуждение категории")));

        c.addNamespace(new NamespaceImpl(
                100,
                "Portal",
                "Portal",
                false,
                false,
                Collections.singletonList("Портал")));

        c.addNamespace(new NamespaceImpl(
                101,
                "Portal talk",
                "Portal talk",
                false,
                false,
                Collections.singletonList("Обсуждение портала")));

        c.addNamespace(new NamespaceImpl(
                108,
                "Book",
                "Book",
                false,
                false,
                Collections.singletonList("Книга")));

        c.addNamespace(new NamespaceImpl(
                109,
                "Book talk",
                "Book talk",
                false,
                false,
                Collections.singletonList("Обсуждение книги")));

        c.setDefaultNamespace(c.getNamespace(0));
        c.setTemplateNamespace(c.getNamespace(10));
    }

    protected void addInterwikis(WikiConfigImpl c) {
        c.addInterwiki(new InterwikiImpl(
                "ru",
                "http://ru.wikipedia.org/wiki/$1",
                true,
                false));

        c.addInterwiki(new InterwikiImpl(
                "en",
                "http://en.wikipedia.org/wiki/$1",
                true,
                false));
    }

    protected void addI18nAliases(WikiConfigImpl c) {
        c.addI18nAlias(new I18nAliasImpl(
                "expr",
                false,
                Collections.singletonList("#expr:")));
        c.addI18nAlias(new I18nAliasImpl(
                "if",
                false,
                Collections.singletonList("#if:")));
        c.addI18nAlias(new I18nAliasImpl(
                "ifeq",
                false,
                Collections.singletonList("#ifeq:")));
        c.addI18nAlias(new I18nAliasImpl(
                "ifexpr",
                false,
                Collections.singletonList("#ifexpr:")));
        c.addI18nAlias(new I18nAliasImpl(
                "iferror",
                false,
                Collections.singletonList("#iferror:")));
        c.addI18nAlias(new I18nAliasImpl(
                "switch",
                false,
                Collections.singletonList("#switch:")));
        c.addI18nAlias(new I18nAliasImpl(
                "ifexist",
                false,
                Collections.singletonList("#ifexist:")));
        c.addI18nAlias(new I18nAliasImpl(
                "time",
                false,
                Collections.singletonList("#time:")));
        c.addI18nAlias(new I18nAliasImpl(
                "titleparts",
                false,
                Collections.singletonList("#titleparts:")));
        c.addI18nAlias(new I18nAliasImpl(
                "redirect",
                false,
                Collections.singletonList("#REDIRECT")));
        c.addI18nAlias(new I18nAliasImpl(
                "currentmonth",
                true,
                Arrays.asList("CURRENTMONTH", "CURRENTMONTH2")));
        c.addI18nAlias(new I18nAliasImpl(
                "currentday",
                true,
                Collections.singletonList("CURRENTDAY")));
        c.addI18nAlias(new I18nAliasImpl(
                "currentyear",
                true,
                Collections.singletonList("CURRENTYEAR")));
        c.addI18nAlias(new I18nAliasImpl(
                "pagename",
                true,
                Arrays.asList("PAGENAME", "PAGENAME:")));
        c.addI18nAlias(new I18nAliasImpl(
                "pagenamee",
                true,
                Arrays.asList("PAGENAMEE", "PAGENAMEE:")));
        c.addI18nAlias(new I18nAliasImpl(
                "namespace",
                true,
                Arrays.asList("NAMESPACE", "NAMESPACE:")));
        c.addI18nAlias(new I18nAliasImpl(
                "talkspace",
                true,
                Collections.singletonList("TALKSPACE")));
        c.addI18nAlias(new I18nAliasImpl(
                "subjectspace",
                true,
                Arrays.asList("SUBJECTSPACE", "ARTICLESPACE")));
        c.addI18nAlias(new I18nAliasImpl(
                "fullpagename",
                true,
                Collections.singletonList("FULLPAGENAME")));
        c.addI18nAlias(new I18nAliasImpl(
                "fullpagenamee",
                true,
                Collections.singletonList("FULLPAGENAMEE")));
        c.addI18nAlias(new I18nAliasImpl(
                "basepagename",
                true,
                Collections.singletonList("BASEPAGENAME")));
        c.addI18nAlias(new I18nAliasImpl(
                "talkpagename",
                true,
                Arrays.asList("TALKPAGENAME", "TALKPAGENAME:")));
        c.addI18nAlias(new I18nAliasImpl(
                "subjectpagename",
                true,
                Arrays.asList("SUBJECTPAGENAME", "ARTICLEPAGENAME")));
        c.addI18nAlias(new I18nAliasImpl(
                "safesubst",
                false,
                Collections.singletonList("SAFESUBST:")));
        c.addI18nAlias(new I18nAliasImpl(
                "sitename",
                true,
                Collections.singletonList("SITENAME")));
        c.addI18nAlias(new I18nAliasImpl(
                "ns",
                false,
                Collections.singletonList("NS:")));
        c.addI18nAlias(new I18nAliasImpl(
                "fullurl",
                false,
                Collections.singletonList("FULLURL:")));
        c.addI18nAlias(new I18nAliasImpl(
                "lcfirst",
                false,
                Collections.singletonList("LCFIRST:")));
        c.addI18nAlias(new I18nAliasImpl(
                "ucfirst",
                false,
                Collections.singletonList("UCFIRST:")));
        c.addI18nAlias(new I18nAliasImpl(
                "lc",
                false,
                Collections.singletonList("LC:")));
        c.addI18nAlias(new I18nAliasImpl(
                "uc",
                false,
                Collections.singletonList("UC:")));
        c.addI18nAlias(new I18nAliasImpl(
                "urlencode",
                false,
                Collections.singletonList("URLENCODE:")));
        c.addI18nAlias(new I18nAliasImpl(
                "contentlanguage",
                true,
                Arrays.asList("CONTENTLANGUAGE", "CONTENTLANG")));
        c.addI18nAlias(new I18nAliasImpl(
                "padleft",
                false,
                Collections.singletonList("PADLEFT:")));
        c.addI18nAlias(new I18nAliasImpl(
                "defaultsort",
                true,
                Arrays.asList("DEFAULTSORT:", "DEFAULTSORTKEY:", "DEFAULTCATEGORYSORT:")));
        c.addI18nAlias(new I18nAliasImpl(
                "filepath",
                false,
                Collections.singletonList("FILEPATH:")));
        c.addI18nAlias(new I18nAliasImpl(
                "tag",
                false,
                Collections.singletonList("#tag:")));
        c.addI18nAlias(new I18nAliasImpl(
                "protectionlevel",
                true,
                Collections.singletonList("PROTECTIONLEVEL:")));
        c.addI18nAlias(new I18nAliasImpl(
                "основная статья",
                false,
                Collections.singletonList("основная статья|")));
        c.addI18nAlias(new I18nAliasImpl(
                "нет статьи",
                false,
                Collections.singletonList("нет статьи|")));
    }
}
