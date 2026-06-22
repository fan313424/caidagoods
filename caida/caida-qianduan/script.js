document.addEventListener("DOMContentLoaded", function () {
    // =========================
    // 登录验证
    // =========================
    // 检查用户是否已登录
    const userInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo');
    const userId = localStorage.getItem('userId') || sessionStorage.getItem('userId');
    
    // 如果未登录，跳转到登录页
    if (!userInfo || !userId) {
        window.location.href = 'login.html';
        return;
    }
    
    // =========================
    // 购物车功能（后端API版本）
    // =========================
    // 使用Nginx代理后，可以使用相对路径避免跨域问题
    // 开发环境: 'http://localhost:8080/api'
    // 生产环境(Nginx): '/api'
    const API_BASE_URL = '/api';
    const CURRENT_USER_ID = parseInt(userId); // 从登录状态获取用户ID
    
    const cartBtn = document.getElementById('cartBtn');
    const cartSidebar = document.getElementById('cartSidebar');
    const cartClose = document.getElementById('cartClose');
    const cartOverlay = document.getElementById('cartOverlay');
    const cartItems = document.getElementById('cartItems');
    const cartCount = document.getElementById('cartCount');
    const totalPrice = document.getElementById('totalPrice');
    const checkoutBtn = document.getElementById('checkoutBtn');

    // 打开购物车
    if (cartBtn) {
        cartBtn.addEventListener('click', function(e) {
            e.preventDefault();
            openCart();
        });
    }

    // 关闭购物车
    if (cartClose) {
        cartClose.addEventListener('click', closeCart);
    }

    if (cartOverlay) {
        cartOverlay.addEventListener('click', closeCart);
    }

    function openCart() {
        cartSidebar.classList.add('active');
        cartOverlay.classList.add('active');
        document.body.style.overflow = 'hidden';
        loadCartFromServer();
    }

    function closeCart() {
        cartSidebar.classList.remove('active');
        cartOverlay.classList.remove('active');
        document.body.style.overflow = '';
    }

    // =========================
    // 动态加载首页商品（价格实时同步数据库）
    // =========================
    async function loadProducts() {
        const grid = document.getElementById('goodsGrid');
        if (!grid) return;

        grid.innerHTML = '<p style="text-align:center;padding:40px;color:#888;">正在加载商品...</p>';

        try {
            const response = await fetch(`${API_BASE_URL}/admin/products`);
            const result = await response.json();
            if (result.code === 200 && result.data && result.data.length > 0) {
                // 只显示上架商品，按最新排序，首页限10件
                const activeProducts = result.data.filter(p => p.status === 1).reverse();
                const displayProducts = activeProducts.slice(0, 10);
                if (displayProducts.length === 0) {
                    grid.innerHTML = '<p style="text-align:center;padding:40px;color:#888;">暂无上架商品</p>';
                    return;
                }
                grid.innerHTML = displayProducts.map(p => `
                    <article class="goods-card" data-id="${p.id}">
                        <img src="${p.imageUrl || 'images/goods01.png'}" alt="${p.name}" onerror="this.src='images/goods01.png'">
                        <h3>${p.name}</h3>
                        <p class="desc">${p.description || ''}</p>
                        <p class="price">￥${(p.price || 0).toFixed(2)}</p>
                        <button class="add-to-cart-btn">加入购物车</button>
                    </article>
                `).join('');

                // 绑定加入购物车事件
                grid.querySelectorAll('.add-to-cart-btn').forEach(button => {
                    button.addEventListener('click', async function() {
                        const card = this.closest('.goods-card');
                        const productId = parseInt(card.dataset.id);
                        try {
                            const response = await fetch(`${API_BASE_URL}/cart/add`, {
                                method: 'POST',
                                headers: { 'Content-Type': 'application/json' },
                                body: JSON.stringify({ userId: CURRENT_USER_ID, productId: productId, quantity: 1 })
                            });
                            const result = await response.json();
                            if (result.code === 200) {
                                showToast('已添加到购物车！');
                                updateCartCountFromServer();
                            } else {
                                showToast(result.message || '添加失败');
                            }
                        } catch (error) {
                            showToast('网络错误，请稍后重试');
                        }
                    });
                });
                return;
            }
        } catch (error) {
            console.error('加载商品列表错误:', error);
        }
        // API 失败兜底：显示加载失败
        grid.innerHTML = '<p style="text-align:center;padding:40px;color:#888;">商品加载失败，请刷新页面重试</p>';
    }

    // 加载首页商品（延迟确保 DOM 完全就绪）
    setTimeout(loadProducts, 100);

    // 从服务器加载购物车
    async function loadCartFromServer() {
        try {
            const response = await fetch(`${API_BASE_URL}/cart?userId=${CURRENT_USER_ID}`);
            const result = await response.json();
            
            if (result.code === 200) {
                renderCart(result.data);
            } else {
                showToast('加载购物车失败');
            }
        } catch (error) {
            console.error('加载购物车错误:', error);
            showToast('网络错误，请稍后重试');
        }
    }

    // 从服务器更新购物车数量
    async function updateCartCountFromServer() {
        try {
            const response = await fetch(`${API_BASE_URL}/cart/count?userId=${CURRENT_USER_ID}`);
            const result = await response.json();
            
            if (result.code === 200 && cartCount) {
                cartCount.textContent = result.data.count;
            }
        } catch (error) {
            console.error('获取购物车数量错误:', error);
        }
    }

    // 从服务器删除商品
    async function removeFromCart(cartId) {
        try {
            const response = await fetch(`${API_BASE_URL}/cart/remove/${cartId}`, {
                method: 'DELETE'
            });
            
            const result = await response.json();
            if (result.code === 200) {
                showToast('商品已移除');
                loadCartFromServer();
                updateCartCountFromServer();
            } else {
                showToast(result.message || '删除失败');
            }
        } catch (error) {
            console.error('删除商品错误:', error);
            showToast('网络错误，请稍后重试');
        }
    }

    // 从服务器更新商品数量
    async function updateQuantity(cartId, change) {
        try {
            // 先获取当前数量
            const cartItemElement = document.querySelector(`.cart-item[data-id="${cartId}"]`);
            const currentQuantity = parseInt(cartItemElement.querySelector('.quantity-display').textContent);
            const newQuantity = currentQuantity + change;
            
            if (newQuantity <= 0) {
                removeFromCart(cartId);
                return;
            }
            
            const response = await fetch(`${API_BASE_URL}/cart/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    cartId: cartId,
                    quantity: newQuantity
                })
            });
            
            const result = await response.json();
            if (result.code === 200) {
                loadCartFromServer();
                updateCartCountFromServer();
            } else {
                showToast(result.message || '更新失败');
            }
        } catch (error) {
            console.error('更新数量错误:', error);
            showToast('网络错误，请稍后重试');
        }
    }

    // 渲染购物车
    function renderCart(cartList) {
        if (!cartItems) return;

        if (!cartList || cartList.length === 0) {
            cartItems.innerHTML = `
                <div class="cart-empty">
                    <div class="cart-empty-icon">🛒</div>
                    <p>购物车是空的</p>
                </div>
            `;
            if (totalPrice) {
                totalPrice.textContent = '￥0.00';
            }
            // 更新购物车数量（侧边栏和顶部导航栏）
            const cartItemCount = document.getElementById('cartItemCount');
            if (cartItemCount) {
                cartItemCount.textContent = '0';
            }
            if (cartCount) {
                cartCount.textContent = '0';
            }
            return;
        }

        cartItems.innerHTML = cartList.map(item => `
            <div class="cart-item" data-id="${item.id}">
                <img src="${item.productImage}" alt="${item.productName}" class="cart-item-image">
                <div class="cart-item-info">
                    <div>
                        <div class="cart-item-name">${item.productName}</div>
                        <div class="cart-item-price">￥${item.productPrice.toFixed(2)}</div>
                    </div>
                    <div class="cart-item-controls">
                        <button class="quantity-btn decrease" data-id="${item.id}">-</button>
                        <span class="quantity-display">${item.quantity}</span>
                        <button class="quantity-btn increase" data-id="${item.id}">+</button>
                        <button class="remove-item" data-id="${item.id}">&times;</button>
                    </div>
                </div>
            </div>
        `).join('');

        // 绑定数量控制事件
        const decreaseButtons = cartItems.querySelectorAll('.decrease');
        const increaseButtons = cartItems.querySelectorAll('.increase');
        const removeButtons = cartItems.querySelectorAll('.remove-item');

        decreaseButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                updateQuantity(parseInt(this.dataset.id), -1);
            });
        });

        increaseButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                updateQuantity(parseInt(this.dataset.id), 1);
            });
        });

        removeButtons.forEach(btn => {
            btn.addEventListener('click', function() {
                removeFromCart(parseInt(this.dataset.id));
            });
        });

        // 计算总价
        const total = cartList.reduce((sum, item) => sum + parseFloat(item.totalPrice), 0);
        if (totalPrice) {
            totalPrice.textContent = '￥' + total.toFixed(2);
        }
        
        // 更新购物车数量（商品种类数）- 同时更新侧边栏和顶部导航栏
        const itemCount = cartList.length;
        const cartItemCount = document.getElementById('cartItemCount');
        if (cartItemCount) {
            cartItemCount.textContent = itemCount;
        }
        if (cartCount) {
            cartCount.textContent = itemCount;
        }
    }

    // 结算按钮 — 点击即支付成功
    if (checkoutBtn) {
        checkoutBtn.addEventListener('click', async function() {
            try {
                // 先检查购物车是否为空
                const cartResponse = await fetch(`${API_BASE_URL}/cart?userId=${CURRENT_USER_ID}`);
                const cartResult = await cartResponse.json();

                if (cartResult.code !== 200 || !cartResult.data || cartResult.data.length === 0) {
                    showToast('购物车是空的！');
                    return;
                }

                // 调用结算接口（即支付成功）
                const checkoutResponse = await fetch(`${API_BASE_URL}/order/checkout`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        userId: CURRENT_USER_ID
                    })
                });

                const checkoutResult = await checkoutResponse.json();
                if (checkoutResult.code === 200) {
                    const order = checkoutResult.data;
                    const total = order.totalAmount ? parseFloat(order.totalAmount).toFixed(2) : '0.00';
                    showToast(`支付成功！订单总额：￥${total}`);

                    // 刷新购物车显示
                    setTimeout(() => {
                        loadCartFromServer();
                        updateCartCountFromServer();
                    }, 500);
                } else {
                    showToast(checkoutResult.message || '结算失败');
                }
            } catch (error) {
                console.error('结算错误:', error);
                showToast('网络错误，请稍后重试');
            }
        });
    }

    // 显示提示消息
    function showToast(message) {
        const toast = document.createElement('div');
        toast.className = 'toast';
        toast.textContent = message;
        document.body.appendChild(toast);
        
        setTimeout(() => toast.classList.add('show'), 10);
        
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 2000);
    }

    // 初始化购物车数量
    updateCartCountFromServer();
    
    // =========================
    // 用户信息显示和退出登录
    // =========================
    // 更新顶部导航栏显示用户信息
    updateUserInfo();
    
    function updateUserInfo() {
        const user = JSON.parse(userInfo);
        const topLinks = document.querySelector('.top-links');
        const isAdmin = user.role === 1;

        if (topLinks && user) {
            topLinks.innerHTML = `
                <li><span>欢迎，${user.username}${isAdmin ? ' [管理员]' : ''}</span></li>
                <li><a href="#" id="logoutBtn">退出登录</a></li>
                <li><a href="orders.html">我的订单</a></li>
                <li><a href="profile.html">个人中心</a></li>
                <li><a href="help.html">帮助中心</a></li>
                ${isAdmin ? `
                <li><a href="product-manage.html">商品管理</a></li>
                <li><a href="order-manage.html">订单管理</a></li>
                ` : ''}
            `;
            
            // 绑定退出登录事件
            document.getElementById('logoutBtn').addEventListener('click', function(e) {
                e.preventDefault();
                logout();
            });
        }
    }
    
    function logout() {
        // 清除登录信息
        localStorage.removeItem('userInfo');
        localStorage.removeItem('userId');
        sessionStorage.removeItem('userInfo');
        sessionStorage.removeItem('userId');
        
        showToast('已退出登录');
        
        // 跳转到登录页
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1000);
    }

    // =========================
    // 轮播图功能
    // =========================
    const slider = document.getElementById("slider");
    const slides = document.querySelectorAll(".slide");
    const dots = document.querySelectorAll(".dot");
    const prevBtn = document.querySelector(".slider-btn.prev");
    const nextBtn = document.querySelector(".slider-btn.next");

    let currentIndex = 0;
    let timer = null;
    const intervalTime = 3000; // 每3秒切换一次

    // 如果页面中没有轮播区域，直接结束，避免报错
    if (!slider || slides.length === 0) {
        return;
    }

    function showSlide(index) {
        const safeIndex = (index + slides.length) % slides.length;

        slides.forEach(function (slide) {
            slide.classList.remove("active");
        });

        dots.forEach(function (dot) {
            dot.classList.remove("active");
        });

        slides[safeIndex].classList.add("active");

        if (dots[safeIndex]) {
            dots[safeIndex].classList.add("active");
        }

        currentIndex = safeIndex;
    }

    function nextSlide() {
        showSlide(currentIndex + 1);
    }

    function prevSlide() {
        showSlide(currentIndex - 1);
    }

    function startAutoPlay() {
        stopAutoPlay();
        timer = setInterval(nextSlide, intervalTime);
    }

    function stopAutoPlay() {
        if (timer) {
            clearInterval(timer);
            timer = null;
        }
    }

    // 初始化
    showSlide(0);
    startAutoPlay();

    // 点击圆点切换
    dots.forEach(function (dot, index) {
        dot.addEventListener("click", function () {
            showSlide(index);
            startAutoPlay();
        });
    });

    // 点击左右按钮切换
    if (nextBtn) {
        nextBtn.addEventListener("click", function () {
            nextSlide();
            startAutoPlay();
        });
    }

    if (prevBtn) {
        prevBtn.addEventListener("click", function () {
            prevSlide();
            startAutoPlay();
        });
    }

    // 鼠标移入暂停，移出继续
    slider.addEventListener("mouseenter", stopAutoPlay);
    slider.addEventListener("mouseleave", startAutoPlay);

    // =========================
    // 回到顶部按钮
    // =========================
    const backTopBtn = document.getElementById("backTop");

    if (backTopBtn) {
        window.addEventListener("scroll", function () {
            backTopBtn.style.display = window.scrollY > 300 ? "block" : "none";
        });

        backTopBtn.addEventListener("click", function () {
            window.scrollTo({
                top: 0,
                behavior: "smooth"
            });
        });
    }

    // =========================
    // PC/移动端视图切换
    // =========================
    const viewToggleBtn = document.getElementById("viewToggleBtn");
    const mobileFrame = document.querySelector(".mobile-frame");
    const mobileFrameIframe = document.getElementById("mobileFrame");

    if (viewToggleBtn && mobileFrame && mobileFrameIframe) {
        viewToggleBtn.setAttribute("data-tooltip", "切换到手机版");

        viewToggleBtn.addEventListener("click", function () {
            const body = document.body;
            const isMobile = body.classList.contains("mobile-sim");

            if (isMobile) {
                // 切回 PC 版
                body.classList.remove("mobile-sim");
                viewToggleBtn.setAttribute("data-tooltip", "切换到手机版");
                // 延迟清空 iframe，避免某些浏览器报错
                setTimeout(function () {
                    mobileFrameIframe.src = "";
                }, 300);
            } else {
                // 切换到移动端版
                body.classList.add("mobile-sim");
                viewToggleBtn.setAttribute("data-tooltip", "切换回PC版");
                // iframe 加载当前页，在 390px 宽度下触发响应式 CSS
                // 加时间戳强制刷新，避免浏览器使用空白缓存
                mobileFrameIframe.src = window.location.href.split('#')[0] + '?mobile_view=' + Date.now();
            }
        });
    }
});
