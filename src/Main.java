import java.util.Random;

class Account {
    private double balance;

    public synchronized void deposit(double amount) {
        balance += amount;
        System.out.printf("Deposited: %.2f, New Balance: %.2f%n", amount, balance);
        notifyAll(); // Уведомляем ожидающие потоки о том, что баланс пополнен
    }

    public synchronized void withdraw(double amount) throws InterruptedException {
        while (balance < amount) {
            System.out.printf("Waiting for balance to reach: %.2f%n", amount);
            wait(); // Ожидаем, пока баланс не станет достаточным
        }
        balance -= amount;
        System.out.printf("Withdrawn: %.2f, New Balance: %.2f%n", amount, balance);
    }

    public synchronized double getBalance() {
        return balance;
    }
}

public class Main {
    public static void main(String[] args) {
        Account account = new Account();
        Random random = new Random();

        // Поток для пополнения счета
        Thread depositThread = new Thread(() -> {
            try {
                while (true) {
                    double amount = random.nextDouble() * 100; // Случайная сумма до 100
                    account.deposit(amount);
                    Thread.sleep(500); // Задержка между пополнениями
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        depositThread.start();

        // Основной поток для снятия средств
        Thread withdrawThread = new Thread(() -> {
            try {
                while (true) {
                    double amountToWithdraw = 50; // Сумма для снятия
                    account.withdraw(amountToWithdraw);
                    System.out.printf("Remaining Balance: %.2f%n", account.getBalance());
                    Thread.sleep(1000); // Задержка между попытками снятия
                }
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        withdrawThread.start();
    }
}
