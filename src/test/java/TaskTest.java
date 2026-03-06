package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    @DisplayName("Создание задачи с корректными данными")
    void testTaskCreation() {
        Task task = new Task(1, "Тестовая задача", "Описание задачи");

        assertEquals(1, task.getId());
        assertEquals("Тестовая задача", task.getTitle());
        assertEquals("Описание задачи", task.getDescription());
        assertFalse(task.isCompleted());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    @DisplayName("Изменение статуса выполнения")
    void testToggleCompletion() {
        Task task = new Task(1, "Задача", "Описание");

        assertFalse(task.isCompleted());

        task.setCompleted(true);
        assertTrue(task.isCompleted());

        task.setCompleted(false);
        assertFalse(task.isCompleted());
    }

    @Test
    @DisplayName("Изменение названия и описания")
    void testSetters() {
        Task task = new Task(1, "Старое название", "Старое описание");

        task.setTitle("Новое название");
        task.setDescription("Новое описание");

        assertEquals("Новое название", task.getTitle());
        assertEquals("Новое описание", task.getDescription());
    }

    @Test
    @DisplayName("toString содержит корректную информацию")
    void testToString() {
        Task task = new Task(1, "Заголовок", "Короткое описание");

        String result = task.toString();

        assertTrue(result.contains("[ ]"));
        assertTrue(result.contains("#1"));
        assertTrue(result.contains("Заголовок"));
        assertTrue(result.contains("Короткое описание"));
    }

    @Test
    @DisplayName("toString обрезает длинное описание")
    void testToStringTruncatesLongDescription() {
        String longDesc = "Это очень длинное описание, которое должно быть обрезано до 30 символов";
        Task task = new Task(1, "Заголовок", longDesc);

        String result = task.toString();

        assertTrue(result.contains("..."));
        assertFalse(result.contains(longDesc));
    }

    @Test
    @DisplayName("toString показывает выполненную задачу")
    void testToStringCompletedTask() {
        Task task = new Task(1, "Задача", "Описание");
        task.setCompleted(true);

        String result = task.toString();

        assertTrue(result.contains("[✓]"));
    }
}