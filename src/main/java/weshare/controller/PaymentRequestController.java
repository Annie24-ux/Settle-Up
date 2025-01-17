package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.PaymentRequest;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.persistence.PersonDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static weshare.model.MoneyHelper.amountOf;

public class PaymentRequestController {
    private static String expenseId;

    

    public static final Handler viewReceived = context -> {
        ExpenseDAO expensesDAO = PaymentControllerHelper.getExpenseDAO();
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsReceived = expensesDAO.findPaymentRequestsReceived(personLoggedIn);
        MonetaryAmount totalAmount = PaymentControllerHelper.calculateTotal(paymentRequestsReceived);

        double grandTotal = totalAmount.getNumber().doubleValue();
        String formattedGrandTotal = String.format("ZAR %.2f", grandTotal);
        String grandTotalString = formattedGrandTotal.split(",")[0];

        Map<String, Object> viewModel = Map.of(
            "paymentRequestsReceived", paymentRequestsReceived,
            "grand_total", grandTotalString
        );

        context.render("paymentrequests_received.html", viewModel);

    };

    public static final Handler viewSent = context -> {
        
        ExpenseDAO expensesDAO = PaymentControllerHelper.getExpenseDAO();
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequestsSent = expensesDAO.findPaymentRequestsSent(personLoggedIn);
        MonetaryAmount totalAmount = PaymentControllerHelper.calculateTotal(paymentRequestsSent);


        double grandTotal = totalAmount.getNumber().doubleValue();
        String formattedGrandTotal = String.format("ZAR %.2f", grandTotal);
        String grandTotalString = formattedGrandTotal.split(",")[0];

        Map<String, Object> viewModel = Map.of(
            "paymentRequests", paymentRequestsSent,
            "grand_total", grandTotalString
        );

        context.render("paymentrequests_sent.html", viewModel);

    };

    

   public static final Handler paymentrequest = context -> {
    ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
    Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

    Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);

    String s = context.req.toString();
    String id = s.split("[=)]")[1];
    expenseId = id;

    Expense handleExpense = null;
    for (Expense expense : expenses) {
        String expenseId = String.valueOf(expense.getId());
        if (expenseId.equals(id)) {
            handleExpense = expense;
        }
    }

    Map<String, Object> viewModel = Map.of("paymentrequest", expenses, "handleExpense", handleExpense);
    context.render("paymentrequest.html", viewModel);
   };

    public static final Handler submitPaymentRequest = context -> {
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        String id = expenseId; 

        String email = context.formParamAsClass("email", String.class).get();
        long amount = context.formParamAsClass("amount", long.class).get();
        String dateString = context.formParamAsClass("due_date", String.class).get();

        String[] datesplit = dateString.split("-");
        int day = Integer.parseInt(datesplit[2]);
        int month = Integer.parseInt(datesplit[1]);
        int year = Integer.parseInt(datesplit[0]);

        LocalDate date = LocalDate.of(year, month, day);
        Person personLoggedin = WeShareServer.getPersonLoggedIn(context);


        Collection<Expense> expenses = expenseDAO.findExpensesForPerson(personLoggedin);

        for (Expense expense : expenses) {
            String expenseId = String.valueOf(expense.getId());
            if (expenseId.equals(id)) {
                expense.requestPayment(new Person(email), amountOf(amount), date);
                expenseDAO.save(expense);
            }
        }
        context.redirect("/paymentrequest?expenseId=" + expenseId);
    };

    public static final Handler processPayment = context -> {
        ExpenseDAO expenseDAO = ServiceRegistry.lookup(ExpenseDAO.class);

        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<PaymentRequest> paymentRequests = expenseDAO.findPaymentRequestsReceived(personLoggedIn);
        String paymentId = context.formParamAsClass("payment", String.class).get();

        for (PaymentRequest paymentRequest : paymentRequests) {
            if (String.valueOf(paymentRequest.getId()).equals(paymentId)) {
                paymentRequest.pay(personLoggedIn, LocalDate.now());
                Expense paymentExpense = new Expense(personLoggedIn, paymentRequest.getDescription(), paymentRequest.getAmountToPay(), LocalDate.now());
                expenseDAO.save(paymentExpense);
            }
        }
        context.redirect(Routes.PAYMENTREQUEST_RECEIVED);
    };
}
