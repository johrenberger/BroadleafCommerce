/*-
 * #%L
 * BroadleafCommerce Framework
 * #%L
 * Copyright (C) 2009 - 2026 Broadleaf Commerce
 * #%L
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

import org.broadleafcommerce.core.offer.domain.Offer
import org.broadleafcommerce.core.offer.domain.OfferItemCriteria
import org.broadleafcommerce.core.offer.service.discount.PromotionDiscount
import org.broadleafcommerce.core.offer.service.discount.domain.PromotableCandidateItemOffer
import spock.lang.Specification

class PromotionDiscountSpec extends Specification {

    def "copy returns a new instance with all fields duplicated"() {
        given: "a PromotionDiscount populated with all fields"
        Offer offer = Mock()
        OfferItemCriteria criteria = Mock()
        PromotableCandidateItemOffer candidate = Mock()
        PromotionDiscount pd = new PromotionDiscount()
        pd.setPromotion(offer)
        pd.setItemCriteria(criteria)
        pd.setCandidateItemOffer(candidate)
        pd.setQuantity(7)
        pd.setFinalizedQuantity(9)

        when: "copy is invoked"
        PromotionDiscount copy = pd.copy()

        then: "a new instance is returned with identical field values"
        copy != null
        copy.is(pd) == false
        copy.getPromotion().is(offer)
        copy.getItemCriteria().is(criteria)
        copy.getCandidateItemOffer().is(candidate)
        copy.getQuantity() == 7
        copy.getFinalizedQuantity() == 9
    }

    def "split returns null when the split consumes the entire finalized quantity"() {
        given: "a PromotionDiscount whose finalizedQuantity equals the split amount"
        PromotionDiscount pd = new PromotionDiscount()
        pd.setFinalizedQuantity(5)
        pd.setQuantity(5)

        when: "split is called with the full finalized quantity"
        PromotionDiscount result = pd.split(5)

        then: "the result is null (the other side gets nothing)"
        result == null
        and: "this discount is updated to reflect the split"
        pd.getFinalizedQuantity() == 5
        pd.getQuantity() == 5
    }

    def "split returns a remainder PromotionDiscount when splitQty is less than finalizedQuantity"() {
        given: "a PromotionDiscount with finalizedQuantity larger than the split amount"
        PromotionDiscount pd = new PromotionDiscount()
        pd.setPromotion(Mock(Offer))
        pd.setItemCriteria(Mock(OfferItemCriteria))
        pd.setCandidateItemOffer(Mock(PromotableCandidateItemOffer))
        pd.setFinalizedQuantity(10)
        pd.setQuantity(10)

        when: "split is called with a smaller quantity"
        PromotionDiscount remainder = pd.split(3)

        then: "the original is updated to the split size"
        pd.getFinalizedQuantity() == 3
        pd.getQuantity() == 3
        and: "a remainder PromotionDiscount is returned with the leftover quantity"
        remainder != null
        remainder.getQuantity() == 7
        remainder.getFinalizedQuantity() == 7
        and: "the remainder shares references to the same promotion/criteria/candidate"
        remainder.getPromotion().is(pd.getPromotion())
        remainder.getItemCriteria().is(pd.getItemCriteria())
        remainder.getCandidateItemOffer().is(pd.getCandidateItemOffer())
    }

    def "incrementQuantity adds to the existing quantity"() {
        given: "a fresh PromotionDiscount"
        PromotionDiscount pd = new PromotionDiscount()

        when: "incrementQuantity is invoked twice"
        pd.incrementQuantity(2)
        pd.incrementQuantity(3)

        then: "the quantity accumulates"
        pd.getQuantity() == 5
    }

    def "resetQty overwrites both quantity and finalizedQuantity"() {
        given: "a PromotionDiscount with prior values"
        PromotionDiscount pd = new PromotionDiscount()
        pd.setQuantity(8)
        pd.setFinalizedQuantity(8)

        when: "resetQty is called with a new value"
        pd.resetQty(4)

        then: "both fields are overwritten"
        pd.getQuantity() == 4
        pd.getFinalizedQuantity() == 4
    }

    def "isFinalized returns true when quantity equals finalizedQuantity"() {
        given: "a PromotionDiscount whose quantity and finalizedQuantity match"
        PromotionDiscount pd = new PromotionDiscount()
        pd.setQuantity(6)
        pd.setFinalizedQuantity(6)

        expect: "isFinalized is true"
        pd.isFinalized()
    }

    def "isFinalized returns false when quantity differs from finalizedQuantity"() {
        given: "a PromotionDiscount where quantity and finalizedQuantity diverge"
        PromotionDiscount pd = new PromotionDiscount()
        pd.setQuantity(4)
        pd.setFinalizedQuantity(7)

        expect: "isFinalized is false"
        !pd.isFinalized()
    }
}
