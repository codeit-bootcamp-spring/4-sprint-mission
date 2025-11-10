FROM nginx:1.27-alpine

# 설정 넣기
COPY nginx/nginx.conf /etc/nginx/nginx.conf

# 정적 파일 복사 (루트에 frontend 디렉터리 있으니까 이렇게 가능)
COPY frontend /usr/share/nginx/html

EXPOSE 80
