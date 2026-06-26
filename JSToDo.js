// --- Shared Utilities ---

// Creates a DOM element with optional properties, CSS classes, text, and an event listener.
function createElement(tag, options) {
    var el = document.createElement(tag);
    if (options.type) el.type = options.type;
    if (options.className) el.className = options.className;
    if (options.classes) el.classList.add.apply(el.classList, options.classes);
    if (options.text) el.textContent = options.text;
    if (options.html) el.innerHTML = options.html;
    if (options.event) el.addEventListener(options.event.type, options.event.handler);
    return el;
}

// Creates a styled button with a click handler.
function createButton(text, classes, onClick) {
    return createElement("button", {
        html: text,
        classes: classes,
        event: { type: 'click', handler: onClick }
    });
}

// Extracts the trimmed task text from a list item element.
function getTaskText(taskItem) {
    return taskItem.querySelector(".task-text").textContent.trim();
}

// Validates that text is non-empty; shows an alert and returns false if empty.
function validateTaskText(text) {
    if (text === "") {
        alert("Please enter a task!");
        return false;
    }
    return true;
}

// Shows a Bootstrap modal by its ID.
function showModal(id) {
    $('#' + id).modal('show');
}

// Hides a Bootstrap modal by its ID.
function hideModal(id) {
    $('#' + id).modal('hide');
}

// --- Application Logic ---

// Adds a new task to the task list.
function addTask() {
    var taskInput = document.getElementById("taskInput");
    var taskText = taskInput.value.trim();

    if (!validateTaskText(taskText)) return;

    var taskList = document.getElementById("taskList");
    var li = createElement("li", { className: 'list-group-item d-flex align-items-center' });

    var checkbox = createElement("input", {
        type: "checkbox",
        className: 'mr-2'
    });

    var span = createElement("span", {
        text: taskText,
        classes: ['task-text']
    });

    checkbox.addEventListener('change', function () {
        toggleStrikethrough(checkbox, span);
    });

    var editButton = createButton("Edit",
        ['btn', 'btn-sm', 'btn-warning', 'ml-auto', 'mr-2'],
        function () { editTask(li); }
    );

    var deleteButton = createButton("Delete",
        ['btn', 'btn-sm', 'btn-danger'],
        function () { deleteTask(li); }
    );

    li.appendChild(checkbox);
    li.appendChild(span);
    li.appendChild(editButton);
    li.appendChild(deleteButton);

    taskList.appendChild(li);
    taskInput.value = "";
}

// Toggles strikethrough style based on checkbox state.
function toggleStrikethrough(checkbox, taskTextElement) {
    if (checkbox.checked) {
        taskTextElement.classList.add('strikethrough');
    } else {
        taskTextElement.classList.remove('strikethrough');
    }
}

// Edits an existing task via modal dialog.
function editTask(taskItem) {
    var taskText = getTaskText(taskItem);
    var editInput = document.getElementById('editInput');

    document.getElementById('editText').textContent = 'Edit task: "' + taskText + '"';
    editInput.value = taskText;

    showModal('editModal');

    document.getElementById('saveButton').onclick = function () {
        var newText = editInput.value.trim();
        if (!validateTaskText(newText)) return;
        taskItem.querySelector(".task-text").textContent = newText;
        hideModal('editModal');
    };
}

// Deletes a task after user confirmation.
function deleteTask(taskItem) {
    var taskText = getTaskText(taskItem);
    showConfirmationModal(taskText, function () {
        taskItem.remove();
    });
}

// Shows a confirmation modal and executes a callback on confirm.
function showConfirmationModal(taskText, onConfirm) {
    document.getElementById('confirmationText').textContent =
        'Voulez-vous supprimer la t\u00e2che "' + taskText + '" ?';

    showModal('confirmationModal');

    document.getElementById('confirmButton').onclick = function () {
        onConfirm();
        hideModal('confirmationModal');
    };
}

// Adds task on Enter key press.
var taskInput = document.getElementById("taskInput");
taskInput.addEventListener("keypress", function (event) {
    if (event.keyCode === 13) {
        addTask();
    }
});
