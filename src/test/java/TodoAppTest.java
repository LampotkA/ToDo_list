package org.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import static org.junit.jupiter.api.Assertions.*;

class TodoAppTest {

    @TempDir
    Path tempDir;

    private TodoApp todoApp;
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private File tempDataFile;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));

        tempDataFile = tempDir.resolve("test_tasks.json").toFile();

        todoApp = new TodoApp();
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        File dataFile = new File("tasks.json");
        if (dataFile.exists()) {
            dataFile.delete();
        }
    }

    @Test
    @DisplayName("Приложение создается с пустым списком задач")
    void testInitialState() {
        java.lang.reflect.Method method;
        try {
            method = TodoApp.class.getDeclaredMethod("showTasks");
            method.setAccessible(true);
            method.invoke(todoApp);

            String output = outputStream.toString();
            assertTrue(output.contains("Список задач пуст"));
        } catch (Exception e) {
            fail("Ошибка при вызове метода: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Добавление задачи через рефлексию")
    void testAddTask() throws Exception {
        java.lang.reflect.Method addTaskMethod = TodoApp.class.getDeclaredMethod(
                "addTask", java.util.Scanner.class);
        addTaskMethod.setAccessible(true);

        String input = "Тестовая задача\nОписание теста\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        addTaskMethod.invoke(todoApp, scanner);

        String output = outputStream.toString();
        assertTrue(output.contains("Задача успешно добавлена"));

        scanner.close();
    }

    @Test
    @DisplayName("Поиск задачи по ID")
    void testFindTaskById() throws Exception {
        addTaskViaReflection("Задача 1", "Описание 1");

        java.lang.reflect.Method findMethod = TodoApp.class.getDeclaredMethod(
                "findTaskById", int.class);
        findMethod.setAccessible(true);

        Task found = (Task) findMethod.invoke(todoApp, 1);
        assertNotNull(found);
        assertEquals("Задача 1", found.getTitle());

        Task notFound = (Task) findMethod.invoke(todoApp, 999);
        assertNull(notFound);
    }

    @Test
    @DisplayName("Сохранение и загрузка задач")
    void testSaveAndLoad() throws Exception {
        addTaskViaReflection("Сохраняемая задача", "Описание для сохранения");

        java.lang.reflect.Method saveMethod = TodoApp.class.getDeclaredMethod("saveTasks");
        saveMethod.setAccessible(true);
        saveMethod.invoke(todoApp);

        File dataFile = new File("tasks.json");
        assertTrue(dataFile.exists());

        String content = new String(java.nio.file.Files.readAllBytes(dataFile.toPath()));
        assertTrue(content.contains("Сохраняемая задача"));
        assertTrue(content.contains("Описание для сохранения"));
    }

    @Test
    @DisplayName("Удаление задачи")
    void testDeleteTask() throws Exception {
        addTaskViaReflection("Удаляемая задача", "Описание");

        java.lang.reflect.Method deleteMethod = TodoApp.class.getDeclaredMethod(
                "deleteTask", Scanner.class);
        deleteMethod.setAccessible(true);

        String input = "1\nда\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        deleteMethod.invoke(todoApp, scanner);

        java.lang.reflect.Method findMethod = TodoApp.class.getDeclaredMethod(
                "findTaskById", int.class);
        findMethod.setAccessible(true);
        Task task = (Task) findMethod.invoke(todoApp, 1);

        assertNull(task, "Задача с ID=1 должна быть удалена");

        scanner.close();
    }

    @Test
    @DisplayName("Редактирование задачи")
    void testEditTask() throws Exception {
        addTaskViaReflection("Старое название", "Старое описание");

        java.lang.reflect.Method editMethod = TodoApp.class.getDeclaredMethod(
                "editTask", java.util.Scanner.class);
        editMethod.setAccessible(true);

        String input = "1\nНовое название\nНовое описание\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        editMethod.invoke(todoApp, scanner);

        java.lang.reflect.Method findMethod = TodoApp.class.getDeclaredMethod(
                "findTaskById", int.class);
        findMethod.setAccessible(true);
        Task task = (Task) findMethod.invoke(todoApp, 1);

        assertEquals("Новое название", task.getTitle());
        assertEquals("Новое описание", task.getDescription());

        scanner.close();
    }

    @Test
    @DisplayName("Переключение статуса задачи")
    void testToggleTaskStatus() throws Exception {
        addTaskViaReflection("Задача для переключения", "Описание");

        java.lang.reflect.Method toggleMethod = TodoApp.class.getDeclaredMethod(
                "toggleTaskStatus", java.util.Scanner.class);
        toggleMethod.setAccessible(true);

        String input = "1\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        toggleMethod.invoke(todoApp, scanner);
        scanner.close();

        java.lang.reflect.Method findMethod = TodoApp.class.getDeclaredMethod(
                "findTaskById", int.class);
        findMethod.setAccessible(true);
        Task task = (Task) findMethod.invoke(todoApp, 1);

        assertTrue(task.isCompleted());

        outputStream.reset();
        scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        toggleMethod.invoke(todoApp, scanner);
        scanner.close();

        task = (Task) findMethod.invoke(todoApp, 1);
        assertFalse(task.isCompleted());
    }

    @Test
    @DisplayName("Обработка пустого ввода при добавлении задачи")
    void testAddTaskWithEmptyTitle() throws Exception {
        java.lang.reflect.Method addTaskMethod = TodoApp.class.getDeclaredMethod(
                "addTask", java.util.Scanner.class);
        addTaskMethod.setAccessible(true);

        String input = "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        addTaskMethod.invoke(todoApp, scanner);

        String output = outputStream.toString();
        assertTrue(output.contains("Название не может быть пустым"));

        scanner.close();
    }

    private void addTaskViaReflection(String title, String description) throws Exception {
        java.lang.reflect.Method addTaskMethod = TodoApp.class.getDeclaredMethod(
                "addTask", java.util.Scanner.class);
        addTaskMethod.setAccessible(true);

        String input = title + "\n" + description + "\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));
        addTaskMethod.invoke(todoApp, scanner);
        scanner.close();

        outputStream.reset();
    }
}