/**
 * 修改权限
 * */
const gradeButtons = document.querySelectorAll('.update-grade');

gradeButtons.forEach(button => {
    button.addEventListener('click', function() {

        // 获取当前人的uid
        const uid = this.getAttribute('data-uid');
        const userElement = this.closest('.a-userInfo');

        // 在当前书籍容器中找到数量输入框 (input)
        const grade = userElement.querySelector('.grade-select').value;

        const formData = new FormData();
        formData.append("uid", uid);
        formData.append("grade", grade);


        // 发送 POST 请求
        fetch('/data/user-grade-change', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-users';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});


/**
 * 删除用户
 * */
const deleteButtons = document.querySelectorAll('.delete-user');

deleteButtons.forEach(button => {
    button.addEventListener('click', function() {
        // 获取当前人的uid
        const uid = this.getAttribute('data-uid');

        const formData = new FormData();
        formData.append("uid", uid);

        // 发送 POST 请求
        fetch('/data/user-delete', {
            method: 'POST',
            body: formData,
            headers: {
                'Accept': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => {
                alert(data.message);  // 这里的 data.message 是从服务器返回的提示信息
                window.location.href = '/page/manage-users';
            })
            .catch(error => {
                console.log("error:"+error);
            });
    });
});

/**
 * 删除用户
 * */
const profileButtons = document.querySelectorAll('.update-profile');

profileButtons.forEach(button => {
    button.addEventListener('click', function() {
        // 获取当前人的uid
        const uid = this.getAttribute('data-uid');


        fetch(`/page/user-profile?uid=${uid}`, {
            method: 'GET'
        })
            .then(response => {
                window.location.href = response.url;  // 使用 response.url 获取重定向目标
            })
            .catch(error => {
                console.error('提交失败:', error);
                alert('提交失败，请重试');
            });
    });
});