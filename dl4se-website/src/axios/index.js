import axios from "axios";
import store from "@/store";

const instance = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL,
  timeout: 5000,
});

const noAuth = [
  "/",
  "/user/login",
  "/user/register",
  "/user/verify",
  "/user/verify/resend",
  "/user/password/forgotten",
  "/user/password/reset",
  "/statistics/users",
  "/statistics/repos",
  "/statistics/files",
  "/statistics/functions",
  "/statistics/tasks",
  "/statistics/code/size",
  "/statistics/code/lines",
  "/statistics/tasks/size",
  "/statistics/languages/repos",
  "/statistics/languages/files",
  "/statistics/languages/functions",
];

instance.interceptors.request.use(
  (request) => {
    if (!noAuth.includes(request.url)) request.headers["Authorization"] = store.getters.getToken;
    if (request.method === "POST") request.headers["Content-Type"] = "application/json";
    return request;
  },
  (error) => Promise.reject(error),
);

export default instance;
