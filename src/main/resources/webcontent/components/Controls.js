import { h, render } from "../modules/vue.esm.js"
import { Modal } from "./Containers.js";
import { Secure } from "../service/Secure.js";
import { Button } from "./Input.js";

export const TableHeader = {

    name: "TableHeader",

    props: {
        columns: {
            default() {
                return []
            }
        }
    },

    render() {
        let columns = [];
        for(const col of this.columns) {
            columns.push(h('div', {
                class: 'border-r border-1 border-gray-300 px-4 py-2 flex flex-col',
                style: {
                    width: (col.width ? col.width + 'px' : ''),
                    flexGrow: col.full ? 1 : 0
                }
            }, [ col.label ]))
        }
        return h('div', {
            class: "flex flex-row border-gray-300 border border-1 text-gray-500 select-none"
        }, columns)
    }

}

export const TableBody = {

    name: "TableBody", 

    props: {
        columns: {
            default() {
                return []
            }
        },
        data: {
            default() {
                return []
            }
        },
        rowHeight: {
            default: 48
        }
    },

    render() {
        
        let rows = [];
        for(const rowData of this.data) {
            let cells = [];
            for(const col of this.columns) {
                let renderFunc = this.$slots[col.key];
                let cell = null;
                if(renderFunc) {
                    cell = renderFunc({
                        row: rowData
                    });
                } else {
                    cell = h('div')
                }
                cells.push(h('div', {
                    class: "overflow-hidden px-4 py-2 flex flex-col border-r border-gray-300 border-1",
                    style: {
                        width: (col.width ? col.width + 'px' : ''),
                        flexGrow: col.full ? 1 : 0,
                        height: this.rowHeight + 'px'
                    }
                },cell));
            }
            let row = h('div', {
                class: 'flex flex-row border-b border-gray-300 border-1 bg-white hover:bg-gray-100'
            },cells);
            rows.push(row);
        }

        return h('div', {
            class: "flex flex-col border-gray-300 border-l border-r border-1 text-gray-500"
        }, [ rows ])
    }

}

export const PreviewModal = {

    name: "PreviewModal",

    props: {
        file: {
            default() {
                return {
                    basename: "N/A"
                };
            }
        }
    },

    data() {
        return {
            bufferedUrl : null,
            bufferedText: null
        }
    },

    render() {

        let previewView = this.buildPreviewView();
        return h(Modal, { ref: "modal" }, () => [
            h('div', { class: "flex flex-col" }, [ 
                h('div', {
                    class: "flex flex-row justify-between items-center px-6 py-5 text-gray-600"
                }, [
                    h('div', {}, this.file.basename),
                    h('div', {
                        class: "w-6 h-6 flex flex-col items-center justify-center text-gray-400 hover:text-gray-600"
                    }, [h("i", {
                        class: "fas fa-times text-xl ",
                        onclick: this.hide
                    })])
                ]),
                h('div', {
                    class: 'flex flex-col w-full max-h-[600px] overflow-auto'
                }, [ previewView ]),
            ])
        ])

    },

    mounted() {
    },

    methods: {

        hide() {
            this.$refs.modal.hide();
        },

        show() {
            if(this.bufferedUrl) {
                URL.revokeObjectURL(this.bufferedUrl);
                this.bufferedUrl = null;
            }
            if(this.bufferedText) {
                this.bufferedText = null;
            }
            this.$refs.modal.show();
        },

        buildPreviewView() {
            if(!this.file) {
                return false;
            }
            let name = this.file.basename;
            let index = name.indexOf(".");
            if(index < 0) {
                return false;
            }
            let subfix = name.substring(index + 1);
            let supported = ["PDF", "Web","Text", "Markdown", "Audio", "Video", "Image"];
            for(const type of supported) {
                if(this["is" + type](subfix)) {
                    return this["build" + type + "View"]();
                }
            }
            return h('div', {
                class: 'flex flex-col w-full h-full items-center justify-center p-8 text-sm text-gray-600 gap-y-8'
            }, [
                h('div', {},  "此内容暂时不支持在线浏览，你可以下载到本地使用。"),
                h(Button, {
                    color: "blue",
                    onclick: this.downloadFile
                }, () => "下载此文件")
            ]);
        },

        isPDF(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return "pdf" === ext.toLowerCase();
        },

        buildPDFView() {
            if(!this.bufferedUrl) {
                this.downloadContent();
            }
            return h('iframe', {
                class: 'w-[60vw] h-[80vh] text-sm max-h-screen',
                src: this.bufferedUrl || '#'
            })
        },

        isWeb(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return ["htm", "html"]
                .indexOf(ext.toLowerCase()) > -1
        },

        buildWebView() {

        },

        isText(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return ["txt", "json", "inf", "xml", "properties", "java", "c", "h", "cpp", "cc", "js"]
                .indexOf(ext.toLowerCase()) > -1
        },
        buildTextView() {
            
            if(!this.bufferedText) {
                this.downloadContentText();
            }
            return h('div', {
                class: 'p-4 pt-8 w-full text-sm max-h-screen'
            }, [
                h('pre', {}, this.bufferedText)
            ])
        },

        isMarkdown(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return ext.toLowerCase() === "md";
        },

        buildMarkdownView() {

        },

        isAudio(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return ["mp3", "wav", "wma", "aac", "m4a", "ogg"]
                .indexOf(ext.toLowerCase()) > -1
        },
        buildAudioView() {
            if(!this.bufferedUrl) {
                this.downloadContent();
            }
            return h('div', {
                class: 'p-4 w-full'
            }, [
                h('audio', {
                    controls: "true",
                    src:this.bufferedUrl || '#' ,
                    class: "w-full"
                })
            ])
        },

        isVideo(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return ["mp4", "mov", "webm", "3pg", "flv", "wmv", "avi"]
                .indexOf(ext.toLowerCase()) > -1
        },
        buildVideoView() {
            if(!this.bufferedUrl) {
                this.downloadContent();
            }
            return h('div', {
                class: 'p-4 w-full'
            }, [
                h('video', {
                    controls: "true",
                    src: this.bufferedUrl || '#'
                })
            ])
        },

        isImage(ext) {
            if(typeof ext !== "string") {
                return false;
            }
            return ["png", "jpg", "webp", "gif", "tiff", "svg", "bmp", "jpeg","ico"]
                .indexOf(ext.toLowerCase()) > -1
        },
        buildImageView() {
            if(!this.bufferedUrl) {
                this.downloadContent();
            }
            return h('img', { 
                class: 'max-h-screen',
                src: this.bufferedUrl || '#'
            })
        },

        downloadContent() {
            let path = "";
            let current = this.file;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            path = path.replaceAll(/[\/+]+/g, "/")
            if(path.endsWith("/")) {
                path = path.substring(0,path.length - 1);
            }
            if(!this.bufferedUrl) {
                let client = Secure.getDAVClient();
                client.getFileContents(path).then(data => {
                    this.bufferedUrl = URL.createObjectURL(new Blob([data],  { type : this.file.mime}));
                });
            }
        },

        downloadContentText() {
            let path = "";
            let current = this.file;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            path = path.replaceAll(/[\/+]+/g, "/")
            if(path.endsWith("/")) {
                path = path.substring(0,path.length - 1);
            }
            if(!this.bufferedUrl) {
                let client = Secure.getDAVClient();
                client.getFileContents(path).then(data => {
                    let decoder = new TextDecoder("utf-8");
                    let view = new Uint8Array(data);
                    this.bufferedText = decoder.decode(view);
                });
            }
        },

        downloadFile() {
            let path = "";
            let current = this.file;
            while(current) {
                path = current.basename + "/" + path
                current = current.__parent;
            }
            path = path.replaceAll(/[\/+]+/g, "/")
            if(path.endsWith("/")) {
                path = path.substring(0,path.length - 1);
            }
            if(!this.bufferedUrl) {
                let client = Secure.getDAVClient();
                client.getFileContents(path).then(data => {
                    this.bufferedUrl = URL.createObjectURL(new Blob([data],  { type : this.file.mime}));
                    let link = document.createElement("a");
                    link.setAttribute("download", this.file.basename);
                    link.setAttribute("href", this.bufferedUrl);
                    document.body.appendChild(link);
                    link.click();
                    link.remove();
                });
            } else {
                let link = document.createElement("a", {
                    href: this.bufferedUrl,
                    download: true
                });
                document.body.appendChild(link);
                link.click();
                link.remove();
            }
        }
    }

}

