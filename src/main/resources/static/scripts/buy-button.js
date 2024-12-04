/**
 * 买书的按钮
 * */
const buyButtons = document.querySelector('.buy');


buyButtons.addEventListener('click', function() {

    // 获取当前书籍的 bookid
    const bookid = this.getAttribute('data-bookid');

    const formData = new FormData();
    formData.append("bookid", bookid);
    formData.append("amount", "1");

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
        })
        .catch(error => {
            console.log("error:"+error);
        });

});