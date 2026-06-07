/*-
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.spec.offer.service.discount

import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.core.offer.service.discount.OrderOfferComparator
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateOrderOffer
import spock.lang.Specification

class OrderOfferComparatorSpec extends Specification {

    def "compare: lower priority comes first"() {
        given: "two offers with different priorities"
        PromotableCandidateOrderOffer a = Mock() {
            getPriority() >> 5
        }
        PromotableCandidateOrderOffer b = Mock() {
            getPriority() >> 10
        }
        when: "the comparator is applied"
        int result = OrderOfferComparator.INSTANCE.compare(a, b)
        then: "a is sorted before b (negative result)"
        result < 0
    }

    def "compare: higher priority comes first"() {
        given: "two offers with different priorities, reversed"
        PromotableCandidateOrderOffer a = Mock() {
            getPriority() >> 10
        }
        PromotableCandidateOrderOffer b = Mock() {
            getPriority() >> 5
        }
        when: "the comparator is applied"
        int result = OrderOfferComparator.INSTANCE.compare(a, b)
        then: "a is sorted after b (positive result)"
        result > 0
    }

    def "compare: same priority falls back to potential savings (higher wins)"() {
        given: "two offers with same priority but different potential savings"
        Money aSavings = new Money(5.00) // less savings
        Money bSavings = new Money(15.00) // more savings
        PromotableCandidateOrderOffer a = Mock() {
            getPriority() >> 5
            getPotentialSavings() >> aSavings
        }
        PromotableCandidateOrderOffer b = Mock() {
            getPriority() >> 5
            getPotentialSavings() >> bSavings
        }
        when: "the comparator is applied"
        int result = OrderOfferComparator.INSTANCE.compare(a, b)
        then: "a (lower savings) is sorted after b (higher savings)"
        // p2.getPotentialSavings().compareTo(p1.getPotentialSavings())
        //   = bSavings.compareTo(aSavings) = positive (b > a)
        // so comparator returns positive when b has more savings (b is "greater")
        result > 0
    }
}
