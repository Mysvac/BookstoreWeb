/**
 * 中止订单
 * */
const gradeButtons = document.querySelectorAll('.update-grade');

gradeButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const billid = this.getAttribute('data-billid');
        const userElement = this.closest('.a-userInfo');

        // 在当前书籍容器中找到数量输入框 (input)
        const price = userElement.querySelector('.grade').value;

        const formData = new FormData();
        formData.append("billid", billid);

        // 发送 POST 请求
        fetch('/data/bill-suspend', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-orders';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});