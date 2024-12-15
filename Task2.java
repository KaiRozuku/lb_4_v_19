import java.util.concurrent.CompletableFuture;
import java.util.Random;

public class Task2 {

    public static void main(String[] args) {
        double[] numbers = new double[20];
        Random random = new Random();

        // Генерація випадкових чисел
        CompletableFuture<Void> generateNumbers = CompletableFuture.runAsync(() -> {
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = random.nextDouble() * 10; // Генеруємо числа від 0 до 10
            }
            System.out.println("Випадкові числа згенеровані.");
        });

        // Виведення вихідної послідовності
        generateNumbers.thenRunAsync(() -> {
            System.out.println("Вихідна послідовність:");
            for (double num : numbers) {
                System.out.printf("%.3f ", num);
            }
            System.out.println();
        }).join();

        // Асинхронне обчислення суми
        long startTime = System.nanoTime();

        CompletableFuture<Double> resultFuture = CompletableFuture.supplyAsync(() -> calculateSum(numbers));

        // Додавання додаткових асинхронних операцій
        resultFuture.thenApplyAsync(result -> {
                    System.out.printf("Результат обчислення: %.3f\n", result);
                    return result;
                }).thenAcceptAsync(_ -> System.out.println("Процес обчислення завершено."))
                .thenRunAsync(() -> {
                    long endTime = System.nanoTime();
                    System.out.println("Час виконання: " + (endTime - startTime) / 1_000_000.0 + " мс");
                }).join();
    }

    private static double calculateSum(double[] numbers) {
        double sum = 0;
        for (int i = 0; i < numbers.length - 1; i++) {
            sum += numbers[i] * numbers[i + 1];
        }
        return sum;
    }
}
