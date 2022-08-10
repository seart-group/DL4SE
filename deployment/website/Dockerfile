FROM node:18.7-alpine as install
ARG WEBSITE_PORT
ARG SERVER_URL
COPY ./dl4se-website/package*.json ./
RUN npm install
COPY ./dl4se-website .

FROM install as build
ARG WEBSITE_PORT
ARG SERVER_URL
ARG NODE_ENV=production
RUN apk update && apk add gettext
RUN envsubst < .env.template > .env
RUN npm run build

FROM nginx:1.23-alpine as production
COPY --from=build /dist /usr/share/nginx/html
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]