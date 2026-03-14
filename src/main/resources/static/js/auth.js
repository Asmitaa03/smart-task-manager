const apiBase = "";

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };
  if (token) headers["Authorization"] = "Bearer " + token;
  return headers;
}

document.addEventListener("DOMContentLoaded", () => {
  const registerForm = document.getElementById("registerForm");
  const loginForm = document.getElementById("loginForm");

  if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const body = {
        name: document.getElementById("name").value.trim(),
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value,
        role: document.getElementById("role").value || "USER"
      };

      const res = await fetch(apiBase + "/api/users/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      const msg = document.getElementById("registerMsg");
      if (res.ok) {
        msg.style.color = "green";
        msg.textContent = "Registered successfully!";
        setTimeout(() => (location.href = "/"), 1500);
      } else {
        msg.style.color = "crimson";
        const data = await res.json().catch(() => ({}));
        if (data.errors && typeof data.errors === "object") {
          const firstError = Object.values(data.errors)[0];
          msg.textContent = firstError || data.message || "Registration failed!";
        } else {
          msg.textContent = data.message || "Registration failed!";
        }
      }
    });
  }

  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      const body = {
        email: document.getElementById("email").value.trim(),
        password: document.getElementById("password").value
      };

      const res = await fetch(apiBase + "/api/users/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
      });

      const msg = document.getElementById("loginMsg");
      if (res.ok) {
        const data = await res.json();
        localStorage.setItem("token", data.token);
        localStorage.setItem("currentUser", JSON.stringify(data.user));
        location.href = "/dashboard.html";
      } else {
        msg.style.color = "crimson";
        const err = await res.json().catch(() => ({}));
        msg.textContent = err.message || "Invalid email/password!";
      }
    });
  }
});
