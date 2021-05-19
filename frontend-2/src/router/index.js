import VueRouter from 'vue-router'

import Login from '../views/Login'
import Register from '../views/Register'
import Projects from '../views/Projects'
import Project from '../views/Project'
import Issues from '../views/Issues'
import Issue from '../views/Issue'

import Vue from 'vue'

Vue.use(VueRouter)

const routes = [
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
        path: '/issues',
        name: 'Issues',
        component: Issues
    },
    {
        path: '/issue',
        name: 'Issue',
        component: Issue
    },
]

const router = new VueRouter({routes: routes, mode: 'history'})

export default router;