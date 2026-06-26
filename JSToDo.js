// Helper: safely get a DOM element by ID, logging an error if not found
function getRequiredElement(id) {
    var el = document.getElementById(id);
    if (!el) {
        console.error('Required element not found: #' + id);
    }
    return el;
}

// Helper: safely show a Bootstrap modal, with dependency check
function showModal(selector) {
    if (typeof $ === 'undefined' || typeof $.fn.modal === 'undefined') {
        console.error('jQuery or Bootstrap modal plugin is not loaded — cannot show modal: ' + selector);
        return;
    }
    $(selector).modal('show');
}

// Helper: safely hide a Bootstrap modal, with dependency check
function hideModal(selector) {
    if (typeof $ === 'undefined' || typeof $.fn.modal === 'undefined') {
        console.error('jQuery or Bootstrap modal plugin is not loaded — cannot hide modal: ' + selector);
        return;
    }
    $(selector).modal('hide');
}

// Function to add a new task to the task list
function addTask() {
    var taskInput = getRequiredElement("taskInput");
    if (!taskInput) { return; }

    var taskText = taskInput.value.trim();

    if (taskText === "") {
        alert("Please enter a task!");
        return;
    }

    var taskList = getRequiredElement("taskList");
    if (!taskList) { return; }

    var li = document.createElement("li");
    li.className = 'list-group-item d-flex align-items-center';

    var checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.className = 'mr-2';
    checkbox.addEventListener('change', function () {
        toggleStrikethrough(checkbox, span);
    });
    li.appendChild(checkbox);

    var span = document.createElement("span");
    span.textContent = taskText;
    span.classList.add('task-text');
    li.appendChild(span);

    var editButton = document.createElement("button");
    editButton.innerHTML = "Edit";
    editButton.classList.add('btn', 'btn-sm', 'btn-warning', 'ml-auto', 'mr-2');
    editButton.addEventListener('click', function () {
        editTask(li);
    });
    li.appendChild(editButton);

    var deleteButton = document.createElement("button");
    deleteButton.innerHTML = "Delete";
    deleteButton.classList.add('btn', 'btn-sm', 'btn-danger');
    deleteButton.addEventListener('click', function () {
        deleteTask(li);
    });
    li.appendChild(deleteButton);

    taskList.appendChild(li);
    taskInput.value = "";
}

// Function to toggle strikethrough style based on checkbox state
function toggleStrikethrough(checkbox, taskTextElement) {
    if (!checkbox || !taskTextElement) {
        console.error('toggleStrikethrough called with missing arguments');
        return;
    }
    if (checkbox.checked) {
        taskTextElement.classList.add('strikethrough');
    } else {
        taskTextElement.classList.remove('strikethrough');
    }
}

// Active save-button handler reference, used to prevent listener leaks
var _currentSaveHandler = null;

// Function to edit a task
function editTask(taskItem) {
    if (!taskItem) {
        console.error('editTask called without a task item');
        return;
    }

    var taskSpan = taskItem.querySelector(".task-text");
    if (!taskSpan) {
        console.error('editTask: task item is missing a .task-text element');
        return;
    }

    var taskText = taskSpan.textContent.trim();
    var editText = getRequiredElement('editText');
    var editInput = getRequiredElement('editInput');
    var saveButton = getRequiredElement('saveButton');
    if (!editText || !editInput || !saveButton) { return; }

    editText.textContent = 'Edit task: "' + taskText + '"';
    editInput.value = taskText;

    // Remove previous handler to avoid leaking listeners across edits
    if (_currentSaveHandler) {
        saveButton.removeEventListener('click', _currentSaveHandler);
    }

    _currentSaveHandler = function () {
        var newText = editInput.value.trim();
        if (newText !== "") {
            taskSpan.textContent = newText;
            hideModal('#editModal');
        } else {
            alert("Please enter a task!");
        }
    };

    saveButton.addEventListener('click', _currentSaveHandler);

    showModal('#editModal');
}

// Function to delete a task
function deleteTask(taskItem) {
    if (!taskItem) {
        console.error('deleteTask called without a task item');
        return;
    }

    var taskSpan = taskItem.querySelector(".task-text");
    if (!taskSpan) {
        console.error('deleteTask: task item is missing a .task-text element');
        return;
    }

    var taskText = taskSpan.textContent.trim();
    showConfirmationModal(taskText, function () {
        taskItem.remove();
    });
}

// Active confirm-button handler reference, used to prevent listener leaks
var _currentConfirmHandler = null;

// Function to show a confirmation modal
function showConfirmationModal(taskText, onConfirm) {
    if (typeof onConfirm !== 'function') {
        console.error('showConfirmationModal: onConfirm callback is not a function');
        return;
    }

    var confirmationText = getRequiredElement('confirmationText');
    var confirmButton = getRequiredElement('confirmButton');
    if (!confirmationText || !confirmButton) { return; }

    confirmationText.textContent = 'Voulez-vous supprimer la tâche "' + taskText + '" ?';

    // Remove previous handler to avoid leaking listeners
    if (_currentConfirmHandler) {
        confirmButton.removeEventListener('click', _currentConfirmHandler);
    }

    _currentConfirmHandler = function () {
        onConfirm();
        hideModal('#confirmationModal');
    };

    confirmButton.addEventListener('click', _currentConfirmHandler);

    showModal('#confirmationModal');
}

// Defer event listener setup until the DOM is fully loaded
document.addEventListener('DOMContentLoaded', function () {
    var taskInput = getRequiredElement("taskInput");
    if (!taskInput) { return; }

    taskInput.addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            addTask();
        }
    });
});
