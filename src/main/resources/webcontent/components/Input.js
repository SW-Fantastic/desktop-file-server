import { h, onBeforeUnmount } from "../modules/vue.esm.js"

/**
 * 基本的Vue按钮组件
 */
export const Button = {

    name: "Button",

    props: {
        color: {
            default : "default"
        },
        type: {
            default: "default"
        }
    },

    render() {
        const ColorSchema = {
            default: `border border-1 border-gray-200 bg-white 
                      rounded text-gray-500 hover:bg-gray-100 cursor-default
                      flex flex-col items-center justify-center
                    `,
            blue: `border border-1 border-blue-200 bg-blue-500 
                      rounded text-white hover:bg-blue-600 cursor-default
                      flex flex-col items-center justify-center
            `,
            red: `border border-1 border-red-200 bg-red-500 
                      rounded text-white hover:bg-red-600 cursor-default
                      flex flex-col items-center justify-center
            `,
        }

        const TypeSchema = {
            default: `px-4 py-2`,
            icon: `p-2`
        }

        const slots = this.$slots;
        let defaultSlot = slots.default ? slots.default() : [ h('div', null, "Button") ];
        
        return h('div', {
            class: ColorSchema[this.color] + TypeSchema[this.type]
        },[ h('div', null, defaultSlot) ])
        
    }

}

/**
 * 基本的Vue文本框组件
 */
export const TextInput = {

    name: "TextInput", 

    emits: ["update:modelValue"],

    props: {
        placeholder: {
            default: "Input text"
        },
        type: {
            default: "text"
        },
        englishMode: {
            default: false
        },
        modelValue: {
            default: ""
        }
    },

    watch: {
        modelValue(v) {
            this.value = v;
        }
    },

    data() {
        return {
            value: this.modelValue
        }
    },

    render() {

        return h('div', {
            class: "border border-1 border-gray-200 rounded px-4 py-2"
        }, [h('input', {
            class: "w-full h-full bg-transparent outline-0",
            type: this.type,
            placeholder: this.placeholder,
            value: this.value,
            oninput: this.onTextChanged
        })])

    },

    methods: {

        onTextChanged(e) {
            if(this.englishMode) {
                e.target.value = e.target.value.replaceAll(/[^\w./]/ig, '')
            }
            this.value = e.target.value;
            this.$emit("update:modelValue", this.value); 
        }

    }

}

export const Dropdown = {

    name: "Dropdown",

    props: {
        modelValue: {
            default: false
        },
        height: {
            default: 120
        },
        placeholder: {
            default: '请选择...'
        }
    },

    data() {
        return {
            pos: {
                top: 0,
                left: 0,
                width: 0
            },
            value: null,
            visible: false
        }
    },

    computed: {
        dropdownPos() {
            return {
                top: this.pos.top + 'px',
                left: this.pos.left + 'px',
                width: this.pos.width + 'px',
                maxHeight: this.height + 'px'
            }
        }
    },

    render() {

        let contentElem = null;
        let content = this.$slots.default;
        if(content) {
            contentElem = content();
        } else {
            contentElem = h('div',{}, this.placeholder)
        }

        let dpContent = this.$slots.content;
        let dpElement = [];
        if(dpContent) {
            dpElement = dpContent();
        }

        return h('div', {
            ref: 'content',
            class: 'w-full px-4 py-2 border border-gray-200 border-1 rounded-md text-gray-400',
            onclick: this.trigger
        }, [
            contentElem,
            h('div', {
                style: this.dropdownPos,
                class: 'fixed border shadow-lg border-gray-200 border-1 bg-white rounded-md py-2 ' + (this.visible ? 'visible' : 'invisible')
            }, dpElement)
        ])
    },

    mounted() {
        
        window.addEventListener('resize', this.reposition);
        window.addEventListener('scroll', this.reposition, true);
        window.addEventListener('click', this.hide);
    
    },

    onBeforeUnmount() {
      
        window.removeEventListener("resize", this.reposition);
        window.removeEventListener('scroll', this.reposition, true);
        window.removeEventListener('click', this.hide)

    },

    methods: {
        reposition() {
            let rect = this.$refs.content.getBoundingClientRect();
            this.pos.top = rect.top + rect.height + 4;
            this.pos.left = rect.left;
            this.pos.width = rect.width;
        },
        hide() {
            this.visible = false;
        },
        trigger(e) {
            e.stopPropagation();
            this.reposition();
            this.visible = !this.visible;
        }
    }

} 