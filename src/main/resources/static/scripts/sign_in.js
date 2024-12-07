
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

    uid = encrypt(uid,10086);
    password = encrypt(password,10086);

    // 创建一个虚拟的表单
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '/user/sign-in';  // 设置 action 为目标 URL

    // 创建 uid 和 password 的隐藏输入字段
    const inputUid = document.createElement('input');
    inputUid.type = 'hidden';
    inputUid.name = 'uid';
    inputUid.value = uid;

    const inputPassword = document.createElement('input');
    inputPassword.type = 'hidden';
    inputPassword.name = 'password';
    inputPassword.value = password;

    // 将输入字段添加到表单中
    form.appendChild(inputUid);
    form.appendChild(inputPassword);

    // 将表单添加到页面（表单必须先添加到 DOM 中）
    document.body.appendChild(form);

    // 提交表单
    form.submit();
});