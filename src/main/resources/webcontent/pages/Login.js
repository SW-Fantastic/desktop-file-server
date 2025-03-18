import { h } from "../modules/vue.esm.js"
import { Card } from "../components/Containers.js"
import { TextInput,Button } from "../components/Input.js"
import { Secure } from "../service/Secure.js"

export default {

    name: "Login",

    data() {
        return {
            username: "",
            password: "",
            registerable: false
        }
    },

    created() {
        Secure.registerSupported(resp => {
            if(resp.code === 200) {
                this.registerable = resp.data
            }
        })
    },

    render() {

        let registerPart = null;
        if(this.registerable) {
            registerPart = h('div', {
                class: 'w-full flex flex-row items-center justify-center text-gray-500 text-sm'
            }, [
                h('span', {}, '没有账号？'),
                h('a', {
                    class: "text-blue-400 cursor-pointer",
                    onclick: this.register
                }, '点击注册')
            ])
        } else {
            registerPart = h('div', {
                class: 'w-full flex flex-row items-center justify-center text-gray-500 text-sm'
            }, [
                h('span', {}, '没有账号？请联系管理员'),
            ])
        }

        return h('div', {
            class: "w-screen h-screen flex flex-col justify-center items-center"
        }, h(Card, {
            class: "w-[600px]"
        }, () => [
            h('div', {
                class: "flex flex-col gap-y-4"
            }, [
                h('h2', {
                    class: "font-semibold text-2xl px-4"
                }, [ "登录到繁星云" ]),
                h('div', {
                    class: "w-full border-t border-1 border-gray-200"
                }),
                h('div', {
                    class: "px-4 "
                }, "用户名"),
                h(TextInput, {
                    placeholder: "请输入用户名",
                    modelValue: this.username,
                    englishMode: true,
                    'onUpdate:modelValue':this.patchState('username')
                }),
                h('div', {
                    class: "px-4 "
                }, "密码"),
                h(TextInput, {
                    placeholder: "请输入密码",
                    type: "password",
                    modelValue: this.password,
                    'onUpdate:modelValue': this.patchState('password')
                }),
                h(Button, {
                    color: "blue",
                    onclick: this.login
                }, () => "登录"),
                registerPart,
                h("div", {
                    class: "flex flex-row items-center justify-center text-sm text-gray-400"
                }, "Fnatastic 2024 MIT Licensed.")
            ])
        ]))
    },

    methods: {

        login() {
           Secure.login(this.username, this.password, () => {
            this.$router.replace("/")
           });
        },

        register() {
            if(this.registerable) {
                this.$router.replace("/register")
            }
        },

        patchState(key) {
            return val => {
                this[key] = val;
            }
        }

    }

}