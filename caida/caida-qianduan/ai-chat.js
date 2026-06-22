(function() {
    const API = '/api/ai/chat';

    // 浮窗HTML
    const html = `
    <div id="aiChatWidget" style="position:fixed;bottom:80px;right:12px;z-index:9998;">
        <!-- 聊天面板 -->
        <div id="aiChatPanel" style="display:none;width:calc(100vw - 24px);max-width:360px;height:min(480px,65vh);background:#fff;border-radius:18px;box-shadow:0 12px 40px rgba(0,0,0,0.18);flex-direction:column;overflow:hidden;margin-bottom:12px;">
            <!-- 头部 -->
            <div style="background:linear-gradient(135deg,var(--main-red,#b71c1c),var(--light-red,#d84343));color:#fff;padding:12px 16px;display:flex;justify-content:space-between;align-items:center;">
                <div style="display:flex;align-items:center;gap:8px;">
                    <img src="images/sasuke_avatar.png" style="width:32px;height:32px;border-radius:50%;object-fit:cover;border:2px solid rgba(255,255,255,0.3);">
                    <div>
                        <div style="font-weight:600;font-size:14px;">宇智波佐助 · 客服</div>
                        <div style="font-size:10px;opacity:0.8;">写轮眼洞察你的需求</div>
                    </div>
                </div>
                <button id="aiChatClose" style="background:none;border:none;color:#fff;font-size:22px;cursor:pointer;padding:0;line-height:1;">×</button>
            </div>
            <!-- 快捷问题 -->
            <div id="aiQuickQs" style="display:flex;gap:6px;padding:8px 12px;overflow-x:auto;border-bottom:1px solid #f0f0f0;flex-shrink:0;flex-wrap:nowrap;">
                <button class="ai-quick-q" data-q="有什么手机推荐？">📱 手机推荐</button>
                <button class="ai-quick-q" data-q="热门商品有哪些？">🔥 热门商品</button>
                <button class="ai-quick-q" data-q="怎么下单购买？">🛒 如何购买</button>
                <button class="ai-quick-q" data-q="有优惠活动吗？">🎁 优惠活动</button>
            </div>
            <!-- 消息区 -->
            <div id="aiMessages" style="flex:1;overflow-y:auto;padding:12px;display:flex;flex-direction:column;gap:10px;background:#fafbfc;">
                <div style="display:flex;gap:6px;">
                    <img src="images/sasuke_avatar.png" style="width:28px;height:28px;border-radius:50%;object-fit:cover;flex-shrink:0;">
                    <div style="background:#fff;padding:8px 12px;border-radius:12px;font-size:13px;line-height:1.5;color:#333;box-shadow:0 1px 3px rgba(0,0,0,0.04);max-width:85%;">
                        哼，我是宇智波佐助。有什么需要就快说 😤<br>财大优选的东西还不错...
                    </div>
                </div>
            </div>
            <!-- 输入区 -->
            <div style="padding:10px 12px;border-top:1px solid #eee;display:flex;gap:8px;background:#fff;">
                <input id="aiInput" type="text" placeholder="输入你的问题..." style="flex:1;border:2px solid #e8e8e8;border-radius:22px;padding:8px 14px;font-size:14px;outline:none;" onkeypress="if(event.key==='Enter')document.getElementById('aiSendBtn').click()">
                <button id="aiSendBtn" style="width:38px;height:38px;background:linear-gradient(135deg,var(--main-red,#b71c1c),var(--light-red,#d84343));color:#fff;border:none;border-radius:50%;font-size:16px;cursor:pointer;flex-shrink:0;">➤</button>
            </div>
        </div>
        <!-- 浮动按钮 -->
        <button id="aiChatBtn" style="width:52px;height:52px;border:none;border-radius:50%;cursor:pointer;box-shadow:0 8px 24px rgba(183,28,28,0.35);transition:all 0.3s;display:flex;align-items:center;justify-content:center;float:right;padding:0;overflow:hidden;background:#fff;" title="智能客服"><img src="images/sasuke_avatar.png" style="width:52px;height:52px;object-fit:cover;"></button>
    </div>`;

    document.body.insertAdjacentHTML('beforeend', html);

    const panel = document.getElementById('aiChatPanel');
    const btn = document.getElementById('aiChatBtn');
    const close = document.getElementById('aiChatClose');
    const sendBtn = document.getElementById('aiSendBtn');
    const input = document.getElementById('aiInput');
    const messages = document.getElementById('aiMessages');
    const quickQs = document.getElementById('aiQuickQs');

    let isOpen = false;

    // 给按钮加文字层
    const btnText = document.createElement('span');
    btnText.style.cssText = 'display:none;font-size:24px;color:#b71c1c;font-weight:bold;position:absolute;';
    btnText.textContent = '✕';
    btn.style.position = 'relative';
    btn.appendChild(btnText);

    btn.addEventListener('click', () => {
        isOpen = !isOpen;
        panel.style.display = isOpen ? 'flex' : 'none';
        btn.querySelector('img').style.display = isOpen ? 'none' : 'block';
        btnText.style.display = isOpen ? 'block' : 'none';
        if (isOpen) input.focus();
    });

    close.addEventListener('click', () => {
        isOpen = false;
        panel.style.display = 'none';
        btn.querySelector('img').style.display = 'block';
        btnText.style.display = 'none';
    });

    // 快捷问题
    quickQs.querySelectorAll('.ai-quick-q').forEach(qbtn => {
        qbtn.addEventListener('click', function() {
            const q = this.dataset.q;
            input.value = q;
            sendMessage();
        });
    });

    // 发送消息
    sendBtn.addEventListener('click', sendMessage);

    async function sendMessage() {
        const text = input.value.trim();
        if (!text) return;

        // 显示用户消息
        addMessage('user', text);
        input.value = '';
        quickQs.style.display = 'none';

        // 显示加载
        const loadingId = addMessage('ai', '<span class="ai-loading">思考中<span>.</span><span>.</span><span>.</span></span>');

        try {
            const res = await fetch(API, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ message: text })
            });
            const data = await res.json();

            // 移除加载
            const loadingEl = document.getElementById(loadingId);
            if (loadingEl) loadingEl.remove();

            if (data.code === 200) {
                addMessage('ai', data.data);
            } else {
                addMessage('ai', '抱歉，我暂时无法回答，请稍后再试 😅');
            }
        } catch (e) {
            const loadingEl = document.getElementById(loadingId);
            if (loadingEl) loadingEl.remove();
            addMessage('ai', '网络连接失败，请检查网络后重试 📡');
        }
    }

    function addMessage(role, content) {
        const id = 'msg-' + Date.now();
        const isAi = role === 'ai';
        const div = document.createElement('div');
        div.id = id;
        div.style.cssText = 'display:flex;gap:8px;' + (isAi ? '' : 'flex-direction:row-reverse;');
        div.innerHTML = isAi
            ? `<img src="images/sasuke_avatar.png" style="width:26px;height:26px;border-radius:50%;object-fit:cover;flex-shrink:0;">
               <div style="background:#fff;padding:8px 12px;border-radius:12px;font-size:13px;line-height:1.5;color:#333;box-shadow:0 1px 3px rgba(0,0,0,0.04);max-width:80%;">${content}</div>`
            : `<div style="background:linear-gradient(135deg,var(--main-red,#b71c1c),var(--light-red,#d84343));color:#fff;padding:8px 12px;border-radius:12px;font-size:13px;line-height:1.5;max-width:80%;">${content}</div>`;
        messages.appendChild(div);
        messages.scrollTop = messages.scrollHeight;
        return id;
    }

    // 加载动画样式
    const style = document.createElement('style');
    style.textContent = `
        .ai-loading span { animation: aiDot 1.4s infinite; }
        .ai-loading span:nth-child(2) { animation-delay: 0.2s; }
        .ai-loading span:nth-child(3) { animation-delay: 0.4s; }
        @keyframes aiDot { 0%,80%,100% { opacity:0; } 40% { opacity:1; } }
        .ai-quick-q { padding:6px 12px;background:#fff;border:1px solid #e0e0e0;border-radius:16px;font-size:12px;cursor:pointer;white-space:nowrap;transition:all 0.2s;color:#555; }
        .ai-quick-q:hover { border-color:var(--main-red,#b71c1c);color:var(--main-red,#b71c1c);background:#fff5f5; }
    `;
    document.head.appendChild(style);

    // 退出登录时清除聊天记录
    function resetChat() {
        messages.innerHTML = `<div style="display:flex;gap:6px;">
            <img src="images/sasuke_avatar.png" style="width:26px;height:26px;border-radius:50%;object-fit:cover;flex-shrink:0;">
            <div style="background:#fff;padding:8px 12px;border-radius:12px;font-size:13px;line-height:1.5;color:#333;box-shadow:0 1px 3px rgba(0,0,0,0.04);max-width:80%;">
                哼，我是宇智波佐助。有什么需要就快说 😤<br>财大优选的东西还不错...
            </div>
        </div>`;
        quickQs.style.display = 'flex';
    }

    // 监听 localStorage 变化（检测退出登录）
    let lastUserInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo');
    setInterval(() => {
        const currentUser = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo');
        if (lastUserInfo && !currentUser) {
            resetChat();
            if (isOpen) { close.click(); }
        }
        lastUserInfo = currentUser;
    }, 1000);
})();
