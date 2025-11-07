# Nginx 베이스 이미지 사용
FROM nginx:1.25-alpine

# nginx 설정 파일 복사
COPY ./nginx/conf/default.conf /etc/nginx/conf.d/default.conf

# 프론트엔드 정적 파일 복사
COPY ./static /usr/share/nginx/html

# Nginx는 기본적으로 80포트 사용
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]