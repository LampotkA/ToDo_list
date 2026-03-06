package org.example;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TodoAppIntegrationTest {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private File dataFile;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        dataFile = new File("tasks.json");
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Полный сценарий работы приложения")
    void testFullScenario() {
        TodoApp app = new TodoApp();

        try {
            addTask(app, "Интеграционная задача", "Тестовое описание");

            Task task = findTask(app, 1);
            assertNotNull(task, "Задача должна быть добавлена");
            assertEquals("Интеграционная задача", task.getTitle());

            toggleTask(app, 1);
            task = findTask(app, 1);
            assertTrue(task.isCompleted(), "Задача должна быть отмечена выполненной");

            editTask(app, 1, "Обновлённая задача", "Обновлённое описание");
            task = findTask(app, 1);
            assertEquals("Обновлённая задача", task.getTitle());

            saveTasks(app);
            assertTrue(dataFile.exists(), "Файл должен быть создан");

            deleteTask(app, 1);
            task = findTask(app, 1);
            assertNull(task, "Задача должна быть удалена");

        } catch (Exception e) {
            fail("Ошибка в интеграционном тесте: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("Проверка сохранения данных между сессиями")
    void testDataPersistence() throws Exception {
        TodoApp app1 = new TodoApp();
        addTask(app1, "Задача для сохранения", "Описание");
        saveTasks(app1);

        assertTrue(dataFile.exists(), "Файл должен существовать");
        String content = Files.readString(dataFile.toPath());
        assertTrue(content.contains("Задача для сохранения"));

        TodoApp app2 = new TodoApp();
        Task loadedTask = findTask(app2, 1);

        assertNotNull(loadedTask, "Задача должна загрузиться");
        assertEquals("Задача для сохранения", loadedTask.getTitle());
    }

    private void addTask(TodoApp app, String title, String desc) throws Exception {
        java.lang.reflect.Method method = TodoApp.class.getDeclaredMethod(
                "addTask", java.util.Scanner.class);
        method.setAccessible(true);

        String input = title + "\n" + desc + "\n";
        java.util.Scanner scanner = new java.util.Scanner(
                new ByteArrayInputStream(input.getBytes()));
        method.invoke(app, scanner);
        scanner.close();
    }

    private Task findTask(TodoApp app, int id) throws Exception {
        java.lang.reflect.Method method = TodoApp.class.getDeclaredMethod(
                "findTaskById", int.class);
        method.setAccessible(true);
        return (Task) method.invoke(app, id);
    }

    private void toggleTask(TodoApp app, int id) throws Exception {
        java.lang.reflect.Method method = TodoApp.class.getDeclaredMethod(
                "toggleTaskStatus", java.util.Scanner.class);
        method.setAccessible(true);

        String input = id + "\n";
        java.util.Scanner scanner = new java.util.Scanner(
                new ByteArrayInputStream(input.getBytes()));
        method.invoke(app, scanner);
        scanner.close();
    }

    private void editTask(TodoApp app, int id, String newTitle, String newDesc)
            throws Exception {
        java.lang.reflect.Method method = TodoApp.class.getDeclaredMethod(
                "editTask", java.util.Scanner.class);
        method.setAccessible(true);

        String input = id + "\n" + newTitle + "\n" + newDesc + "\n";
        java.util.Scanner scanner = new java.util.Scanner(
                new ByteArrayInputStream(input.getBytes()));
        method.invoke(app, scanner);
        scanner.close();
    }

    private void deleteTask(TodoApp app, int id) throws Exception {
        java.lang.reflect.Method method = TodoApp.class.getDeclaredMethod(
                "deleteTask", java.util.Scanner.class);
        method.setAccessible(true);

        String input = id + "\nда\n";
        java.util.Scanner scanner = new java.util.Scanner(
                new ByteArrayInputStream(input.getBytes()));
        method.invoke(app, scanner);
        scanner.close();
    }

    private void saveTasks(TodoApp app) throws Exception {
        java.lang.reflect.Method method = TodoApp.class.getDeclaredMethod("saveTasks");
        method.setAccessible(true);
        method.invoke(app);
    }
}