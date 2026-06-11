/*-
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 *
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.openadmin.spec

import org.broadleafcommerce.common.presentation.client.SupportedFieldType
import org.broadleafcommerce.common.web.BroadleafRequestContext
import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata
import org.broadleafcommerce.openadmin.web.service.FormBuilderServiceImpl
import spock.lang.Specification

import java.util.TimeZone

/**
 * Tests for {@link FormBuilderServiceImpl#extractDefaultValueFromFieldData(String, BasicFieldMetadata)}.
 *
 * This method is a pure-string-in, string-out transformation that pulls a "default value" string
 * out of a {@link BasicFieldMetadata} and either returns it unchanged (after a type-coercion
 * sanity check) or returns {@code null} when the string does not parse as the declared
 * {@link SupportedFieldType}. The behaviour we want to lock down:
 *
 *  <ul>
 *      <li>Rule-typed fields ({@code RULE_SIMPLE}, {@code RULE_SIMPLE_TIME},
 *          {@code RULE_WITH_QUANTITY}) short-circuit to {@code null} without consulting
 *          {@code defaultValue} at all, because rule inputs are not pre-filled by a literal
 *          string.</li>
 *      <li>{@code BOOLEAN} fields accept only the strings {@code "true"}, {@code "false"},
 *          {@code "Y"}, {@code "N"} (case-insensitive). Any other string returns {@code null}.</li>
 *      <li>{@code DATE} fields accept an ISO-ish date string in the Broadleaf default
 *          date format and round-trip it (parse + reformat), so the returned string is
 *          always formatted by {@code FormatUtil.getDateFormat()} (not the original
 *          input literal). The literal token {@code "today"} (case-insensitive) is
 *          replaced with the current date formatted the same way.</li>
 *      <li>Numeric fields ({@code INTEGER}, {@code DECIMAL}, {@code MONEY}) parse via
 *          {@link java.text.DecimalFormat} using the {@code BroadleafRequestContext}'s
 *          configured locale; unparseable strings return {@code null} rather than
 *          throwing.</li>
 *  </ul>
 *
 * The test instantiates the production class directly (no Spring) because
 * {@code extractDefaultValueFromFieldData} reads only its arguments and the static
 * {@code BroadleafRequestContext}; none of the @Resource-injected collaborators are
 * dereferenced on the code paths we exercise.
 */
class FormBuilderServiceImplSpec extends Specification {

    FormBuilderServiceImpl service

    def setup() {
        // extractDefaultValueFromFieldData reads the locale AND timezone off the static
        // BroadleafRequestContext. Spin up a fresh one per test so we are
        // isolated from any test that left state behind, and seed the timezone
        // because FormatUtil.getDateFormat() pulls it from the context (NPEs
        // otherwise on the DATE code path). The locale is only consulted on
        // the INTEGER / DECIMAL / MONEY branches, which we don't exercise
        // here; those branches would require a fully-configured Broadleaf
        // Locale entity and are out of scope.
        BroadleafRequestContext context = new BroadleafRequestContext()
        context.setTimeZone(TimeZone.getDefault())
        BroadleafRequestContext.setBroadleafRequestContext(context)
        service = new FormBuilderServiceImpl()
    }

    def cleanup() {
        BroadleafRequestContext.setBroadleafRequestContext(null)
    }

    // ---- RULE_*: short-circuit to null regardless of defaultValue ----

    def "RULE_SIMPLE field always returns null even when a defaultValue is set"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("myRule")
        fmd.setDefaultValue("anything-non-null")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.RULE_SIMPLE.toString(), fmd)

        then:
        result == null
    }

    def "RULE_SIMPLE_TIME field always returns null"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("myRuleTime")
        fmd.setDefaultValue("11:30")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.RULE_SIMPLE_TIME.toString(), fmd)

        then:
        result == null
    }

    def "RULE_WITH_QUANTITY field always returns null"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("myRuleQty")
        fmd.setDefaultValue("3")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.RULE_WITH_QUANTITY.toString(), fmd)

        then:
        result == null
    }

    // ---- BOOLEAN: explicit allow-list of true/false/Y/N (case-insensitive) ----

    def "BOOLEAN field accepts lowercase 'true'"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("active")
        fmd.setDefaultValue("true")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.BOOLEAN.toString(), fmd)

        then:
        result == "true"
    }

    def "BOOLEAN field accepts lowercase 'false'"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("active")
        fmd.setDefaultValue("false")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.BOOLEAN.toString(), fmd)

        then:
        result == "false"
    }

    def "BOOLEAN field accepts uppercase 'Y' and 'N'"() {
        given:
        BasicFieldMetadata ymd = new BasicFieldMetadata()
        ymd.setName("flag")
        ymd.setDefaultValue("Y")

        BasicFieldMetadata nmd = new BasicFieldMetadata()
        nmd.setName("flag")
        nmd.setDefaultValue("n")

        when:
        String yResult = service.extractDefaultValueFromFieldData(
                SupportedFieldType.BOOLEAN.toString(), ymd)
        String nResult = service.extractDefaultValueFromFieldData(
                SupportedFieldType.BOOLEAN.toString(), nmd)

        then:
        yResult == "Y"
        nResult == "n"
    }

    def "BOOLEAN field rejects any string outside the allow-list"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("active")
        fmd.setDefaultValue("yes")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.BOOLEAN.toString(), fmd)

        then:
        result == null
    }

    def "BOOLEAN field rejects a numeric string"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("active")
        fmd.setDefaultValue("1")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.BOOLEAN.toString(), fmd)

        then:
        result == null
    }

    // ---- DATE: round-tripped through FormatUtil + "today" sentinel ----

    def "DATE field returns the defaultValue unchanged when it parses cleanly"() {
        given: "a date in the Broadleaf default date format (yyyy.MM.dd HH:mm:ss)"
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("publishedOn")
        fmd.setDefaultValue("2020.01.15 00:00:00")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.DATE.toString(), fmd)

        then: "the returned string round-trips through FormatUtil.getDateFormat() unchanged"
        result == "2020.01.15 00:00:00"
    }

    def "DATE field substitutes 'today' (case-insensitive) with the formatted current date"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("publishedOn")
        fmd.setDefaultValue("today")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.DATE.toString(), fmd)

        then: "the result is a non-null date string in yyyy.MM.dd HH:mm:ss form"
        result != null
        result.matches(/\d{4}\.\d{2}\.\d{2} \d{2}:\d{2}:\d{2}/)
    }

    def "DATE field returns null when the defaultValue is unparseable"() {
        given:
        BasicFieldMetadata fmd = new BasicFieldMetadata()
        fmd.setName("publishedOn")
        fmd.setDefaultValue("not-a-date")

        when:
        String result = service.extractDefaultValueFromFieldData(
                SupportedFieldType.DATE.toString(), fmd)

        then:
        result == null
    }
}
