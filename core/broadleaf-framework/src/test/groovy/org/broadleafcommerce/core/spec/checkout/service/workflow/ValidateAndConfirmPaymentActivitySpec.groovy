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
package org.broadleafcommerce.core.spec.checkout.service.workflow

import ch.qos.logback.classic.Level
import org.broadleafcommerce.common.money.Money
import org.broadleafcommerce.common.payment.PaymentGatewayType
import org.broadleafcommerce.common.payment.PaymentTransactionType
import org.broadleafcommerce.common.payment.PaymentType
import org.broadleafcommerce.common.payment.dto.PaymentResponseDTO
import org.broadleafcommerce.core.checkout.service.exception.CheckoutException
import org.broadleafcommerce.core.checkout.service.strategy.OrderPaymentConfirmationStrategy
import org.broadleafcommerce.core.checkout.service.workflow.ValidateAndConfirmPaymentActivity
import org.broadleafcommerce.core.payment.domain.OrderPayment
import org.broadleafcommerce.core.payment.domain.OrderPaymentImpl
import org.broadleafcommerce.core.payment.domain.PaymentTransaction
import org.broadleafcommerce.core.payment.domain.PaymentTransactionImpl
import org.broadleafcommerce.core.payment.service.DefaultPaymentGatewayCheckoutService
import org.broadleafcommerce.core.payment.service.OrderPaymentService
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusService
import org.broadleafcommerce.core.payment.service.OrderPaymentStatusServiceImpl
import org.broadleafcommerce.core.workflow.state.ActivityStateManagerImpl
import org.broadleafcommerce.core.workflow.state.RollbackStateLocal
import org.broadleafcommerce.profile.core.domain.CustomerPayment

/**
 * @author Elbert Bautista (elbertbautista)
 */
class ValidateAndConfirmPaymentActivitySpec extends BaseCheckoutActivitySpec {

    def setupSpec() {
        // Enable TRACE level logging for the activity so that LOG.isTraceEnabled() and
        // LOG.isErrorEnabled() guard branches inside the execute() flow are exercised.
        def logger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ValidateAndConfirmPaymentActivity.class)
        logger.setLevel(Level.TRACE)
    }

    OrderPaymentStatusService statusService = new OrderPaymentStatusServiceImpl()

    OrderPayment confirmedCC = new OrderPaymentImpl()
    PaymentTransaction confirmedCCTransaction = new PaymentTransactionImpl()

    OrderPayment unconfirmedTP = new OrderPaymentImpl()
    PaymentTransaction unconfirmedTPTransaction = new PaymentTransactionImpl()

    OrderPayment unconfirmedCC = new OrderPaymentImpl()
    PaymentTransaction unconfirmedCCTransaction = new PaymentTransactionImpl()

    def setup() {
        def rollbackStateLocal = new RollbackStateLocal()
        rollbackStateLocal.setThreadId("SPOCK_THREAD")
        rollbackStateLocal.setWorkflowId("TEST")
        RollbackStateLocal.setRollbackStateLocal(rollbackStateLocal)

        new ActivityStateManagerImpl().init()

        confirmedCC.amount = new Money(10)
        confirmedCC.type = PaymentType.CREDIT_CARD
        confirmedCC.order = context.seedData.order

        confirmedCCTransaction.type = PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        confirmedCCTransaction.amount = new Money(10)
        confirmedCCTransaction.success = true
        confirmedCCTransaction.orderPayment = confirmedCC

        confirmedCC.transactions << confirmedCCTransaction

        unconfirmedTP.amount = new Money(12)
        unconfirmedTP.type = PaymentType.THIRD_PARTY_ACCOUNT
        unconfirmedTP.order = context.seedData.order

        unconfirmedTPTransaction.type = PaymentTransactionType.UNCONFIRMED
        unconfirmedTPTransaction.amount = new Money(12)
        unconfirmedTPTransaction.success = true
        unconfirmedTPTransaction.orderPayment = unconfirmedTP

        unconfirmedTP.transactions << unconfirmedTPTransaction

        unconfirmedCC.amount = new Money(15)
        unconfirmedCC.type = PaymentType.CREDIT_CARD
        unconfirmedCC.order = context.seedData.order

        unconfirmedCCTransaction.type = PaymentTransactionType.UNCONFIRMED
        unconfirmedCCTransaction.amount = new Money(15)
        unconfirmedCCTransaction.success = true
        unconfirmedCCTransaction.orderPayment = unconfirmedCC

        unconfirmedCC.transactions << unconfirmedCCTransaction
    }

    def reset() {
        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            it
        }
        context.seedData.order.payments = new ArrayList<OrderPayment>()
        context.seedData.order.total = null
    }

    def "Test Activity State Manager"() {
        setup: "I have one confirmed order payment on the order"
        reset()
        context.seedData.order.payments << confirmedCC
        context.seedData.order.total = new Money(10)

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "There should be 1 state container for the Activity State Manager rollback thread"
        def containers = ActivityStateManagerImpl.stateManager.stateMap.get("SPOCK_THREAD_TEST")
        containers.size() == 1
    }

    def "Test validate payment sums against order total"() {
        setup: "I have one confirmed order payment on the order but the order total does not match"
        reset()
        context.seedData.order.payments << confirmedCC
        context.seedData.order.total = new Money(20)

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "An IllegalArgumentException should be thrown stating that the sums don't add up"
        IllegalArgumentException ex = thrown()
        ex.message == "There are not enough payments to pay for the total order. The sum of " +
                "the payments is " + confirmedCCTransaction.amount.amount.toPlainString() + " and the order total is " + context.seedData.order.total.amount.toPlainString()

    }

    def "Test SUCCESSFULLY confirming all unconfirmed THIRD_PARTY_ACCOUNT transactions on the order"() {
        setup: "I have one unconfirmed THIRD_PARTY_ACCOUNT order payment transaction on the order"
        reset()
        context.seedData.order.payments << unconfirmedTP
        context.seedData.order.total = new Money(12)

        //Initiate Mocks
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(12))
                .rawResponse("TEST")
                .successful(true)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> new PaymentTransactionImpl()
        mockOrderPaymentService.save(_ as OrderPayment) >> {OrderPayment payment -> payment}
        mockOrderPaymentService.save(_ as PaymentTransaction) >> {PaymentTransaction transaction -> transaction}

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "The order should contain an OrderPayment with two THIRD_PARTY_ACCOUNT Transactions"
        context.seedData.order.payments.get(0).transactions.get(0).type == PaymentTransactionType.UNCONFIRMED
        context.seedData.order.payments.get(0).transactions.get(1).type == PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        context.seedData.order.payments.get(0).transactions.get(1).success
        context.seedData.order.payments.get(0).transactions.get(1).parentTransaction == context.seedData.order.payments.get(0).transactions.get(0)
    }

    def "Test UNSUCCESSFULLY confirming all unconfirmed THIRD_PARTY_ACCOUNT transactions on the order"() {
        setup: "I have one unconfirmed THIRD_PARTY_ACCOUNT order payment transaction on the order"
        reset()
        context.seedData.order.payments << unconfirmedTP
        context.seedData.order.total = new Money(12)

        //Initiate Mocks
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(12))
                .rawResponse("TEST")
                .successful(false)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        
        PaymentTransaction tx = new PaymentTransactionImpl().with {
            orderPayment = unconfirmedTP
            it
        }
        
        mockOrderPaymentService.createTransaction() >> {
            tx
        }
        mockOrderPaymentService.save(_ as OrderPayment) >> {OrderPayment payment -> payment}
        mockOrderPaymentService.save(_ as PaymentTransaction) >> {PaymentTransaction transaction -> transaction}
        mockOrderPaymentService.readPaymentById(_) >> {Long id -> unconfirmedTP }
        mockOrderPaymentService.readTransactionById(_) >> { Long id -> tx }
        
        DefaultPaymentGatewayCheckoutService mockCheckoutService = Stub()
        mockCheckoutService.orderPaymentService = mockOrderPaymentService

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockCheckoutService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "A CheckoutException should be thrown and the order should contain an OrderPayment with two THIRD_PARTY_ACCOUNT Transactions (one unsuccessful)"
        CheckoutException ex = thrown()
        ex.message == "Attempting to confirm/authorize an UNCONFIRMED transaction on the order was unsuccessful."
        context.seedData.order.payments.get(0).transactions.get(0).type == PaymentTransactionType.UNCONFIRMED
        context.seedData.order.payments.get(0).transactions.get(1).type == PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        !context.seedData.order.payments.get(0).transactions.get(1).success
        context.seedData.order.payments.get(0).transactions.get(1).parentTransaction == context.seedData.order.payments.get(0).transactions.get(0)
    }

    def "Test SUCCESSFULLY confirming all unconfirmed CREDIT_CARD transactions on the order"() {
        setup: "I have one unconfirmed CREDIT_CARD order payment transaction on the order"
        reset()
        context.seedData.order.payments << unconfirmedCC
        context.seedData.order.total = new Money(15)

        //Initiate Mocks
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(15))
                .rawResponse("TEST")
                .successful(true)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> new PaymentTransactionImpl()
        mockOrderPaymentService.save(_ as OrderPayment) >> {OrderPayment payment -> payment}
        mockOrderPaymentService.save(_ as PaymentTransaction) >> {PaymentTransaction transaction -> transaction}

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService;
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "The order should contain an OrderPayment with two CREDIT_CARD Transactions"
        context.seedData.order.payments.get(0).transactions.get(0).type == PaymentTransactionType.UNCONFIRMED
        context.seedData.order.payments.get(0).transactions.get(1).type == PaymentTransactionType.AUTHORIZE_AND_CAPTURE
        context.seedData.order.payments.get(0).transactions.get(1).success
        context.seedData.order.payments.get(0).transactions.get(1).parentTransaction == context.seedData.order.payments.get(0).transactions.get(0)

    }

    def "Test confirming with null responseDTO from strategy throws CheckoutException"() {
        setup: "I have one unconfirmed CREDIT_CARD order payment and the strategy returns a null response"
        reset()
        context.seedData.order.payments << unconfirmedCC
        context.seedData.order.total = new Money(15)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> null

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> new PaymentTransactionImpl()
        mockOrderPaymentService.save(_ as OrderPayment) >> { OrderPayment payment -> payment }
        mockOrderPaymentService.save(_ as PaymentTransaction) >> { PaymentTransaction transaction -> transaction }

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "A CheckoutException should be thrown because the response DTO was null"
        CheckoutException ex = thrown()
        ex.message.contains("Unable to 'confirm' the UNCONFIRMED Transaction")
    }

    def "Test createCustomerPaymentToken is invoked when saveToken is true on an AUTHORIZE transaction"() {
        setup: "I have one confirmed AUTHORIZE transaction with saveToken=true"
        reset()

        // Build a payment whose single transaction is AUTHORIZE (not UNCONFIRMED) so the
        // second branch (else if AUTHORIZE/AUTHORIZE_AND_CAPTURE) is exercised.
        OrderPayment confirmedAuthPayment = new OrderPaymentImpl()
        confirmedAuthPayment.amount = new Money(10)
        confirmedAuthPayment.type = PaymentType.CREDIT_CARD
        confirmedAuthPayment.order = context.seedData.order

        PaymentTransaction authTx = new PaymentTransactionImpl()
        authTx.type = PaymentTransactionType.AUTHORIZE
        authTx.amount = new Money(10)
        authTx.success = true
        authTx.saveToken = true
        authTx.orderPayment = confirmedAuthPayment
        confirmedAuthPayment.transactions << authTx

        context.seedData.order.payments << confirmedAuthPayment
        context.seedData.order.total = new Money(10)

        CustomerPayment mockCustomerPayment = Mock(CustomerPayment)
        OrderPaymentService mockOrderPaymentService = Mock()
        1 * mockOrderPaymentService.createCustomerPaymentFromPaymentTransaction(_ as PaymentTransaction) >> mockCustomerPayment

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "no exception should be thrown"
        noExceptionThrown()
    }

    def "Test multiple confirmed transactions of same type throws IllegalArgumentException"() {
        setup: "I have one unconfirmed CREDIT_CARD payment that, after confirmation, will have two AUTHORIZE_AND_CAPTURE transactions"
        reset()
        context.seedData.order.payments << unconfirmedCC
        context.seedData.order.total = new Money(15)

        // Response returns UNCONFIRMED as the paymentTransactionType so that the new transaction
        // created during confirmation will share its type with the existing UNCONFIRMED parent.
        // After the second loop adds the new transaction, getTransactionsForType(UNCONFIRMED)
        // will return 2 transactions, triggering the IllegalArgumentException.
        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.CREDIT_CARD, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(15))
                .rawResponse("TEST")
                .successful(true)
                .paymentTransactionType(PaymentTransactionType.UNCONFIRMED)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> new PaymentTransactionImpl()
        mockOrderPaymentService.save(_ as OrderPayment) >> { OrderPayment payment -> payment }
        mockOrderPaymentService.save(_ as PaymentTransaction) >> { PaymentTransaction transaction -> transaction }

        activity = new ValidateAndConfirmPaymentActivity().with {
            orderPaymentStatusService = statusService
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "An IllegalArgumentException should be thrown about multiple confirmed transactions"
        IllegalArgumentException ex = thrown()
        ex.message.contains("There should only be one AUTHORIZE or AUTHORIZE_AND_CAPTURE transaction")
    }

    def "Test failed transaction with shouldRollbackFailedTransaction returning true adds to rollback list"() {
        setup: "I have one unconfirmed THIRD_PARTY payment whose confirmation fails and shouldRollback returns true"
        reset()
        context.seedData.order.payments << unconfirmedTP
        context.seedData.order.total = new Money(12)

        PaymentResponseDTO responseDTO = new PaymentResponseDTO(PaymentType.THIRD_PARTY_ACCOUNT, PaymentGatewayType.PASSTHROUGH)
                .amount(new Money(12))
                .rawResponse("TEST")
                .successful(false)
                .paymentTransactionType(PaymentTransactionType.AUTHORIZE_AND_CAPTURE)

        OrderPaymentConfirmationStrategy mockStrategy = Mock()
        mockStrategy.confirmTransaction(*_) >> responseDTO

        PaymentTransaction tx = new PaymentTransactionImpl().with {
            orderPayment = unconfirmedTP
            it
        }

        OrderPaymentService mockOrderPaymentService = Mock()
        mockOrderPaymentService.createTransaction() >> { tx }
        mockOrderPaymentService.save(_ as OrderPayment) >> { OrderPayment payment -> payment }
        mockOrderPaymentService.save(_ as PaymentTransaction) >> { PaymentTransaction transaction -> transaction }
        mockOrderPaymentService.readPaymentById(_) >> { Long id -> unconfirmedTP }
        mockOrderPaymentService.readTransactionById(_) >> { Long id -> tx }

        DefaultPaymentGatewayCheckoutService mockCheckoutService = Stub()
        mockCheckoutService.orderPaymentService = mockOrderPaymentService

        // Subclass the activity to make shouldRollbackFailedTransaction return true so that
        // the failedTransactionsToRollBack.add(tx) branch is exercised.
        def customActivity = new TestableValidateAndConfirmPaymentActivity()
        activity = customActivity.with {
            orderPaymentStatusService = statusService
            orderPaymentConfirmationStrategy = mockStrategy
            orderPaymentService = mockOrderPaymentService
            paymentGatewayCheckoutService = mockCheckoutService
            it
        }

        when: "I execute the ValidateAndConfirmPaymentActivity"
        context = activity.execute(context)

        then: "A CheckoutException should be thrown"
        CheckoutException ex = thrown()
        ex != null
    }

    def "Test ResponseTransactionPair no-arg constructor is accessible"() {
        setup: "Invoke the package-private no-arg constructor of the inner ResponseTransactionPair class"
        when: "I instantiate ResponseTransactionPair via Groovy's outer.new InnerClass() syntax"
        def outer = activity ?: new ValidateAndConfirmPaymentActivity()
        def pair = outer.new ValidateAndConfirmPaymentActivity.ResponseTransactionPair()

        then: "The pair should be created with null fields"
        pair.responseDTO == null
        pair.transactionId == null
    }

}

/**
 * Test subclass that overrides shouldRollbackFailedTransaction to return true,
 * exercising the failedTransactionsToRollBack.add(tx) branch.
 */
class TestableValidateAndConfirmPaymentActivity extends ValidateAndConfirmPaymentActivity {
    TestableValidateAndConfirmPaymentActivity() {
        super(null)
    }

    @Override
    protected boolean shouldRollbackFailedTransaction(
            ValidateAndConfirmPaymentActivity.ResponseTransactionPair failedTransactionPair) {
        return true
    }
}
