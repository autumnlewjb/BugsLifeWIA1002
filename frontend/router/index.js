import {createRouter, createWebHistory} from 'vue-router'

import Login from '../views/Login'
import Register from '../views/Register'
import Projects from '../views/Projects'
import Project from '../views/Project'
import Issues from '../views/Issues'
import Issue from '../views/Issue'

const routes = [
    {
        path: '/',
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
    }
]

const router = createRouter({history: createWebHistory(process.env.BASE_URL), routes})

export default router;