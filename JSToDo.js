// Function to add a new task to the task list
function addTask() {
    var taskInput = document.getElementById("taskInput"); // Get the task input element
    var taskText = taskInput.value.trim(); // Get the trimmed task text

    if (taskText !== "") { // Check if the task text is not empty
        var taskList = document.getElementById("taskList"); // Get the task list element
        var li = document.createElement("li"); // Create a new list item
        li.className = 'list-group-item d-flex align-items-center'; // Set the list item classes

        var checkbox = document.createElement("input"); // Create a new checkbox input
        checkbox.type = "checkbox"; // Set the input type to checkbox
        checkbox.className = 'mr-2'; // Set the checkbox class
        checkbox.addEventListener('change', function () {
            toggleStrikethrough(checkbox, span); // Add change event listener to toggle strikethrough
        });
        li.appendChild(checkbox); // Append the checkbox to the list item

        var span = document.createElement("span"); // Create a new span element
        span.textContent = taskText; // Set the span text to the task text
        span.classList.add('task-text'); // Add the task-text class to the span
        li.appendChild(span); // Append the span to the list item

        var editButton = document.createElement("button"); // Create a new edit button
        editButton.innerHTML = "Edit"; // Set the button text
        editButton.classList.add('btn', 'btn-sm', 'btn-warning', 'ml-auto', 'mr-2'); // Add button classes
        editButton.addEventListener('click', function () {
            editTask(li); // Add click event listener to edit task
        });
        li.appendChild(editButton); // Append the edit button to the list item

        var deleteButton = document.createElement("button"); // Create a new delete button
        deleteButton.innerHTML = "Delete"; // Set the button text
        deleteButton.classList.add('btn', 'btn-sm', 'btn-danger'); // Add button classes
        deleteButton.addEventListener('click', function () {
            deleteTask(li); // Add click event listener to delete task
        });
        li.appendChild(deleteButton); // Append the delete button to the list item

        taskList.appendChild(li); // Append the list item to the task list
        taskInput.value = ""; // Clear the task input
    } else {
        alert("Please enter a task!"); // Show an alert if the task text is empty
    }
}

// Function to toggle strikethrough style based on checkbox state
function toggleStrikethrough(checkbox, taskTextElement) {
    if (checkbox.checked) {
        taskTextElement.classList.add('strikethrough'); // Add strikethrough class if checkbox is checked
    } else {
        taskTextElement.classList.remove('strikethrough'); // Remove strikethrough class if checkbox is unchecked
    }
}

// Function to edit a task
function editTask(taskItem) {
    var taskText = taskItem.querySelector(".task-text").textContent.trim(); // Get the task text
    var editText = document.getElementById('editText'); // Get the edit text element
    var editInput = document.getElementById('editInput'); // Get the edit input element
    editText.textContent = `Edit task: "${taskText}"`; // Set the edit text content
    editInput.value = taskText; // Set the edit input value

    $('#editModal').modal('show'); // Show the edit modal

    document.getElementById('saveButton').onclick = function () { // Add click event to save button
        var newText = editInput.value.trim(); // Get the trimmed new text
        if (newText !== "") { // Check if new text is not empty
            taskItem.querySelector(".task-text").textContent = newText; // Update task text in UI
            $('#editModal').modal('hide'); // Hide the edit modal
        } else {
            alert("Please enter a task!"); // Show an alert if new text is empty
        }
    };
}

// Function to delete a task
function deleteTask(taskItem) {
    var taskText = taskItem.querySelector(".task-text").textContent.trim(); // Get the task text
    showConfirmationModal(taskText, function () { // Show confirmation modal for task deletion
        taskItem.remove(); // Remove task from UI
    });
}

// Function to show a confirmation modal
function showConfirmationModal(taskText, onConfirm) {
    const confirmationText = document.getElementById('confirmationText'); // Get the confirmation text element
    confirmationText.textContent = `Voulez-vous supprimer la tâche "${taskText}" ?`; // Set confirmation text
    $('#confirmationModal').modal('show'); // Show confirmation modal

    document.getElementById('confirmButton').onclick = function () { // Add click event to confirm button
        onConfirm(); // Call onConfirm callback
        $('#confirmationModal').modal('hide'); // Hide confirmation modal
    };
}

// Event listener for keypress on task input (to add task on Enter key press)
var taskInput = document.getElementById("taskInput"); // Get the task input element
taskInput.addEventListener("keypress", function (event) { // Add keypress event listener
    if (event.keyCode === 13) { // Check if Enter key is pressed
        addTask(); // Call addTask function
    }
});
