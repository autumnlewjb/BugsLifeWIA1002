import VueRouter from 'vue-router'

import Login from '../views/Login'
import Register from '../views/Register'
import Projects from '../views/Projects'
import Project from '../views/Project'
import Issue from '../views/Issue'
import Search from '../views/Search'
import Home from '../views/Home'
import Profile from '../views/Profile'
import PageNotFound from '../views/404'

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
        component: Register,
        meta: {
            requiresAuth: true,
            requiresAdmin: true
        }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: Profile,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/projects',
        name: 'Projects',
        component: Projects,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/project',
        name: 'Project',
        component: Project,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/issue',
        name: 'Issue',
        component: Issue,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '/search',
        name: 'Search',
        component: Search,
        meta: {
            requiresAuth: true
        }
    },
    {
        path: '*',
        name: 'PageNotFound',
        component: PageNotFound
    }
]

const router = new VueRouter({routes: routes, mode: 'history'})

router.beforeEach((to, from, next) => {
    if (to.matched.some(route => route.meta.requiresAuth)) {
        store.dispatch('fetchCurrentUser').then(() => {
            if (store.getters.getCurrentUser) {
                if (to.matched.some(route => route.meta.requiresAdmin)) {
                    if (store.getters.getCurrentUser.roles.find(role => role.name == 'ADMIN')) {
                        next();
                    } else {
                        next({name: 'Profile'});
                    }
                } else {
                    next();
                }
            } else {
                next({name: 'Login'});
            }
        });
    } else {
        if (store.getters.getCurrentUser) {
            if (to.name == 'Login' || to.name == 'Home') {
                next({name: 'Profile'});
            } else {
                next();
            }
        } else {
            next();
        }
    }
})

export default router;