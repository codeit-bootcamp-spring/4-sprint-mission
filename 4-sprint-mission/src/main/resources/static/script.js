// API endpoints
const API_BASE_URL = '/api';
const ENDPOINTS = {
    USERS: `${API_BASE_URL}/user/findAll`,
    BINARY_CONTENT: `${API_BASE_URL}/binaryContent/find`
};

document.addEventListener('DOMContentLoaded', () => {
    fetchAndRenderUsers();
});

async function fetchAndRenderUsers() {
    try {
        const response = await fetch(ENDPOINTS.USERS);
        if (!response.ok) throw new Error('사용자 데이터를 가져오는 데 실패했습니다.');
        const users = await response.json();
        renderUserList(users);
    } catch (error) {
        console.error('🚨 사용자 목록 요청 실패:', error);
    }
}

async function fetchUserProfile(profileId) {
    try {
        const response = await fetch(`${ENDPOINTS.BINARY_CONTENT}?binaryContentId=${profileId}`);
        if (!response.ok) throw new Error('프로필 이미지 요청 실패');
        const profile = await response.json();

        return `data:${profile.contentType};base64,${profile.data}`;
    } catch (error) {
        console.error('🚨 프로필 이미지 오류:', error);
        return '/default-avatar.png';
    }
}

async function renderUserList(users) {
    const userListElement = document.getElementById('userList');
    userListElement.innerHTML = '';

    for (const user of users) {
        const userElement = document.createElement('div');
        userElement.className = 'user-item';

        const profileUrl = user.profileId
            ? await fetchUserProfile(user.profileId)
            : '/default-avatar.png';

        userElement.innerHTML = `
            <img src="${profileUrl}" alt="${user.userName}" class="user-avatar">
            <div class="user-info">
                <div class="user-name">${user.userName}</div>
                <div class="user-email">${user.userEmail}</div>
            </div>
            <div class="status-badge ${user.online ? 'online' : 'offline'}">
                ${user.online ? '온라인' : '오프라인'}
            </div>
        `;

        userListElement.appendChild(userElement);
    }
}
