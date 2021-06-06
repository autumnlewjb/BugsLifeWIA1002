import VueRouter from 'vue-router'

import Login from '../views/Login'
import Register from '../views/Register'
import Projects from '../views/Projects'
import Project from '../views/Project'
import Issue from '../views/Issue'
import Search from '../views/Search'
import Home from '../views/Home'
import Profile from '../views/Profile'

import Vue from 'vue'

import store from '../store/index.js'

Vue.use(VueRouter)

const routes = [
    {
        path: '/',
        name: 'Home',
        component: Home
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
        path: '/profile',
        name: 'Profile',
        component: Profile
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
    if (store.getters.getCurrentUser) {
        if (to.name == 'Login' || to.name == 'Home') {
            next({name: 'Profile'});
        } else if (to.name == 'Register' && store.getters.getCurrentUser.roles.find((role) => role.name == 'ADMIN') == null) {
            next({name: 'Profile'});
        } else {
            next();
        }
    } else {
        if (to.name == 'Register') {
            next({name: 'Login'});
        } else if (to.name == 'Login') {
            next();
        } else if (to.name == 'Home') {
            next();
        } else {
            next({name: 'Login'});
        }
    }
})

export default router;