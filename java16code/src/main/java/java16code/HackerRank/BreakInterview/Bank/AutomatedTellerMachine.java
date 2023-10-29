package java16code.HackerRank.BreakInterview.Bank;

import lombok.Getter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.Date;

/*
 * Program to simulate basic operation's with an Automated Teller Machine.
 */
public class AutomatedTellerMachine {

    private static int accountNumber;
    private final String holderName;
    private final String holderAddress;
    private final String openDate;
    private static double balance;
    private static double[] transactions;
    @Getter
    private static String[] transactionsSummary;
    private static int numOfTransactions;
    private static int noOfAccounts = 0;

    public static void main(String[] args )
    {
        Scanner scannerIn = new Scanner(System.in);
        AutomatedTellerMachine atmBankAccount = firstAccount(scannerIn);
        //noinspection InfiniteLoopStatement
        while(true)
        {
            System.out.println("Automated Teller Machine");
            System.out.println("Choose 1 for Withdraw");
            System.out.println("Choose 2 for Deposit");
            System.out.println("Choose 3 for Check Balance");
            System.out.println("Choose 4 for Info Account");
            System.out.println("Choose 5 for Transactions summary");
            System.out.println("Choose 6 for Info of transaction");
            System.out.println("Choose 0 for EXIT");
            System.out.print("Choose the operation you want to perform: ");

            if(scannerIn.hasNextInt()) {
                int n = scannerIn.nextInt();
                switch(n)
                {
                    case 1:
                        withdrawOperation(scannerIn);
                        break;
                    case 2:
                        depositOperation(scannerIn);
                        break;
                    case 3:
                        balanceOperation();
                        break;
                    case 4:
                        accountInfoOperation(atmBankAccount);
                        break;
                    case 5:
                        transactionsSummaryOperation();
                        break;
                    case 6:
                        transactionInfoOperation(scannerIn);
                        break;
                    case 0:
                        exitOperation(scannerIn);
                        break;
                }
            } else {
                invalidOption(scannerIn);
                scannerIn.nextLine();
            }
        }
    }

    private static void withdrawOperation(Scanner s)
    {
        System.out.print("Enter money to be withdrawn:");
        if (s.hasNextDouble())
        {
            double withdraw = s.nextDouble();

            if(balance >= withdraw)
            {
                withdraw(withdraw);
                System.out.println("Please collect your money.");
            }
            else
            {
                System.out.println("Insufficient Balance.");
            }
            System.out.println();
        }
        else
        {
            invalidOption(s);
            s.nextLine();
        }
    }

    private static void depositOperation(Scanner s)
    {
        System.out.print("Enter money to be deposited:");
        if (s.hasNextDouble())
        {

            double deposit = s.nextDouble();
            if(deposit > 0) {
                deposit(deposit);
                System.out.println("Your Money has been successfully deposited.\n");
            } else {
                System.out.println("You don't deposited an amount valid of money.\n");
            }
        }
        else
        {
            invalidOption(s);
            s.nextLine();
        }
    }

    private static void balanceOperation() {
        System.out.println("Balance : " + balance);
        System.out.println("Account number: " + getAccountNum());
        System.out.println("Operations made with this account:" + getNumberOfTransactions());
        System.out.println();
    }

    private static void accountInfoOperation(AutomatedTellerMachine bankAccount) {
        if (!Objects.equals(bankAccount.holderName, "NN")){
            System.out.println(bankAccount.getAccountInfo());
        } else {
            System.out.println("Without accounts yet.\n");
        }
    }

    private static void transactionsSummaryOperation() {
        System.out.println("Transactions summary:");
        Arrays.stream(getTransactionsSummary())
            .filter(Objects::nonNull)
            .forEach(System.out::println);
        System.out.println();
    }

    private static void transactionInfoOperation(Scanner s) {
        System.out.print("Enter the number of the transaction to look up:");
        if (s.hasNextInt())
        {
            int n = s.nextInt();
            System.out.println(getTransactionInfo(n) + "\n");
        } else {
            invalidOption(s);
            s.nextLine();
        }
    }

    private static void exitOperation(Scanner s) {
        s.close();
        System.out.println("\nThank you for visit us, come back soon!!!.");
        System.exit(0);
    }

    private static int getAccountNum() {
        return accountNumber;
    }

    private String getAccountInfo() {
        return "Account number: " + accountNumber + "\nCustomer Name: " + holderName + "\nHolder's Address: "
            + holderAddress + "\nOpen Date: " + openDate + "\nBalance:" + balance + "\n";
    }

    private static int getNumberOfTransactions() {
        return numOfTransactions;
    }

    private static String getTransactionInfo(int n) {
        String transaction = transactionsSummary[n];
        return Objects.requireNonNullElse(transaction, "No transaction exists with that number.");
    }

    private AutomatedTellerMachine(String YouName, double MoneyToAccount, String YouAddress, String actualDate) {
        holderName = YouName;
        balance = MoneyToAccount;
        holderAddress = YouAddress;
        openDate = actualDate;
        noOfAccounts++;
        accountNumber = noOfAccounts;
        transactions = new double[100];
        transactionsSummary = new String[100];
        transactions[0] = balance;
        transactionsSummary[0] = "A balance of : $" + balance + " was deposited.";
        numOfTransactions = 1;
    }

    private static void deposit(double amount) {

        if (amount <= 0) {
            System.out.println("Amount to be deposited should be positive");
        } else {
            balance = balance + amount;
            transactions[numOfTransactions] = amount;
            transactionsSummary[numOfTransactions] = "$" + amount + " was deposited.";
            numOfTransactions++;
        }
    }

    private static void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Amount to be withdrawn should be positive");
        } else {
            if (balance < amount) {
                System.out.println("Insufficient balance");
            } else {
                balance = balance - amount;
                transactions[numOfTransactions] = amount;
                transactionsSummary[numOfTransactions] = "$" + amount + " was withdrawn.";
                numOfTransactions++;
            }
        }
    }

    private static AutomatedTellerMachine firstAccount(Scanner s) {
        System.out.println("WELCOME!!!, Let's go to create your first account bank!\n");
        AutomatedTellerMachine firstAccount;
        double moneyToAccount = 0.0;
        boolean setMoney = false;
        //new BankAccount("NN", 0.0, "AnyAddress", "0001-01-01");

        System.out.println("- Type you Full Name: ");
        String YouName = s.nextLine();
        do {
            System.out.println("- Money you want to ingress at Account: ");
            if(s.hasNextDouble()) {
                moneyToAccount = s.nextDouble();
                setMoney = true;
            } else {
                invalidOption(s);
                s.nextLine();
            }
        } while (!setMoney);

        String youAddress = "";
        System.out.println("- Type you Address: ");
        do {
            if(s.hasNext()) youAddress = s.nextLine();
        } while (youAddress.isEmpty());

        String actualDate = new Date().toString();

        firstAccount = new AutomatedTellerMachine(YouName, moneyToAccount, youAddress, actualDate);
        System.out.println("--- SUMMARY ACCOUNT OPENED ---");
        System.out.println(firstAccount.getAccountInfo());

        return firstAccount;
    }

    private static void invalidOption(Scanner s) {
        System.out.print("\nYou has typed an invalid option!!!\n\n");
        s.reset();
    }

}
