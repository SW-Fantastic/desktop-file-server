import { h } from "../modules/vue.esm.js";
import { TableHeader,TableBody,PreviewModal } from "../components/Controls.js";
import { Card,TreeNode,Modal,ToolBar } from "../components/Containers.js";
import { Button,TextInput } from "../components/Input.js";

import { Secure } from "../service/Secure.js";

const HeaderBar = {

    name: "HeaderBar",

    render() {
        return h('div', {
            class: "p-6 bg-white shadow-md flex flex-row justify-between items-center"
        }, [
            h('div', {}, [ h('div', {
                class: "text-xl text-gray-500"
            }, "StartCloud") ]), 
            h('div', {}, [])
        ]);
    }

}


const TrashModal = {

    name: "TrashModal", 

    emits: ["refresh"],

    props: {
        target: {
            default: null
        }
    },

    render() {
        return h(Modal, { ref: 'modal' }, () => [
            h('div', { class: "flex flex-col" }, [ 
                h('div', {
                    class: "flex flex-row justify-between items-center px-6 py-5 text-gray-600"
                }, [
                    h('div', {}, '删除'),
                    h('div', {
                        class: "w-6 h-6 flex flex-col items-center justify-center text-gray-400 hover:text-gray-600"
                    }, [h("i", {
                        class: "fas fa-times text-xl ",
                        onclick: this.hide
                    })])
                ]),
                h('div', { class: 'px-6' }, [
                    h('span', {}, "你确实要删除文件（夹）《" + this.target.basename + "》吗？")
                ]),
                h('div', { class: 'py-4 px-6' }, [
                    h(Button,{
                        color: 'red',
                        onclick: this.trashFolder
                    }, () => [
                        h('span', {}, '删除')
                    ])
                ])
            ])
        ])
    },

    methods: {
        show() {
            this.$refs.modal.show();
        },
        hide() {
            this.$refs.modal.hide();
        },
        trashFolder() {
            if(!this.target) {
                return;
            }

            let path = "";
            let current = this.target;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            let client = Secure.getDAVClient();
            client.deleteFile(path).then(resp => {
                this.$emit("refresh");
                this.hide();
            }).catch(e => {
                if(e.status === 401) {
                    Secure.logout();
                    this.$router.replace("/login")
                }
            });
        }
    }

}

const AddFolderModal = {

    name: "AddFolderModal", 

    emits: ["refresh"],

    props: {
        parent: {
            default: null
        }
    },

    data() {
        return {
            folderName: ''
        }
    },

    render() {
        return h(Modal, { ref: 'modal' }, () => [
            h('div', { class: "flex flex-col" }, [ 
                h('div', {
                    class: "flex flex-row justify-between items-center px-6 py-5 text-gray-600"
                }, [
                    h('div', {}, '创建文件夹'),
                    h('div', {
                        class: "w-6 h-6 flex flex-col items-center justify-center text-gray-400 hover:text-gray-600"
                    }, [h("i", {
                        class: "fas fa-times text-xl ",
                        onclick: this.hide
                    })])
                ]),
                h('div', { class: 'px-6' }, [
                    h(TextInput, {
                        placeholder: "输入文件夹名称...",
                        modelValue: this.folderName,
                        'onUpdate:modelValue':this.patchState('folderName')
                    })
                ]),
                h('div', { class: 'py-4 px-6' }, [
                    h(Button,{
                        color: 'blue',
                        onclick: this.createFolder
                    }, () => [
                        h('span', {}, '保存')
                    ])
                ])
            ])
        ])
    },

    methods: {
        createFolder() {
            if(this.parent == null) {
                return
            }
            if(Secure.isStringBlank(this.folderName)) {
                return
            }
            let path = "";
            let current = this.parent;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }

            path = path.replaceAll(/[\/+]+/g, "/")
            let newPath = path;
            if(path.endsWith("/")) {
                newPath = path + this.folderName;
            } else {
                newPath = path + "/" + this.folderName
            }

            let client = Secure.getDAVClient();
            client.createDirectory(newPath).then(resp => {
                this.$emit("refresh");
                this.hide();
            }).catch(e => {
                if(e.status === 401) {
                    Secure.logout();
                    this.$router.replace("/login")
                }
            });
        },
        show() {
            this.$refs.modal.show();
        },
        hide() {
            this.$refs.modal.hide();
        },
        patchState(key) {
            return val => {
                this[key] = val;
            }
        }
    }

}

const ModalRename = {

    name: "RenameModal",

    emits: ["refresh"],

    props: {
        target: {
            default() {
                return null
            }
        }
    },

    watch: {
        target(val) {
            if(val) {
                this.name = val.basename;
            }
        }
    },

    data() {
        return {
            name: this.target ? this.target.basename : ""
        }
    },

    render() {
        return h(Modal, { ref: 'modal' }, () => [
            h('div', { class: "flex flex-col" }, [ 
                h('div', {
                    class: "flex flex-row justify-between items-center px-6 py-5 text-gray-600"
                }, [
                    h('div', {}, '重命名文件（夹）'),
                    h('div', {
                        class: "w-6 h-6 flex flex-col items-center justify-center text-gray-400 hover:text-gray-600"
                    }, [h("i", {
                        class: "fas fa-times text-xl ",
                        onclick: this.hide
                    })])
                ]),
                h('div', { class: 'px-6' }, [
                    h(TextInput, {
                        placeholder: "输入新的名称...",
                        modelValue: this.name,
                        'onUpdate:modelValue':this.patchState('name')
                    })
                ]),
                h('div', { class: 'py-4 px-6' }, [
                    h(Button,{
                        color: 'blue',
                        onclick: this.doRename
                    }, () => [
                        h('span', {}, '保存')
                    ])
                ])
            ])
        ])
    },

    methods: {
        show() {
            this.$refs.modal.show();
        },
        hide() {
            this.$refs.modal.hide();
        },
        doRename() {
            if(!this.target || Secure.isStringBlank(this.name)) {
                return;
            }

            let path = "";
            let current = this.target;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            path = path.replaceAll(/[\/+]+/g, "/")
            if(path.endsWith("/")) {
                path = path.substring(0, path.length - 1);
            }
            let parentPath = path.substring(0, path.lastIndexOf("/"));
          
            let source = parentPath + "/" + this.target.basename;
            let renamed = parentPath + "/" + this.name;

            let client = Secure.getDAVClient();
            client.moveFile(source, renamed).then(resp => {
                this.target.basename = this.name;
                this.hide();
                this.$emit("refresh")
            }).catch(e => {
                if(e.status === 401) {
                    Secure.logout();
                    this.$router.replace("/login")
                }
            })

        },
        patchState(key) {
            return val => {
                this[key] = val;
            }
        }
    }
}


export default {

    name: "Index",

    data() {

        const ofIcon = (icon, key) => {
            return {icon, key}
        }

        const ofCol = (label, key, width, full = false) => {
            return { label, key, width, full }
        }
        
        return {
            toolIcons: [
                ofIcon('plus', 'addFolder'),
                ofIcon('trash-alt', 'trashFolder'),
                ofIcon('home','home'),
                ofIcon('pencil-alt', "renameFolder")
            ],
            tableColumns: [
                ofCol("", "icon", 64), 
                ofCol("文件名", "name", 300), 
                ofCol('文件大小', 'size', 140),
                ofCol('日期', 'date', 200),ofCol("", "op", null, true)
            ],
            currentNode: null,
            currentFile: null,
            rootNode: {
                basename: "/",
                children: [],
                files: []
            }
        }
    },

    created() {
        
        this.currentNode = this.rootNode;
        this.currentFile = {
            basename: ""
        }
        this.stateChange({
            node: this.rootNode,
            state: true
        });

    },

    render() {
        return h('div', {
            class: "w-screen h-screen flex flex-col"
        }, [
            h(AddFolderModal, {
                ref: "addModal",
                parent: this.currentNode,
                onRefresh: this.refreshCurrent
            }),
            h(TrashModal, {
                ref: "trashModal",
                target: this.currentNode,
                onRefresh: this.refreshParent
            }),
            h(TrashModal, {
                ref: "trashFileModal",
                target: this.currentFile,
                onRefresh: this.refreshCurrent
            }),
            h(PreviewModal, {
                ref: 'previewModal',
                file: this.currentFile
            }),
            h(ModalRename, {
                ref: "renameModal",
                target: this.currentNode
            }),
            h(ModalRename, {
                ref: "renameFileModal",
                target: this.currentFile
            }),
            h(HeaderBar, {}),
            h('div', {
                class: "flex w-full flex-row p-6 gap-x-6 h-full"
            }, [
                h('div', {
                    class: "min-w-[360px] max-h-full h-full flex flex-col gap-y-2"
                }, [
                    h(ToolBar, {
                        items: this.toolIcons,
                        onItemClick: this.dispatchToolClicked
                    }),
                    h(Card,{
                        class: "h-full max-h-full"
                    }, () => h(TreeNode, {
                        labelProp: "basename",
                        childrenProp: "children",
                        node: this.rootNode,
                        root: true,
                        active: this.currentNode,
                        onChanged: this.stateChange,
                        onSelected: this.stateChange
                     }))
                ]),
                h('div', {
                    class: "w-full"
                }, [
                    h(Card, {
                        class: "flex flex-col h-full max-h-full"
                    }, () => [
                        h(TableHeader, {
                            columns: this.tableColumns
                        }),
                        h(TableBody, {
                            columns: this.tableColumns,
                            data: this.currentNode.files || []
                        },{
                            icon: (scope) => [h('div', { class: "w-full h-full flex flex-col items-center justify-center"}, [
                                h('i', {
                                    class: this.getFileIcon(scope.row.basename) + " text-lg"
                                })
                            ])],
                            name: (scope) => [h('div',{ class: "w-full h-full flex flex-col items-start justify-center text-sm"}, [
                                h('span', {}, scope.row.basename)
                            ])],
                            size: (scope) => [h('div',{ class: "w-full h-full flex flex-col items-start justify-center text-sm"}, [
                                h('span', {}, Math.ceil(scope.row.size / 1024) + ' KB')
                            ])],
                            date: (scope) => [h('div',{ class: "w-full h-full flex flex-col items-start justify-center text-sm"}, [
                                h('span', {}, this.getDateStr(scope.row.lastmod))
                            ])],
                            op: (scope) => [h('div',{ class: "w-full h-full flex flex-row items-center justify-end text-sm"}, [
                                h('div', { class: 'text-lg p-2 text-gray-300 hover:text-gray-500'}, [h('i', {
                                    class: 'fas fa-folder-open',
                                    onclick: e => this.preview(scope.row)
                                })]),
                                h('div', { class: 'text-lg p-2 text-gray-300 hover:text-gray-500'}, [h('i', {
                                    class: 'fas fa-pencil-alt',
                                    onclick: e => this.renameFile(scope.row)
                                })]),
                                h('div', { class: 'text-lg p-2 text-gray-300 hover:text-gray-500'}, [h('i', {
                                    class: 'fas fa-download',
                                    onclick: e => this.downloadFile(scope.row)
                                })]),
                                h('div', { class: 'text-lg p-2 text-gray-300 hover:text-gray-500'}, [h('i', {
                                    class: 'fas fa-trash',
                                    onclick: e => this.trashFile(scope.row)
                                })]),
                            ])],
                        })
                    ])
                ])
            ])
        ]);
    },

    methods: {
        /**
         * 根据文件类型返回适当的图标。
         */
        getFileIcon(name) {
            if(name.indexOf(".") < 0) {
                return "fas fa-file"
            } else {
                let subfix = name.substring(name.indexOf(".") + 1).toLowerCase();
                const images = ["png", "jpg", "gif", "jpeg", "svg", "webp", "bmp", "tiff"];
                const text = ["txt"];
                const config = ["ini", "inf", "conf", "cfg", "properties", "yml"];
                const source = ["c","java", "cpp","h", "go", "js","cjs","mjs","cc", "m","html", "css", "json"];
                const audios = ["mp3", "wav", "aac", "midi", "ogg"];
                const videos = ["mp4", "mkv", "avi", "wmv", "flv"];
                const archives = ["rar", "zip","7z", "gz","tar","xz", "tgz","bz", "bz2"];
                const doc = ["doc", "docx", "xls", "xlsx", "ppt", "pptx", "xlsb"];
                if(images.indexOf(subfix) > -1) {
                    return "fas fa-file-image"
                }
                if(config.indexOf(subfix) > -1) {
                    return "fas fa-cog"
                }
                if(source.indexOf(subfix) > -1) {
                    return "fas fa-code"
                }
                if(archives.indexOf(subfix) > -1) {
                    return "fas fa-file-archive"
                }
                if(doc.indexOf(subfix) > -1) {
                    return "fas fa-file-word"
                }
                if(audios.indexOf(subfix) > -1) {
                    return "fas fa-file-audio"
                }
                if(videos.indexOf(subfix) > -1) {
                    return "fas fa-file-video"
                }
                return "fas fa-file"
            }
        },
        /**
         * 日期格式化，返回日期字符串 
         */
        getDateStr(date) {
            let target = new Date(date);
            return target.getFullYear() + "/" + (target.getMonth() + 1) + "/" + target.getDate();
        },
        /**
         * 刷新文件夹节点。 
         */
        stateChange({ node, state}) {
            let path = "";
            let current = node;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            path = path.replaceAll(/\/+/g,"/")
            let client = Secure.getDAVClient();
            client.getDirectoryContents(path).then(resp => {
                let items = resp.filter(it => it.basename !== node.basename);
                let folders = items.filter(it => it.type === "directory");
                let files = items.filter(it => it.type !== "directory");
                for(let file of files) {
                    file.__parent = node;
                }
                node.files = files;
                node.children = folders;
            }).catch(e => {
                if(e.status === 401) {
                    Secure.logout();
                    this.$router.replace("/login")
                }
            });
            this.currentNode = node;
        },
        /**
         * 工具栏，事件分发 
         */
        dispatchToolClicked(key) {
            this[key]();
        },
        /**
         * 工具按钮，返回主文件夹
         */
        home() {
            this.stateChange({
                node: this.rootNode,
                state: true
            });
        },
        /**
         * 工具按钮，添加文件夹
         */
        addFolder() {
            this.$refs.addModal.show();
        },
        /**
         *  工具按钮，目录重命名
         */
        renameFolder() {
            if(this.currentNode === this.rootNode) {
                return
            }
            this.$refs.renameModal.show();
        },
        /**
         * 刷新当前目录
         */
        refreshCurrent() {
            this.stateChange({
                node: this.currentNode,
                state: true
            });
            this.currentFile = null;
        },
        /**
         * 删除选中的文件夹
         */
        trashFolder() {
            if(this.currentNode.__parent) {
                this.$refs.trashModal.show()
            }
        },
        /**
         * 刷新当前文件夹的父文件夹
         */
        refreshParent() {
            this.stateChange({
                node: this.currentNode.__parent,
                state: true
            });
        },
        /**
         * 删除指定的文件
         */
        trashFile(file) {
            this.currentFile = file;
            this.$refs.trashFileModal.show();
        },
        /**
         * 预览该文件 
         */
        preview(file) {
            this.currentFile = file;
            this.$refs.previewModal.show();
        },
        /**
         * 重命名该文件
         */
        renameFile(file) {
            this.currentFile = file;
            this.$refs.renameFileModal.show();
        },
        /**
         * 下载文件 
         */
        downloadFile(file) {
            
            let path = "";
            let current = file;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            path = path.replaceAll(/[\/+]+/g, "/")
            if(path.endsWith("/")) {
                path = path.substring(0,path.length - 1);
            }

            let client = Secure.getDAVClient();
            client.getFileContents(path).then(data => {
                let bufferedUrl = URL.createObjectURL(new Blob([data],  { type : file.mime}));
                let link = document.createElement("a");
                link.setAttribute("download", file.basename);
                link.setAttribute("href", bufferedUrl);
                document.body.appendChild(link);
                link.click();
                link.remove();
            });
        
        }
    }

}