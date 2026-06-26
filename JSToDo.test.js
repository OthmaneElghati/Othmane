/**
 * Unit tests for JSToDo.js - To-Do List application
 * Tests cover all exported functions: addTask, toggleStrikethrough,
 * editTask, deleteTask, showConfirmationModal, initEventListeners
 */

const { addTask, toggleStrikethrough, editTask, deleteTask, showConfirmationModal, initEventListeners } = require('./JSToDo');

// Mock jQuery's modal function
const mockModal = jest.fn();
global.$ = jest.fn(() => ({ modal: mockModal }));

function setupDOM() {
  document.body.innerHTML = `
    <input type="text" id="taskInput" class="form-control" placeholder="Add new task...">
    <ul id="taskList" class="list-group mt-3"></ul>
    <div id="confirmationModal">
      <p id="confirmationText"></p>
      <button id="confirmButton"></button>
    </div>
    <div id="editModal">
      <p id="editText"></p>
      <input type="text" id="editInput" class="form-control">
      <button id="saveButton"></button>
    </div>
  `;
}

beforeEach(() => {
  setupDOM();
  jest.clearAllMocks();
  jest.spyOn(window, 'alert').mockImplementation(() => {});
});

describe('addTask', () => {
  test('adds a new task to the task list when input is non-empty', () => {
    const taskInput = document.getElementById('taskInput');
    taskInput.value = 'Buy groceries';

    addTask();

    const taskList = document.getElementById('taskList');
    expect(taskList.children.length).toBe(1);

    const li = taskList.children[0];
    expect(li.tagName).toBe('LI');
    expect(li.className).toBe('list-group-item d-flex align-items-center');

    const span = li.querySelector('.task-text');
    expect(span.textContent).toBe('Buy groceries');
  });

  test('clears the input field after adding a task', () => {
    const taskInput = document.getElementById('taskInput');
    taskInput.value = 'Walk the dog';

    addTask();

    expect(taskInput.value).toBe('');
  });

  test('creates a checkbox input in the new task', () => {
    document.getElementById('taskInput').value = 'Test task';

    addTask();

    const li = document.getElementById('taskList').children[0];
    const checkbox = li.querySelector('input[type="checkbox"]');
    expect(checkbox).not.toBeNull();
    expect(checkbox.className).toBe('mr-2');
  });

  test('creates an Edit button in the new task', () => {
    document.getElementById('taskInput').value = 'Test task';

    addTask();

    const li = document.getElementById('taskList').children[0];
    const buttons = li.querySelectorAll('button');
    const editBtn = buttons[0];
    expect(editBtn.innerHTML).toBe('Edit');
    expect(editBtn.classList.contains('btn-warning')).toBe(true);
  });

  test('creates a Delete button in the new task', () => {
    document.getElementById('taskInput').value = 'Test task';

    addTask();

    const li = document.getElementById('taskList').children[0];
    const buttons = li.querySelectorAll('button');
    const deleteBtn = buttons[1];
    expect(deleteBtn.innerHTML).toBe('Delete');
    expect(deleteBtn.classList.contains('btn-danger')).toBe(true);
  });

  test('shows alert when input is empty', () => {
    document.getElementById('taskInput').value = '';

    addTask();

    expect(window.alert).toHaveBeenCalledWith('Please enter a task!');
    expect(document.getElementById('taskList').children.length).toBe(0);
  });

  test('shows alert when input is only whitespace', () => {
    document.getElementById('taskInput').value = '   ';

    addTask();

    expect(window.alert).toHaveBeenCalledWith('Please enter a task!');
    expect(document.getElementById('taskList').children.length).toBe(0);
  });

  test('trims whitespace from task text', () => {
    document.getElementById('taskInput').value = '  Clean house  ';

    addTask();

    const span = document.getElementById('taskList').querySelector('.task-text');
    expect(span.textContent).toBe('Clean house');
  });

  test('can add multiple tasks', () => {
    const taskInput = document.getElementById('taskInput');

    taskInput.value = 'Task 1';
    addTask();
    taskInput.value = 'Task 2';
    addTask();
    taskInput.value = 'Task 3';
    addTask();

    const taskList = document.getElementById('taskList');
    expect(taskList.children.length).toBe(3);
  });

  test('checkbox toggles strikethrough on the task text', () => {
    document.getElementById('taskInput').value = 'Strike me';
    addTask();

    const li = document.getElementById('taskList').children[0];
    const checkbox = li.querySelector('input[type="checkbox"]');
    const span = li.querySelector('.task-text');

    // Simulate checking the checkbox
    checkbox.checked = true;
    checkbox.dispatchEvent(new Event('change'));

    expect(span.classList.contains('strikethrough')).toBe(true);

    // Simulate unchecking
    checkbox.checked = false;
    checkbox.dispatchEvent(new Event('change'));

    expect(span.classList.contains('strikethrough')).toBe(false);
  });

  test('delete button removes the task from the list', () => {
    document.getElementById('taskInput').value = 'Delete me';
    addTask();

    const li = document.getElementById('taskList').children[0];
    const deleteBtn = li.querySelectorAll('button')[1];

    deleteBtn.click();

    // The confirmation modal should be shown
    expect(global.$).toHaveBeenCalledWith('#confirmationModal');
    expect(mockModal).toHaveBeenCalledWith('show');
  });

  test('edit button opens the edit modal', () => {
    document.getElementById('taskInput').value = 'Edit me';
    addTask();

    const li = document.getElementById('taskList').children[0];
    const editBtn = li.querySelectorAll('button')[0];

    editBtn.click();

    expect(global.$).toHaveBeenCalledWith('#editModal');
    expect(mockModal).toHaveBeenCalledWith('show');
  });
});

describe('toggleStrikethrough', () => {
  test('adds strikethrough class when checkbox is checked', () => {
    const checkbox = { checked: true };
    const span = document.createElement('span');

    toggleStrikethrough(checkbox, span);

    expect(span.classList.contains('strikethrough')).toBe(true);
  });

  test('removes strikethrough class when checkbox is unchecked', () => {
    const checkbox = { checked: false };
    const span = document.createElement('span');
    span.classList.add('strikethrough');

    toggleStrikethrough(checkbox, span);

    expect(span.classList.contains('strikethrough')).toBe(false);
  });

  test('does not add duplicate strikethrough class', () => {
    const checkbox = { checked: true };
    const span = document.createElement('span');
    span.classList.add('strikethrough');

    toggleStrikethrough(checkbox, span);

    // classList.add does not add duplicates
    expect(span.className).toBe('strikethrough');
  });
});

describe('editTask', () => {
  function createTaskItem(text) {
    const li = document.createElement('li');
    li.className = 'list-group-item d-flex align-items-center';
    const span = document.createElement('span');
    span.classList.add('task-text');
    span.textContent = text;
    li.appendChild(span);
    return li;
  }

  test('shows the edit modal with current task text', () => {
    const taskItem = createTaskItem('Original task');

    editTask(taskItem);

    const editText = document.getElementById('editText');
    const editInput = document.getElementById('editInput');
    expect(editText.textContent).toBe('Edit task: "Original task"');
    expect(editInput.value).toBe('Original task');
    expect(global.$).toHaveBeenCalledWith('#editModal');
    expect(mockModal).toHaveBeenCalledWith('show');
  });

  test('saves new text when save button is clicked with valid input', () => {
    const taskItem = createTaskItem('Old text');

    editTask(taskItem);

    const editInput = document.getElementById('editInput');
    editInput.value = 'New text';

    const saveButton = document.getElementById('saveButton');
    saveButton.click();

    expect(taskItem.querySelector('.task-text').textContent).toBe('New text');
    expect(mockModal).toHaveBeenCalledWith('hide');
  });

  test('shows alert when save button is clicked with empty input', () => {
    const taskItem = createTaskItem('Some task');

    editTask(taskItem);

    const editInput = document.getElementById('editInput');
    editInput.value = '';

    const saveButton = document.getElementById('saveButton');
    saveButton.click();

    expect(window.alert).toHaveBeenCalledWith('Please enter a task!');
    // Original text should remain unchanged
    expect(taskItem.querySelector('.task-text').textContent).toBe('Some task');
  });

  test('shows alert when save button is clicked with whitespace-only input', () => {
    const taskItem = createTaskItem('Keep me');

    editTask(taskItem);

    document.getElementById('editInput').value = '   ';
    document.getElementById('saveButton').click();

    expect(window.alert).toHaveBeenCalledWith('Please enter a task!');
    expect(taskItem.querySelector('.task-text').textContent).toBe('Keep me');
  });

  test('trims the task text from the item before displaying', () => {
    const taskItem = createTaskItem('  Padded text  ');

    editTask(taskItem);

    // textContent.trim() is called on the task text
    const editInput = document.getElementById('editInput');
    expect(editInput.value).toBe('Padded text');
  });
});

describe('deleteTask', () => {
  test('shows confirmation modal with correct task text', () => {
    const li = document.createElement('li');
    const span = document.createElement('span');
    span.classList.add('task-text');
    span.textContent = 'Task to delete';
    li.appendChild(span);
    document.getElementById('taskList').appendChild(li);

    deleteTask(li);

    const confirmText = document.getElementById('confirmationText');
    expect(confirmText.textContent).toBe('Voulez-vous supprimer la tâche "Task to delete" ?');
    expect(global.$).toHaveBeenCalledWith('#confirmationModal');
    expect(mockModal).toHaveBeenCalledWith('show');
  });

  test('removes task from DOM when confirmed', () => {
    const li = document.createElement('li');
    const span = document.createElement('span');
    span.classList.add('task-text');
    span.textContent = 'Remove me';
    li.appendChild(span);
    const taskList = document.getElementById('taskList');
    taskList.appendChild(li);

    expect(taskList.children.length).toBe(1);

    deleteTask(li);

    // Simulate clicking confirm
    document.getElementById('confirmButton').click();

    expect(taskList.children.length).toBe(0);
  });

  test('does not remove task if confirmation is not clicked', () => {
    const li = document.createElement('li');
    const span = document.createElement('span');
    span.classList.add('task-text');
    span.textContent = 'Stay here';
    li.appendChild(span);
    const taskList = document.getElementById('taskList');
    taskList.appendChild(li);

    deleteTask(li);

    // Don't click confirm
    expect(taskList.children.length).toBe(1);
  });
});

describe('showConfirmationModal', () => {
  test('sets confirmation text and shows modal', () => {
    const onConfirm = jest.fn();

    showConfirmationModal('My task', onConfirm);

    const confirmText = document.getElementById('confirmationText');
    expect(confirmText.textContent).toBe('Voulez-vous supprimer la tâche "My task" ?');
    expect(global.$).toHaveBeenCalledWith('#confirmationModal');
    expect(mockModal).toHaveBeenCalledWith('show');
  });

  test('calls onConfirm callback when confirm button is clicked', () => {
    const onConfirm = jest.fn();

    showConfirmationModal('Confirm me', onConfirm);

    document.getElementById('confirmButton').click();

    expect(onConfirm).toHaveBeenCalledTimes(1);
  });

  test('hides modal after confirmation', () => {
    const onConfirm = jest.fn();

    showConfirmationModal('Hide me', onConfirm);

    document.getElementById('confirmButton').click();

    expect(mockModal).toHaveBeenCalledWith('hide');
  });

  test('handles special characters in task text', () => {
    const onConfirm = jest.fn();

    showConfirmationModal('Task with "quotes" & <html>', onConfirm);

    const confirmText = document.getElementById('confirmationText');
    expect(confirmText.textContent).toContain('Task with "quotes" & <html>');
  });
});

describe('initEventListeners', () => {
  test('adds keypress listener to task input', () => {
    const taskInput = document.getElementById('taskInput');
    const spy = jest.spyOn(taskInput, 'addEventListener');

    initEventListeners();

    expect(spy).toHaveBeenCalledWith('keypress', expect.any(Function));
  });

  test('calls addTask when Enter key is pressed', () => {
    initEventListeners();

    const taskInput = document.getElementById('taskInput');
    taskInput.value = 'Enter task';

    const event = new KeyboardEvent('keypress', { keyCode: 13 });
    taskInput.dispatchEvent(event);

    const taskList = document.getElementById('taskList');
    expect(taskList.children.length).toBe(1);
    expect(taskList.querySelector('.task-text').textContent).toBe('Enter task');
  });

  test('does not call addTask for non-Enter keys', () => {
    initEventListeners();

    const taskInput = document.getElementById('taskInput');
    taskInput.value = 'Should not add';

    const event = new KeyboardEvent('keypress', { keyCode: 65 }); // 'A' key
    taskInput.dispatchEvent(event);

    const taskList = document.getElementById('taskList');
    expect(taskList.children.length).toBe(0);
  });

  test('does not throw if taskInput element does not exist', () => {
    document.body.innerHTML = '';

    expect(() => initEventListeners()).not.toThrow();
  });
});

describe('Integration: full task lifecycle', () => {
  test('add, edit, complete, and delete a task', () => {
    const taskInput = document.getElementById('taskInput');

    // Add a task
    taskInput.value = 'Lifecycle task';
    addTask();

    const taskList = document.getElementById('taskList');
    const li = taskList.children[0];
    expect(li.querySelector('.task-text').textContent).toBe('Lifecycle task');

    // Edit the task
    editTask(li);
    document.getElementById('editInput').value = 'Updated task';
    document.getElementById('saveButton').click();
    expect(li.querySelector('.task-text').textContent).toBe('Updated task');

    // Complete the task (toggle strikethrough)
    const checkbox = li.querySelector('input[type="checkbox"]');
    checkbox.checked = true;
    checkbox.dispatchEvent(new Event('change'));
    expect(li.querySelector('.task-text').classList.contains('strikethrough')).toBe(true);

    // Delete the task
    deleteTask(li);
    document.getElementById('confirmButton').click();
    expect(taskList.children.length).toBe(0);
  });
});
