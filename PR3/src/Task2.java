import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class Task2 {

    private static final ExecutorService executorService = Executors.newWorkStealingPool();

    // Завдання для підрахунку символів
    static class FileTask implements Callable<FileResult> {
        private final File file;

        public FileTask(File file) {
            this.file = file;
        }

        @Override
        public FileResult call() throws Exception {
            long characterCount = 0;
            try {
                List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
                for (String line : lines) {
                    characterCount += line.length();
                }
            } catch (MalformedInputException e) {
                System.err.println("File " + file.getName() + " has invalid encoding. Skipping...");
                return new FileResult(file.getName(), 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new FileResult(file.getName(), characterCount);
        }
    }

    static class FileResult {
        private final String fileName;
        private final long characterCount;

        public FileResult(String fileName, long characterCount) {
            this.fileName = fileName;
            this.characterCount = characterCount;
        }

        public String getFileName() {
            return fileName;
        }

        public long getCharacterCount() {
            return characterCount;
        }

        @Override
        public String toString() {
            return "File: " + fileName + ", Character Count: " + characterCount;
        }
    }

    // Рекурсивний метод для обробки файлів в директорії та підкаталогах
    public static void processDirectory(String directoryPath) throws InterruptedException, ExecutionException {
        File directory = new File(directoryPath);

        // Перевіряємо чи існує директорія
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Directory not found or is not a directory.");
            return;
        }

        List<File> files = new ArrayList<>();
        findTextFiles(directory, files);

        if (!files.isEmpty()) {
            List<Future<FileResult>> futures = new ArrayList<>();
            for (File file : files) {
                futures.add(executorService.submit(new FileTask(file)));
            }

            // Результати
            for (Future<FileResult> future : futures) {
                FileResult result = future.get();
                System.out.println(result);
            }
        } else {
            System.out.println("No text files found in the directory or its subdirectories.");
        }
    }

    // Рекурсивний метод для знаходження текстових файлів
    private static void findTextFiles(File directory, List<File> files) {
        File[] fileList = directory.listFiles();
        if (fileList != null) {
            for (File file : fileList) {
                if (file.isDirectory()) {
                    // Рекурсивний пошук в папці
                    findTextFiles(file, files);
                } else if (file.getName().endsWith(".txt")) {
                    // Додати текстовий файл в список
                    files.add(file);
                }
            }
        }
    }

    // Головний метод
    public static void main(String[] args) {
        // Отримати директорію
        System.out.println("Enter the directory path:");
        Scanner scanner = new Scanner(System.in);
        String directoryPath = scanner.nextLine().replace("\\", "/"); // Заміна на правильний роздільник для шляху

        try {
            processDirectory(directoryPath);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown(); // Завершити роботу ExecutorService
        }
    }
}
