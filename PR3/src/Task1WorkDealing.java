import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Task1WorkDealing {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of rows in the array: ");
        int rows = scanner.nextInt();

        System.out.print("Enter the number of columns in the array: ");
        int cols = scanner.nextInt();

        int[][] array = new int[rows][cols];
        generateRandomArray(array);

        System.out.println("Generated Array:");
        printArray(array);

        long startTime = System.nanoTime();
        int minValue = findMinUsingWorkDealing(array);
        long endTime = System.nanoTime();

        System.out.println("Minimum value: " + minValue);
        System.out.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
    }

    // Генерація випадкових чисел для масиву
    private static void generateRandomArray(int[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                array[i][j] = (int) (Math.random() * 100);
            }
        }
    }

    // Виведення масиву
    private static void printArray(int[][] array) {
        for (int[] row : array) {
            for (int value : row) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }

    // Пошук мінімального значення за допомогою багатозадачності
    private static int findMinUsingWorkDealing(int[][] array) {
        int numThreads = Runtime.getRuntime().availableProcessors(); // Кількість доступних процесорів
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        AtomicInteger min = new AtomicInteger(Integer.MAX_VALUE); // ініціалізація мінімального значення

        // Паралельне виконання для кожного рядка масиву
        for (int[] row : array) {
            executorService.submit(() -> {
                for (int value : row) {
                    min.updateAndGet(currentMin -> Math.min(currentMin, value)); // Оновлення мінімуму
                }
            });
        }

        executorService.shutdown();
        try {
            // Очікування завершення всіх завдань
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        return min.get(); // Повернення мінімального значення
    }
}
