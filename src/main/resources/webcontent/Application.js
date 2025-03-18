import { createApp, h } from "./modules/vue.esm.js";
import { createRouter, createWebHashHistory } from "./modules/vue-router.js"
import Index from "./Index.js";

import { Secure } from "./service/Secure.js";

const router = createRouter({
    history: createWebHashHistory(),
    routes:[{
        path: "/login",
        component:() => import("./pages/Login.js")
    }, {
        path: "/register",
        component:() => import("./pages/Register.js")
    }, {
        path: "/",
        component:() => import("./pages/Index.js")
    }]
})

router.beforeEach((to,from,next) => {

    if(to.path === "/login" || to.path === '/register') {
        if(Secure.hasLogined()) {
            next({ path : "/"})
            return;
        } else {
            next();
        }
    } else {
        if(!Secure.hasLogined()) {
            next({ path: "/login" })
            return;
        }
        next()
    }

})

const App = createApp(Index);
App.use(router)
App.mount("#app");