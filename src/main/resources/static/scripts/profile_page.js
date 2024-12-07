function toggleForm() {
    var formSection = document.getElementById("form-section");
    if (formSection.style.display === "none" || formSection.style.display === "") {
        formSection.style.display = "block"; // 显示表单
    } else {
        formSection.style.display = "none"; // 隐藏表单
    }
}

document.getElementById("change-info-button").addEventListener('click',function () {
    toggleForm()
});

