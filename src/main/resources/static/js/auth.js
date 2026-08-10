const USER_STORAGE_KEY = 'iot_users';
const SESSION_KEY = 'iot_current_user';
const WELCOME_SHOWN_KEY = 'iot_welcome_shown';

function getUsers() {
  try {
    return JSON.parse(localStorage.getItem(USER_STORAGE_KEY)) || [];
  } catch {
    return [];
  }
}

function saveUsers(users) {
  localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(users));
}

function initDefaultUser() {
  const users = getUsers();
  if (users.length === 0) {
    saveUsers([{ username: 'admin', password: '123456' }]);
  }
}

function registerUser(username, password) {
  initDefaultUser();
  const users = getUsers();
  if (users.some(u => u.username === username)) {
    return { success: false, message: '用户名已存在' };
  }
  if (username.length < 2) {
    return { success: false, message: '用户名至少 2 个字符' };
  }
  if (password.length < 6) {
    return { success: false, message: '密码至少 6 位' };
  }
  users.push({ username, password });
  saveUsers(users);
  return { success: true };
}

function loginUser(username, password) {
  initDefaultUser();
  const users = getUsers();
  const user = users.find(u => u.username === username && u.password === password);
  if (!user) {
    return { success: false, message: '用户名或密码错误' };
  }
  sessionStorage.setItem(SESSION_KEY, username);
  sessionStorage.removeItem(WELCOME_SHOWN_KEY);
  return { success: true, username };
}

function getCurrentUser() {
  return sessionStorage.getItem(SESSION_KEY);
}

function logout() {
  sessionStorage.removeItem(SESSION_KEY);
  sessionStorage.removeItem(WELCOME_SHOWN_KEY);
  window.location.href = '/login.html';
}

function requireAuth() {
  if (!getCurrentUser()) {
    window.location.href = '/login.html';
    return false;
  }
  return true;
}

function shouldShowWelcome() {
  return getCurrentUser() && !sessionStorage.getItem(WELCOME_SHOWN_KEY);
}

function markWelcomeShown() {
  sessionStorage.setItem(WELCOME_SHOWN_KEY, '1');
}

initDefaultUser();
