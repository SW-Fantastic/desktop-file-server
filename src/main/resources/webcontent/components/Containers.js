import { h,Teleport, watch,Comment } from "../modules/vue.esm.js";
import { Button } from "./Input.js";

export const Card = {

    name: "Card", 

    render() {

        const slots = this.$slots;
        let defaultSlot = slots.default ? slots.default() : [];

        return h('div', {
            class: "p-4 rounded-md border border-1 border-slate-200 bg-white shadow-md"
        }, defaultSlot )
    }

}


export const ToolBar = {

    name: "ToolBar", 

    emits: ["itemClick"],

    props: {
        items: {
            default(){
                return []
            }
        }
    },

    render() {
        let icons = [];
        for(const item of this.items) {
            icons.push(this.button(item.icon, item.key))
        }
        return h(Card, {}, () => [
            h('div', {
                class:"flex flex-row gap-x-2"
            }, icons)
        ])
    },

    methods: {
        button(icon, key) {
            const that = this;
            return h(Button, {
                type: 'icon',
                class: "w-8 h-8",
                onclick(e) {
                    that.$emit("itemClick", key)
                }
            }, () => [
                h('i', {
                    class: 'fas fa-' + icon
                })
            ])
        }
    }

}

export const Modal = {

    name: "Modal",

    emits: ["update:modelValue"],

    props: {
        modelValue: {
            default: false
        }
    },

    watch: {
        modelValue(val) {
            this.visible = val;
        }
    },

    data() {
        return {
            visible: this.modelValue
        }
    },

    render() {
        
        const slots = this.$slots;
        let defaultSlot = slots.default ? slots.default() : [];

        return this.visible ? h('div', {
            class: "fixed bg-slate-800/50 w-screen h-screen flex flex-col justify-center items-center z-[20]",
            onclick: this.hide
        }, [ 
            h('div', {
                class: "flex flex-col min-w-[500px] max-w-[60%] max-h-[80%] overflow-y-hidden bg-white rounded-md",
                onclick(e) {
                    e.stopPropagation();
                }
            }, defaultSlot)
        ]) : h(Comment, {}, "v-if")

    }, 

    methods: {
        show() {
            this.visible = true;
            this.$emit("update:modelValue", this.visible)
        },
        hide(){
            this.visible = false;
            this.$emit("update:modelValue", this.visible)
        }
    }

}

export const TreeNode = {

    name: "TreeNode", 

    emits: ["changed", "selected"],

    props: {
        labelProp: {
            default: "name"
        },
        childrenProp: {
            default: "children"
        },
        node: {
            default() {
                return {
                    name: "Node",
                    children: []
                }
            }
        },
        root: {
            default: false
        },
        active: {
            default: null
        }
    },

    data() {
        return {
            expand: false
        }
    },

    render() {

        const that = this;
        let children = [];
        let nodeChild = this.node[this.childrenProp];
        if(nodeChild && nodeChild.length > 0) {
            for(const it of nodeChild) {
                it.__parent = this.node;
                children.push(h(TreeNode, {
                    labelProp: this.labelProp,
                    childrenProp: this.childrenProp,
                    node: it,
                    active: this.active,
                    onChanged(item) {
                        that.$emit("changed", item)
                    },
                    onSelected(item) {
                        that.$emit('selected', item)
                    }
                }))
            }
        }

        if(children.length === 0) {
            this.expand = false;
        }

        let content = null;
        if(!this.root) {
            let stateClasses = " bg-white hover:bg-blue-100 "
            if(this.active === this.node) {
                stateClasses = stateClasses + " text-blue-500 font-semibold "
            }
            content = [ 
                h('div', {
                    class: "px-2 py-1 flex flex-row items-center justify-start rounded-md " + stateClasses,
                    onclick: this.onSelect
                }, [ 
                    h('i', {
                        class: "w-4 h-4 fas " + (this.expand ? "fa-caret-down" : "fa-caret-right"),
                        onclick: this.onExpand
                    }),
                    h('div', {
                        class: "text-sm",
                    }, this.node[this.labelProp])
                ]),
                h('div', {
                    class: "flex flex-col ml-4"
                }, this.expand ? children : [])
            ]
        } else {
            content = children
        }

        return h('div', {
            class: "flex flex-col cursor-default select-none",
        }, content)
    },

    methods: {

        onExpand(e) {
            if(this.root) {
                return
            }
            this.expand = !this.expand;
            this.$emit("changed", {
                node: this.node,
                state: this.expand
            })
        },

        onSelect(e) {
            this.$emit('selected', {
                node: this.node,
                state: this.expand
            })
        }

    }

}