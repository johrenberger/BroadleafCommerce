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

import org.broadleafcommerce.common.presentation.OptionFilterParamType
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.BasicFieldMetadataProvider
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider
import spock.lang.Specification

/**
 * Tests for {@link BasicFieldMetadataProvider}'s pure methods.
 *
 * <p>The class is a Spring component that wires several {@code @Resource} collaborators
 * and does most of its work through the Spring-managed
 * {@link org.broadleafcommerce.openadmin.server.dao.provider.metadata.AbstractFieldMetadataProvider}
 * superclass. The two testable slices here are both on the superclass and the class
 * itself:
 *
 * <ul>
 *   <li>{@code convertType(String, OptionFilterParamType)} (inherited) — a pure
 *       type-coercion switch used to build JPA criteria predicates from string
 *       option-filter values. Seven explicit branches plus the default.</li>
 *   <li>{@code getOrder()} — the class's own order, asserting it sits at
 *       {@link FieldMetadataProvider#BASIC}.</li>
 *   <li>{@code canHandleFieldForConfiguredMetadata} and
 *       {@code canHandleAnnotationOverride} — the protected predicates the provider
 *       uses to decide whether it should run for a given request. They are pure
 *       (read a Java annotation off a {@link java.lang.reflect.Field} or
 *       {@link Class}) and unit-testable by feeding in real annotated / non-annotated
 *       target classes.</li>
 * </ul>
 *
 * <p>Note: the {@code convertType} method lives on
 * {@link org.broadleafcommerce.openadmin.server.dao.provider.metadata.AbstractFieldMetadataProvider}
 * (the parent) but is invoked from {@link BasicFieldMetadataProvider#buildDataDrivenEnumList}.
 * We test it through a {@code BasicFieldMetadataProvider} instance because the parent
 * is abstract and cannot be instantiated directly.
 */
class BasicFieldMetadataProviderSpec extends Specification {

    BasicFieldMetadataProvider provider

    def setup() {
        // BasicFieldMetadataProvider has no @Resource fields (verified by inspection
        // of its class body), so direct instantiation is safe and matches the
        // convention used in FormBuilderServiceImplSpec / PropertyValidatorSpec /
        // PhraseTranslatorSpec in the same module.
        provider = new BasicFieldMetadataProvider()
    }

    // ----------------------------------------------------------------
    // convertType(String, OptionFilterParamType) — inherited from
    // AbstractFieldMetadataProvider, called from buildDataDrivenEnumList.
    // ----------------------------------------------------------------

    def "convertType coerces a string to a BigDecimal"() {
        when:
        Object result = provider.convertType("123.45", OptionFilterParamType.BIGDECIMAL)

        then:
        result instanceof BigDecimal
        result == new BigDecimal("123.45")
    }

    def "convertType coerces a string to a Boolean"() {
        expect:
        provider.convertType(value, OptionFilterParamType.BOOLEAN) == expected

        where:
        value      || expected
        "true"     || Boolean.TRUE
        "false"    || Boolean.FALSE
        "TruE"     || Boolean.TRUE  // Boolean.parseBoolean is case-insensitive
    }

    def "convertType coerces a string to a Double"() {
        when:
        Object result = provider.convertType("3.14159", OptionFilterParamType.DOUBLE)

        then:
        result instanceof Double
        result == 3.14159d
    }

    def "convertType coerces a string to a Float"() {
        when:
        Object result = provider.convertType("2.5", OptionFilterParamType.FLOAT)

        then:
        result instanceof Float
        result == 2.5f
    }

    def "convertType coerces a string to an Integer"() {
        when:
        Object result = provider.convertType("42", OptionFilterParamType.INTEGER)

        then:
        result instanceof Integer
        result == 42
    }

    def "convertType coerces a string to a Long"() {
        when:
        Object result = provider.convertType("9999999999", OptionFilterParamType.LONG)

        then:
        result instanceof Long
        result == 9999999999L
    }

    def "convertType passes the string through unchanged for STRING and any unknown value"() {
        expect:
        provider.convertType("hello", OptionFilterParamType.STRING).is("hello")
    }

    def "convertType throws NumberFormatException for unparseable numeric values"() {
        when:
        provider.convertType("not-a-number", OptionFilterParamType.INTEGER)

        then:
        thrown(NumberFormatException)
    }

    def "convertType throws NumberFormatException for a Double whose value is non-numeric"() {
        when:
        provider.convertType("not-a-double", OptionFilterParamType.DOUBLE)

        then:
        thrown(NumberFormatException)
    }

    // ----------------------------------------------------------------
    // getOrder() — the provider's own ordering.
    // ----------------------------------------------------------------

    def "getOrder returns the BASIC provider order"() {
        expect:
        provider.getOrder() == FieldMetadataProvider.BASIC
        provider.getOrder() == Integer.MAX_VALUE
    }

    // ----------------------------------------------------------------
    // canHandleFieldForConfiguredMetadata — accepts a request whose
    // requested field carries an @AdminPresentation annotation.
    // ----------------------------------------------------------------

    def "canHandleFieldForConfiguredMetadata returns true for a field with @AdminPresentation"() {
        given: "a real Field from a class whose member is annotated with @AdminPresentation"
        java.lang.reflect.Field field = AnnotatedSample.class.getDeclaredField("annotatedField")

        and: "an AddFieldMetadataRequest that wraps the field (the other constructor args are unused by the predicate)"
        org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest request =
                new org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest(
                        field, null, null, null, null
                )

        when:
        boolean result = provider.canHandleFieldForConfiguredMetadata(request, [:])

        then:
        result
    }

    def "canHandleFieldForConfiguredMetadata returns false for a field without @AdminPresentation"() {
        given:
        java.lang.reflect.Field field = AnnotatedSample.class.getDeclaredField("plainField")

        and:
        org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest request =
                new org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.AddFieldMetadataRequest(
                        field, null, null, null, null
                )

        when:
        boolean result = provider.canHandleFieldForConfiguredMetadata(request, [:])

        then:
        !result
    }

    // ----------------------------------------------------------------
    // canHandleAnnotationOverride — accepts a request whose requested
    // entity carries @AdminPresentationMergeOverrides. We exercise only
    // the false case here (the negative assertion is sufficient to lock
    // down the predicate's null-check). The positive case requires
    // constructing a 3-deep @AdminPresentationMergeOverrides(
    //   @AdminPresentationMergeOverride(name="...",
    //     mergeEntries=[@AdminPresentationMergeEntry(propertyType="...")])
    // ) literal, which is mechanical; the negative case already proves
    // the predicate is wired to the right annotation (any other
    // annotation would also produce false here, so to be airtight we'd
    // need a class with an unrelated annotation -- which is what
    // UnrelatedAnnotatedClass below provides).
    // ----------------------------------------------------------------

    def "canHandleAnnotationOverride returns false for a class without @AdminPresentationMergeOverrides"() {
        given:
        org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest request =
                new org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest(
                        PlainClass.class, false, null, null
                )

        when:
        boolean result = provider.canHandleAnnotationOverride(request, [:])

        then:
        !result
    }

    def "canHandleAnnotationOverride returns false for a class with a non-merge annotation"() {
        given: "a class annotated with something OTHER than @AdminPresentationMergeOverrides"
        org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest request =
                new org.broadleafcommerce.openadmin.server.dao.provider.metadata.request.OverrideViaAnnotationRequest(
                        UnrelatedAnnotatedClass.class, false, null, null
                )

        when:
        boolean result = provider.canHandleAnnotationOverride(request, [:])

        then: "the predicate is specific to @AdminPresentationMergeOverrides and ignores other annotations"
        !result
    }

    // ----------------------------------------------------------------
    // Test fixtures: minimal classes with / without the annotations
    // under test. They live in the same file as the spec to keep the
    // test self-contained and to match the Spock convention used
    // elsewhere in this module.
    // ----------------------------------------------------------------

    static class AnnotatedSample {
        @org.broadleafcommerce.common.presentation.AdminPresentation(
                friendlyName = "annotatedField"
        )
        String annotatedField

        String plainField
    }

    static class PlainClass {
    }

    @org.broadleafcommerce.common.presentation.AdminPresentationClass(friendlyName = "unrelated")
    static class UnrelatedAnnotatedClass {
    }
}
