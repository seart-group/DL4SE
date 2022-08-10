import axios from 'axios'
import store from '@/store'

const instance = axios.create({
    baseURL: process.env.VUE_APP_API_BASE_URL,
    timeout: 5000
})

const noAuth = [
    "/",
    "/user/login",
    "/user/register",
    "/user/verify",
    "/user/verify/resend"
]

instance.interceptors.request.use(
    request => {
        if (!noAuth.includes(request.url))
            request.headers["authorization"] = store.getters.getToken
        return request
    },
    error => Promise.reject(error)
)

export default instance