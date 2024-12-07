
function encrypt(str, key) {
    let encrypted = '';
    for (let i = 0; i < str.length; i++) {
        encrypted += String.fromCharCode(str.charCodeAt(i) ^ key); // XOR 运算
    }
    return encrypted;
}

document.getElementById("submit").addEventListener("click", (event)=>{
    event.preventDefault(); // 防止表单刷新页面
    let uid = document.getElementById('uid').value;
    let password = document.getElementById('password').value;

    const formData = new FormData();
    uid = encrypt(uid,10086);
    password = encrypt(password,10086);

    formData.append("uid", uid);
    formData.append("password", password);

    fetch('/user/sign-in', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            window.location.href = response.url;  // 使用 response.url 获取重定向目标
        })
        .catch(error => {
            console.error('提交失败:', error);
            alert('提交失败，请重试');
        });
});