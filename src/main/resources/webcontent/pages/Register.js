
import { h } from "../modules/vue.esm.js"
import { TextInput,Dropdown,Button } from "../components/Input.js"
import { Card, Modal } from "../components/Containers.js"
import { Secure } from "../service/Secure.js"

export default {

    name: "Register",

    data() {
        return {
            
            avatar: '',
            nickname: '',
            username: '',
            password: '',
            passwordRepeat: '',
            groupSelected: null,

            groups: [],
            messageText: ''
        
        }
    },

    created() {
        this.avatar = Secure.getUrl("@api/default-avatar");
        Secure.getRegisterGroups(gp => {
            this.groups = gp.data;
        })
    },

    render() {

        let groupsElem = [];

        for(const item of this.groups) {
            let cell = h('div', {
                class: "w-full flex flex-row items-center justify-center group text-black"
            }, [
                h('div', {
                    onclick: e => this.groupSelect(item),
                    class: 'w-full px-4 py-2 group-hover:bg-blue-100 bg-white'
                }, item.groupName)
            ]);
            groupsElem.push(cell);
        }

        return h('div', {
            class: "w-screen h-screen flex flex-col justify-center items-center"
        },[
            h(Modal, {
                ref: 'modalMessage'
            } ,() => [
                h(Card, {}, () => [
                    h('div', {
                        class: "p-2 flex flex-col"
                    }, [
                        h('div', {
                            class: 'flex flex-row w-full justify-between items-center'
                        }, [
                            h('span', {}, this.messageText),
                        ]),
                        h('div', {
                            class: ' w-full mt-6'
                        },[
                            h(Button, {
                                color: 'blue',
                                onclick: e => this.$refs.modalMessage.hide()
                            }, () => "确定")
                        ])
                    ])
                ])
            ]),
            h(Card, {
                class: "w-[600px] relative"
            }, () => [
                h('div', {
                    class: "flex flex-col gap-y-4"
                }, [
                    h('div', {
                        class: "ml-[-16px] w-full flex flex-row items-center justify-center rounded-full absolute top-[-60px]"
                    }, [
                        h('div', {
                            class: 'rounded-full bg-white shadow p-2 overflow-hidden'
                        }, [
                            h('img', {
                                src: this.avatar,
                                class: 'w-[120px] h-[120px]'
                            })
                        ])
                    ]),
                    h('h2', {
                        class: "font-semibold text-2xl px-4"
                    }, [ "用户注册" ]),
                    h('div', {
                        class: 'py-2'
                    }),
                    h('div', {
                        class: "px-4 "
                    }, "分区"),
                    h(Dropdown,{}, {
                        content: () => groupsElem,
                        default: () => h('div', {
                            class: this.groupSelected ? "text-black" : "text-gray-400",
                        }, this.groupSelected ? this.groupSelected.groupName : '请选择')
                    }),
                    h('div', {
                        class: "px-4 "
                    }, "用户名"),
                    h(TextInput, {
                        placeholder: "请输入用户名",
                        englishMode: true,
                        modelValue: this.username,
                        'onUpdate:modelValue':this.patchState('username')
                    }),
                    h('div', {
                        class: "px-4 "
                    }, "昵称"),
                    h(TextInput, {
                        placeholder: "请输入名字",
                        modelValue: this.nickname,
                        'onUpdate:modelValue':this.patchState('nickname')
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
                    h('div', {
                        class: "px-4 "
                    }, "重复密码"),
                    h(TextInput, {
                        placeholder: "请输入密码",
                        type: "password",
                        modelValue: this.passwordRepeat,
                        'onUpdate:modelValue': this.patchState('passwordRepeat')
                    }),
                    h(Button, {
                        color: "blue",
                        onclick: this.register
                    }, () => "申请新账号"),
                    h('div', {
                        class: 'w-full flex flex-row justify-center items-center text-sm text-gray-400'
                    }, [
                        h('span', {}, "已有账号？"),
                        h('a', {
                            class: 'text-blue-400 cursor-pointer',
                            onclick: this.toLogin
                        }, '点击登录')
                    ])
                ])
            ])
        ])
    },

    methods: {
        groupSelect(gp) {
            this.groupSelected = gp;
        },
        toLogin() {
            this.$router.replace("/login")
        },
        register() {
            if(Secure.isStringBlank(this.username)) {
                this.messageText = '用户名不能为空，请检查您输入的内容。'
                this.$refs.modalMessage.show();
                return;
            }
            if(Secure.isStringBlank(this.password)) {
                this.messageText = '密码不能为空，请检查您输入的内容。'
                this.$refs.modalMessage.show();
                return;
            }
            if(Secure.isStringBlank(this.passwordRepeat)) {
                this.messageText = '您需要重复一遍自己输入的密码。'
                this.$refs.modalMessage.show();
                return;
            }
            if(Secure.isStringBlank(this.nickname)) {
                this.messageText = '名字不能为空，请检查您输入的内容。'
                this.$refs.modalMessage.show();
                return;
            }
            if(this.password !== this.passwordRepeat) {
                this.messageText = '两次输入的密码不一致，请尝试重新输入。'
                this.$refs.modalMessage.show();
                return;
            }
            if(!this.groupSelected) {
                this.messageText = '您需要加入一个分区或者用户组。'
                this.$refs.modalMessage.show();
                return;
            }
            Secure.sendRegister(
                this.username,
                this.password,
                this.nickname,
                this.groupSelected.id,
                resp => {
                    if(resp.code === 200) {
                        if(resp.data === "ok") {
                            this.messageText = '账号已经注册成功了，此时您可以使用此账号登录系统了。';
                            setTimeout(() => {
                                this.$router.replace("/login", 1000 * 5)
                            })
                        } else {
                            this.messageText = '账号已经注册成功了，管理员会尽快审核您的注册请求。';
                        }
                        this.$refs.modalMessage.show();
                    } else {
                        if(resp.data === 'exist') {
                            this.messageText = '您注册的用户名已经被占用，请换一个。';
                        } else {
                            this.messageText = '注册失败，请和管理员联系';
                        }
                        this.$refs.modalMessage.show();
                    } 
                }
            )
        },
        patchState(key) {
            return val => {
                this[key] = val;
            }
        }
    }

}