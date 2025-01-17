package weshare.server;

import weshare.controller.*;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.post;

public class Routes {
    public static final String LOGIN_PAGE = "/";
    public static final String LOGIN_ACTION = "/login.action";
    public static final String LOGOUT = "/logout";
    public static final String EXPENSES = "/expenses";
    public static final String EXPENSE_ACTION = "/expense.action";
    public static final String NEW_EXPENSE = "/newexpense";
    public static final String PAYMENT_REQUEST = "/paymentrequest";
    public static final String ADD_PAYMENT_ACTION = "/payment.action";
    public static final String PAYMENTREQUEST_SENT = "/paymentrequests_sent";
    public static final String PROCESS_PAYMENT_REQUEST = "/paymentrequest.action";
    public static final String PAYMENTREQUEST_RECEIVED = "/paymentrequests_received";




    public static void configure(WeShareServer server) {
        server.routes(() -> {
            post(LOGIN_ACTION,  PersonController.login);
            post(EXPENSE_ACTION,  AddExpensesController.addedExpense);
            post(PROCESS_PAYMENT_REQUEST,  PaymentRequestController.submitPaymentRequest);
            post(ADD_PAYMENT_ACTION,  PaymentRequestController.processPayment);
            get(LOGOUT,         PersonController.logout);
            get(EXPENSES,           ExpensesController.view);
            get(NEW_EXPENSE,   AddExpensesController.newExpense);
            get(PAYMENT_REQUEST, PaymentRequestController.paymentrequest);
            get(PAYMENTREQUEST_SENT,   PaymentRequestController.viewSent);
            get(PAYMENTREQUEST_RECEIVED,   PaymentRequestController.viewReceived);









        });
    }
}
