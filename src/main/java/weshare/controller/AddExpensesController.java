package weshare.controller;

import io.javalin.http.Handler;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.Routes;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import static weshare.model.DateHelper.TODAY;
import static weshare.model.MoneyHelper.amountOf;

public class AddExpensesController {
    public static final Handler newExpense = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        Map<String, Object> viewModel = Map.of("expenses", expenses);
        context.render("newexpense.html", viewModel);
    };

    public static MonetaryAmount getTotalExpense(Collection<Expense> expenses){
        MonetaryAmount TOTAL_RANDS = amountOf(0);
        for(Expense expense : expenses){
            TOTAL_RANDS = TOTAL_RANDS.add(expense.getAmount());
        }
        return TOTAL_RANDS;
    }


    public static final Handler addedExpense = context -> {

        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
        context.redirect(Routes.EXPENSES);



        String description = context.formParamAsClass("description", String.class)
                .check(Objects::nonNull, "Description is required")
                .get();

        String date = context.formParamAsClass("date", String.class)
                .check(Objects::nonNull, "Date is required")
                .get();


        String amount = context.formParamAsClass("amount", String.class)
                .check(Objects::nonNull, "Amount is required")
                .get();

        // had to manually parse the date string using DateTimeFormatter due to internal server error
        LocalDate dateObj = TODAY;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            dateObj = LocalDate.parse(date, formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }


        expensesDAO.save(new Expense(personLoggedIn,description,amountOf(Integer.parseInt(amount)), LocalDate.now()));

        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
        MonetaryAmount totalExpenses = getTotalExpense(expenses);

        Map<String, Object> viewModel = Map.of("expenses", expenses,"totalExpenses",totalExpenses);
        context.sessionAttribute(WeShareServer.SESSION_USER_KEY,personLoggedIn);
        context.redirect(Routes.EXPENSES);
    };

    

}
