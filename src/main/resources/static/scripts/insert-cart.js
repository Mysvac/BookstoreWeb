
// 获取所有的“加入购物车”按钮
const buttons = document.querySelectorAll('.insert-to-cart');

buttons.forEach(button => {
    button.addEventListener('click', function() {
        // 获取当前按钮的 bookid
        const bookid = this.getAttribute('data-bookid');
        const formData = new FormData();
        formData.append("bookid", bookid);

        // 发送 POST 请求
        fetch('/data/insert-cart', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});
