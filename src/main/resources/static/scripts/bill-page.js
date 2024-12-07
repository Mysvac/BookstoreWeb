/**
 * 中止订单
 * */
const suspendButtons = document.querySelectorAll('.suspend-btn');

suspendButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const billid = this.getAttribute('data-billid');

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
                window.location.href = '/page/bill';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});

/**
 * 中止订单
 * */
const finishButtons = document.querySelectorAll('.finish-btn');

finishButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前书籍的 bookid
        const billid = this.getAttribute('data-billid');

        const formData = new FormData();
        formData.append("billid", billid);

        // 发送 POST 请求
        fetch('/data/bill-finish', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/bill';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});
