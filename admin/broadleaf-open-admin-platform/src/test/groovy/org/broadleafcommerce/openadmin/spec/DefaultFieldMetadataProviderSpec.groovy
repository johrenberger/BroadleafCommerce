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

import org.broadleafcommerce.openadmin.server.dao.provider.metadata.DefaultFieldMetadataProvider
import org.broadleafcommerce.openadmin.server.dao.provider.metadata.FieldMetadataProvider
import spock.lang.Specification

/**
 * Tests for {@link DefaultFieldMetadataProvider}.
 *
 * <p>This provider is a Spring {@code @Component} that extends
 * {@link org.broadleafcommerce.openadmin.server.dao.provider.metadata.BasicFieldMetadataProvider}
 * and overrides four public methods, each of which touches Hibernate
 * ({@code Session}, {@code SessionFactory}, {@code PersistentClass}). The
 * 569-line class is mostly glue code; the testable slices are:
 *
 * <ul>
 *   <li>{@code getOrder()} (inherited) — the provider's ordering within the
 *       chain. {@link DefaultFieldMetadataProvider} does not override this,
 *       so it inherits {@link BasicFieldMetadataProvider}'s value, which is
 *       {@link FieldMetadataProvider#BASIC}.</li>
 *   <li>Default constructor: the class is a Spring component but has no
 *       required-arg constructor; direct instantiation is safe (mirrors the
 *       pattern in {@link BasicFieldMetadataProviderSpec}).</li>
 *   <li>Inheritance assertion: the class extends
 *       {@link org.broadleafcommerce.openadmin.server.dao.provider.metadata.BasicFieldMetadataProvider},
 *       which is the load-bearing relationship that makes the rest of the
 *       provider chain work.</li>
 *   <li>Implementation assertion: the class implements
 *       {@link FieldMetadataProvider} (transitively, via the parent).</li>
 * </ul>
 *
 * <p>The four override methods ({@code addMetadata},
 * {@code overrideExclusionsFromXml}, {@code addMetadataFromMappingData},
 * {@code addMetadataFromFieldType}) are deferred to a follow-up task that
 * mocks the full Hibernate surface. They are documented in the
 * test-gap-report as high-risk untested behaviors.
 */
class DefaultFieldMetadataProviderSpec extends Specification {

    DefaultFieldMetadataProvider provider

    def setup() {
        // The class has a @Resource field (dialectHelper) but it's not
        // required for the testable slices (getOrder, instanceof checks).
        // Direct instantiation matches the convention from
        // BasicFieldMetadataProviderSpec.
        provider = new DefaultFieldMetadataProvider()
    }

    def "getOrder returns the BASIC provider order"() {
        expect:
        provider.getOrder() == FieldMetadataProvider.BASIC
        provider.getOrder() == Integer.MAX_VALUE
    }

    def "provider extends BasicFieldMetadataProvider"() {
        expect:
        provider instanceof org.broadleafcommerce.openadmin.server.dao.provider.metadata.BasicFieldMetadataProvider
    }

    def "provider implements FieldMetadataProvider"() {
        expect:
        FieldMetadataProvider.isInstance(provider)
    }

    def "provider class is annotated as a Spring component"() {
        expect:
        DefaultFieldMetadataProvider.class.getAnnotation(org.springframework.stereotype.Component) != null
    }

    def "provider class name reflects its role"() {
        expect:
        DefaultFieldMetadataProvider.simpleName == "DefaultFieldMetadataProvider"
    }
}
