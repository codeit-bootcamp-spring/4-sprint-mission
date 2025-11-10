# ./frontend/Dockerfile
FROM nginx:alpine
# 빌드 산출물 경로에 맞춰 수정하세요. (예: ./frontend/static 또는 ./frontend/dist)
COPY ./static/ /usr/share/nginx/html/
# 또는 Vite/Next 빌드라면 보통 dist/ 이나 .next/static/ 등
# COPY ./dist/ /usr/share/nginx/html/
EXPOSE 80
