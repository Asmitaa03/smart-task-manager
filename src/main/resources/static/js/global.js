document.addEventListener("DOMContentLoaded", () => {
    const file = window.location.pathname.split("/").pop();

    if (file.includes("index") || file === "") {
        document.body.dataset.page = "login";
    }
    else if (file.includes("register")) {
        document.body.dataset.page = "register";
    }
    else if (file.includes("dashboard")) {
        document.body.dataset.page = "dashboard";
    }
});
