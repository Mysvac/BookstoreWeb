
/**
 * 更新数据的按钮
 * */
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
            .then(response => {
                if(response.ok){
                    window.location.href = '/page/cart';
                }else {
                    console.log("请求失败");
                }
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 删除数据的按钮
 * */
const deleteButtons = document.querySelectorAll('.delete');

deleteButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const bookid = this.getAttribute('data-bookid');

        const formData = new FormData();
        formData.append("bookid", bookid);
        formData.append("amount", "0");

        // 发送 POST 请求
        fetch('/data/cart-amount', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if(response.ok){
                    window.location.href = '/page/cart';
                }else {
                    console.log("请求失败");
                }
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 买书*/
const buyButtons = document.querySelectorAll('.buy');
// 为每个按钮添加点击事件
buyButtons.forEach(button => {
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
        fetch('/data/buy-book', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/cart';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});