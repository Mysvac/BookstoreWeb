/**
 * 修改库存和价格
 * */
const updateButtons = document.querySelectorAll('.update-book');

updateButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const bookid = this.getAttribute('data-bookid');

        const bookElement = this.closest('.a-book');

        // 在当前书籍容器中找到数量输入框 (input)
        const price = bookElement.querySelector('.price-input').value;
        const stock = bookElement.querySelector('.stock-input').value;

        const formData = new FormData();
        formData.append("bookid", bookid);
        formData.append("price",price);
        formData.append("stock",stock);

        // 发送 POST 请求
        fetch('/data/book-update', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-books';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});

/**
 * 删除书籍
 * */
const deleteButtons = document.querySelectorAll('.delete-book');

deleteButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const bookid = this.getAttribute('data-bookid');

        const formData = new FormData();
        formData.append("bookid", bookid);

        // 发送 POST 请求
        fetch('/data/book-delete', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-books';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 修改状态
 * */
const ableButtons = document.querySelectorAll('.buy-able');

ableButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const bookid = this.getAttribute('data-bookid');

        const formData = new FormData();
        formData.append("bookid", bookid);

        // 发送 POST 请求
        fetch('/data/book-able', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-books';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});
