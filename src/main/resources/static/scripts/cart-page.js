
const updateButtons = document.querySelectorAll('.update-num');

// 为每个按钮添加点击事件
updateButtons.forEach(button => {
    button.addEventListener('click', function() {
        // 获取当前按钮所在的书籍容器（.book）
        const bookElement = this.closest('.book');

        // 在当前书籍容器中找到数量输入框 (input)
        const quantityInput = bookElement.querySelector('.booknum');

        // 获取输入框中的数量值
        const amount = quantityInput.value;

        // 获取当前书籍的 bookid
        const bookid = this.getAttribute('data-bookid');

        const formData = new FormData();
        formData.append("bookid", bookid);
        formData.append("amount", amount);

        // 发送 POST 请求
        fetch('/data/cart-amount', {
            method: 'POST',
            body: formData
        })
            .then(response => response.json())
            .then(data => {
                console.log("success changed num");
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});