package weshare.controller;


import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;

import javax.money.MonetaryAmount;
import java.util.Collection;
import static weshare.model.MoneyHelper.amountOf;



public class PaymentControllerHelper {

    public static MonetaryAmount calculateTotal(Collection<PaymentRequest> paymentRequests) {
        MonetaryAmount totalAmount = amountOf(0);
        for (PaymentRequest paymentRequest : paymentRequests) {
            totalAmount = totalAmount.add(paymentRequest.getAmountToPay());
        }
        return totalAmount;
    }

    public static ExpenseDAO getExpenseDAO() {
        return ServiceRegistry.lookup(ExpenseDAO.class);
    }
}


