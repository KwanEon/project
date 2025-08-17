# 1단계: React build
FROM node:20 AS build
WORKDIR /app

# 의존성 파일 먼저 복사
COPY package.json package-lock.json ./

# 의존성 설치 (이 단계는 package.json이 변경될 때만 다시 실행됨)
RUN npm install

# 소스 코드 복사
COPY . .

# 앱 빌드
RUN npm run build

# 2단계: Nginx에서 정적 파일 서빙
FROM nginx:alpine

# 빌드된 파일 복사
COPY --from=build /app/build /usr/share/nginx/html

# 내가 만든 nginx.conf 복사 (nginx 기본 설정 덮어쓰기)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
