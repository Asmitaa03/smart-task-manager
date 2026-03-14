const apiBase = "";

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = "Bearer " + token;
  return headers;
}

document.addEventListener("DOMContentLoaded", () => {
  const currentUser = JSON.parse(localStorage.getItem("currentUser") || "null");
  const token = localStorage.getItem("token");

  if (!currentUser || !token) {
    location.href = "/";
    return;
  }

  document.getElementById("userInfo").textContent =
    currentUser.name + " (" + currentUser.role + ")";

  document.getElementById("logoutBtn").onclick = () => {
    localStorage.removeItem("currentUser");
    localStorage.removeItem("token");
    location.href = "/";
  };

  const taskForm = document.getElementById("taskForm");
  const taskList = document.getElementById("taskList");
  const isAdmin = currentUser.role === "ADMIN";

  if (isAdmin && taskForm) {
    taskForm.style.display = "none";
    const taskMsg = document.getElementById("taskMsg");
    if (taskMsg) {
      taskMsg.style.color = "#333";
      taskMsg.textContent = "You are viewing all user tasks (ADMIN).";
    }
  }

  function renderTasks(tasks) {
    taskList.innerHTML = "";
    if (!tasks || tasks.length === 0) {
      taskList.innerHTML = "<li>No tasks found</li>";
      return;
    }
    for (const t of tasks) {
      const ownerName = t.user ? (t.user.name || t.user.email || "User") : (t.userId || "Unknown");
      const li = document.createElement("li");
      li.className = "task-item";
      li.innerHTML = `
        <div>
          <strong>${escapeHtml(t.title)}</strong>
          <div class="meta">${escapeHtml(t.deadline || "")} • ${escapeHtml(t.status || "")}</div>
          <div class="meta">Owner: ${escapeHtml(ownerName)}</div>
        </div>
        <div>
          ${!isAdmin ? `<span class="link" data-id="${t.id}" data-action="edit">Edit</span> | <span class="link" data-id="${t.id}" data-action="delete">Delete</span>` : `<span class="link" data-id="${t.id}" data-action="deleteAdmin">Delete</span>`}
        </div>
      `;
      taskList.appendChild(li);
    }
  }

  async function loadTasks() {
    taskList.innerHTML = "<li>Loading...</li>";
    const url = isAdmin ? apiBase + "/api/tasks/admin/all" : apiBase + "/api/tasks/me";
    const res = await fetch(url, { headers: getAuthHeaders() });
    if (res.status === 401) {
      localStorage.removeItem("currentUser");
      localStorage.removeItem("token");
      location.href = "/";
      return;
    }
    if (!res.ok) {
      const err = await res.json().catch(() => ({ message: "Failed to fetch" }));
      taskList.innerHTML = `<li class="msg">${escapeHtml(err.message || "Failed to fetch tasks")}</li>`;
      return;
    }
    const tasks = await res.json();
    renderTasks(tasks);
  }

  if (taskForm && !isAdmin) {
    taskForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const body = {
        title: document.getElementById("title").value.trim(),
        description: document.getElementById("description").value.trim(),
        deadline: document.getElementById("deadline").value,
        status: document.getElementById("status").value || "PENDING"
      };
      const res = await fetch(apiBase + "/api/tasks", {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(body)
      });
      const taskMsg = document.getElementById("taskMsg");
      if (res.status === 401) {
        localStorage.removeItem("currentUser");
        localStorage.removeItem("token");
        location.href = "/";
        return;
      }
      if (res.ok) {
        if (taskMsg) { taskMsg.style.color = "green"; taskMsg.textContent = "Task created"; }
        taskForm.reset();
        loadTasks();
      } else {
        const err = await res.json().catch(() => ({ message: "Error creating task" }));
        if (taskMsg) {
          taskMsg.style.color = "crimson";
          taskMsg.textContent = err.message || "Failed to create task";
        }
      }
    });
  }

  if (taskList) {
    taskList.addEventListener("click", async (ev) => {
      const action = ev.target.dataset.action;
      const id = ev.target.dataset.id;
      if (!action) return;

      if (action === "delete" || action === "deleteAdmin") {
        if (!confirm("Delete task?")) return;
        const res = await fetch(apiBase + "/api/tasks/" + id, {
          method: "DELETE",
          headers: getAuthHeaders()
        });
        if (res.status === 401) {
          localStorage.removeItem("currentUser");
          localStorage.removeItem("token");
          location.href = "/";
          return;
        }
        if (res.status === 204) loadTasks();
        else {
          const err = await res.json().catch(() => ({ message: "Delete failed" }));
          alert(err.message || "Delete failed");
        }
      }

      if (action === "edit") {
        alert("Edit in UI not implemented yet. Use API or add an edit form.");
      }
    });
  }

  loadTasks();
});

function escapeHtml(s) {
  return String(s || "").replace(/[&<>"']/g, c => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}
