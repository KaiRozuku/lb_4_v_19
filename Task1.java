import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Task1 {

    public static void main(String[] args) {
        String[] fileNames = {"file1.txt", "file2.txt", "file3.txt"}; // Імена файлів з текстом
        List<String> sentences = new ArrayList<>();

        CompletableFuture<Void> loadSentences = CompletableFuture.runAsync(() -> {
            long startTime = System.nanoTime();
            try {
                for (String fileName : fileNames) {
                    Path filePath = Paths.get(fileName);  // Create a Path object once
                    try {
                        List<String> lines = Files.readAllLines(filePath);
                        sentences.addAll(lines);
                        sentences.add("");
                    } catch (NoSuchFileException e) {
                        System.err.println("Файл не знайдено: " + fileName);
                    } catch (IOException e) {
                        System.err.println("Помилка при зчитуванні файлу: " + fileName);
                    }
                }
                System.out.println("Речення завантажені з файлів.");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            long endTime = System.nanoTime();
            System.out.println("Час завантаження речень: " + (endTime - startTime) / 1_000_000.0 + " мс");
        });


        // Виведення початкових речень
        CompletableFuture<Void> displaySentences = loadSentences.thenRunAsync(() -> {
            long startTime = System.nanoTime();
            System.out.println("\nПочаткові речення:");
            sentences.forEach(System.out::println);
            long endTime = System.nanoTime();
            System.out.println("\nЧас виведення початкових речень: " + (endTime - startTime) / 1_000_000.0 + " мс");
        });

        // Видалення усіх літер верхнього та нижнього регістрів
        CompletableFuture<List<String>> processSentences = displaySentences.thenApplyAsync(_ -> {
            long startTime = System.nanoTime();
            List<String> processed = sentences.stream()
                    .map(sentence -> sentence.replaceAll("[a-zA-Z]", ""))
                    .collect(Collectors.toList());
            long endTime = System.nanoTime();
            System.out.println("Час видалення літер: " + (endTime - startTime) / 1_000_000.0 + " мс");
            return processed;
        });

        // Асинхронне обчислення результуючого масиву
        CompletableFuture<List<String>> resultFuture = CompletableFuture.supplyAsync(() -> {
            long startTime = System.nanoTime();
            List<String> result = processSentences.join();
            long endTime = System.nanoTime();
            System.out.println("Час обчислення результату: " + (endTime - startTime) / 1_000_000.0 + " мс");
            return result;
        });

        // Виведення результуючого масиву
        CompletableFuture<Void> displayProcessed = resultFuture.thenAcceptAsync(processed -> {
            long startTime = System.nanoTime();
            System.out.println("\nРезультуючий масив:");
            processed.forEach(System.out::println);
            long endTime = System.nanoTime();
            System.out.println("\nЧас виведення результуючого масиву: " + (endTime - startTime) / 1_000_000.0 + " мс");
        });

        // Заключне повідомлення про завершення
        CompletableFuture<Void> finalStep = displayProcessed.thenRunAsync(() -> System.out.println("Всі асинхронні операції завершені."));

        // Очікуємо завершення всіх асинхронних операцій
        try {
            finalStep.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException();
        }
    }
}