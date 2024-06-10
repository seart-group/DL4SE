FROM node:18.20.3-alpine AS build

COPY ./dl4se-website .

COPY <<-EOF .env
VUE_APP_API_BASE_URL=http://localhost:8080/api
EOF

RUN npm ci && npm run build

FROM nginx:1.26.0-alpine-slim AS production

ENV TZ=UTC

COPY --from=build /dist /usr/share/nginx/html
COPY ./nginx/nginx.conf.template /etc/nginx/templates/default.conf.template

ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]

EXPOSE 80 8000

HEALTHCHECK \
    --start-period=10s \
    --interval=60s \
    --timeout=30s \
    --retries=5 \
    CMD nc -z 127.0.0.1 80 || exit 1
