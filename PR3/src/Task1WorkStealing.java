import java.util.Scanner;
import java.util.concurrent.*;

public class Task1WorkStealing {
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
        int minValue = findMinUsingWorkStealing(array);
        long endTime = System.nanoTime();

        System.out.println("Minimum value: " + minValue);
        System.out.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
    }

    // Генерація випадкових чисел
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

    // Пошук мінімального значення
    private static int findMinUsingWorkStealing(int[][] array) {
        int numThreads = Runtime.getRuntime().availableProcessors(); // Кількість доступних процесорів
        ForkJoinPool forkJoinPool = new ForkJoinPool(numThreads);

        // Рекурсивне завдання для пошуку мінімуму
        return forkJoinPool.invoke(new FindMinTask(array, 0, array.length));
    }

    // Завдання для пошуку мінімального елемента
    private static class FindMinTask extends RecursiveTask<Integer> {
        private final int[][] array;
        private final int startRow;
        private final int endRow;

        public FindMinTask(int[][] array, int startRow, int endRow) {
            this.array = array;
            this.startRow = startRow;
            this.endRow = endRow;
        }

        @Override
        protected Integer compute() {
            if (endRow - startRow <= array.length / Runtime.getRuntime().availableProcessors()) {
                // Якщо кількість рядків мала, виконуємо пошук мінімуму без поділу
                int min = Integer.MAX_VALUE;
                for (int i = startRow; i < endRow; i++) {
                    for (int value : array[i]) {
                        min = Math.min(min, value);
                    }
                }
                return min;
            } else {
                // Інакше ділимо завдання на підзадачі
                int midRow = (startRow + endRow) / 2;
                FindMinTask leftTask = new FindMinTask(array, startRow, midRow);
                FindMinTask rightTask = new FindMinTask(array, midRow, endRow);

                leftTask.fork();
                int rightMin = rightTask.compute(); // Результат з правої підзадачі
                int leftMin = leftTask.join(); // Результат з лівої підзадачі

                return Math.min(leftMin, rightMin); // Повертаємо мінімум з двох підзадач
            }
        }
    }
}
