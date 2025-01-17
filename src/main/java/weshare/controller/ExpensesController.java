package weshare.controller;

import io.javalin.http.Handler;
import org.javamoney.moneta.function.MonetaryFunctions;
import org.jetbrains.annotations.NotNull;
import weshare.model.Expense;
import weshare.model.Person;
import weshare.persistence.ExpenseDAO;
import weshare.server.ServiceRegistry;
import weshare.server.WeShareServer;

import javax.money.MonetaryAmount;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;


import static weshare.controller.AddExpensesController.getTotalExpense;
import static weshare.model.MoneyHelper.amountOf;

public class ExpensesController {


    public static final Handler view = context -> {
        ExpenseDAO expensesDAO = ServiceRegistry.lookup(ExpenseDAO.class);
        Person personLoggedIn = WeShareServer.getPersonLoggedIn(context);
    
        Collection<Expense> expenses = expensesDAO.findExpensesForPerson(personLoggedIn);
    
        MonetaryAmount totalAmount = amountOf(0);
    
        // creating a list of expenses to filter out fully paid ones
        ArrayList<Expense> filteredExpenses = new ArrayList<>(expenses);
    
        for (Expense expense : expenses) {
            totalAmount = totalAmount.add(expense.getAmount());
        }
    
        filteredExpenses.removeIf(Expense::isFullyPaidByOthers);
    
        String formattedGrandTotal = String.format("ZAR %.2f", totalAmount.getNumber().doubleValue());
        String grandTotal = formattedGrandTotal.split(",")[0];
    
        Map<String, Object> viewModel = Map.of(
            "expenses", filteredExpenses, 
            "grandTotal", grandTotal
        );
    
        context.render("expenses.html", viewModel);
    };
    
}
