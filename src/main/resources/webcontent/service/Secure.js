import { Axios } from "../modules/Axios.js"
import { createClient,AuthType } from "../modules/DAVClient.js"

const BASE_URL_DEV = "http://localhost:8080";
const BASE_URL = "/"

export class Secure {

    static requestor = new Axios({
        baseURL: BASE_URL
    });

    static client = null;

    static token = null;

    /**
     * 尝试登录
     * @param {*} username 用户名
     * @param {*} password 密码
     * @param {*} callback Callback，登录后调用
     */
    static login(username , password, callback) {
        const axios = Secure.requestor;
        axios.post("/@auth/login", JSON.stringify({
            username,
            password
        }), {
            headers: {
                "Content-Type": "plain/text"
            }
        }).then(resp => {
            if(resp.status === 200) {
                Secure.token = resp.data;
                window.sessionStorage.setItem("TOKEN", Secure.token);
                if(callback) {
                    callback();
                }
            }
        })
    }

    /**
     * 判断是否可注册
     * @param {*} callback 
     */
    static registerSupported(callback) {
        const axios = Secure.requestor;
        axios.get('/@api/register', {
            headers: {
                "Content-Type": "application/json"
            }
        }).then(resp => {
            callback(JSON.parse(resp.data))
        })
    }

    /**
     * 获取可加入的分组/分区
     * @param {*} callback 
     */
    static getRegisterGroups(callback) {
        const axios = Secure.requestor;
        axios.get("/@api/groups", {
            headers: {
                "Content-Type": "application/json"
            }
        }).then(resp => {
            callback(JSON.parse(resp.data))
        })
    }

    /**
     * 发送注册请求。
     * @param {*} username 
     * @param {*} password 
     * @param {*} nickname 
     * @param {*} groupId 
     * @param {*} callback 
     */
    static sendRegister(username, password, nickname, groupId, callback) {
        const data = {
            nickname: nickname,
            name: username,
            password: password,
            group: {
                id: groupId
            }
        };
        const axios = Secure.requestor;
        axios.post("/@api/register", JSON.stringify(data), {
            headers: {
                "Content-Type": "application/json"
            }
        }).then(resp => {
            callback(JSON.parse(resp.data))
        });
    }

    /**
     * 获取绝对路径
     * @param {} url 
     * @returns 
     */
    static getUrl(url) {
        if(!url.startsWith("/")) {
            url = "/" + url;
        }
        return BASE_URL + url;
    }

    /**
     * 获取当前登录账号的Token
     * @returns 当前的Token
     */
    static getToken() {
        if(!Secure.token) {
            let token = window.sessionStorage.getItem("TOKEN");
            Secure.token = token;
        }
        return Secure.token;
    }

    /**
     * 获取登录状态
     * @returns boolean
     */
    static hasLogined() {
        if(this.token) {
            return true;
        }
        let token = window.sessionStorage.getItem("TOKEN");
        return token !== null
    }

    /**
     * 退出登录
     */
    static logout() {
        this.token = null;
        window.sessionStorage.removeItem("TOKEN");
    }

    /**
     * 获取WebDAV客户端
     * @returns DAV客户端
     */
    static getDAVClient() {
        if(!this.hasLogined()) {
            return null;
        }
        if(!this.client) {

            let realBaseUrl = BASE_URL;
            if(!realBaseUrl.startsWith("http")) {
                let protocol = window.location.protocol;
                let host = window.location.host;
                realBaseUrl = protocol + "//" + host + BASE_URL;
            }

            this.client = createClient(realBaseUrl,{
                authType: AuthType.Token,
                token: {
                    access_token: Secure.getToken(),
                    token_type: "Bearer"
                }
            })

        }
        return this.client;
    }

    /**
     * 字符串是否为空
     * @param {*} str 字符串 
     * @returns boolean
     */
    static isStringBlank(str) {
        if(typeof str !== "string") {
            return true;
        }
        return !(/[\S]+/g.test(str));
    }
}