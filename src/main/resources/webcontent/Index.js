import { h } from "./modules/vue.esm.js";
import { RouterView } from "./modules/vue-router.js";
export default {

    name: "Index",

    render() {
        return h('div', {
            class: "w-screen h-screen bg-slate-100 overflow-hidden"
        }, [ h(RouterView, {}) ])
    },

    methods: {

    }

}