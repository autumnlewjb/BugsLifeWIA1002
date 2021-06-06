import VueRouter from 'vue-router'

import Login from '../views/Login'
import Register from '../views/Register'
import Projects from '../views/Projects'
import Project from '../views/Project'
import Issue from '../views/Issue'
import Search from '../views/Search'

import Vue from 'vue'

import store from '../store/index.js'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
    },
    {
        path: '/login',
        name: 'Login',
        component: Login
    },
    {
        path: '/register',
        name: 'Register',
        component: Register
    },
    {
        path: '/projects',
        name: 'Projects',
        component: Projects
    },
    {
        path: '/project',
        name: 'Project',
        component: Project
    },
    {
        path: '/issue',
        name: 'Issue',
        component: Issue
    },
    {
        path: '/search',
        name: 'Search',
        component: Search
    }
]

const router = new VueRouter({routes: routes, mode: 'history'})

router.beforeEach((to, from, next) => {
    if (to.name == 'Register') {
        if (!store.getters.getCurrentUser) {
            next();
        } else {
            next({name: "Projects"});
        }
    } else if (to.name == 'Home' && store.getters.getCurrentUser) {
        next({name: 'Projects'})
    } else if (to.name != 'Login' && !store.getters.getCurrentUser) {
        next({name: "Login"});
    } else if (to.name == 'Login' && store.getters.getCurrentUser) {
        next({name: 'Projects'});
    } else {
        next();
    }
})

export default router;