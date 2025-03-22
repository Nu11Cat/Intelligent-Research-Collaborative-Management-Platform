import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import MainLayout from '../layout/MainLayout.vue'
import Attendance from '../views/Attendance.vue'
import Recruitment from '../views/Recruitment.vue'
import PreRegister from '../views/PreRegister.vue'
import Admin from '../views/Admin.vue'
import GroupManagement from '../views/GroupManagement.vue'
import PreRegisteredUserManagement from '../views/PreRegisteredUserManagement.vue'
import Console from '../views/Console.vue'
import About from '../views/About.vue'
import TeamRecruitment from '../views/TeamRecruitment.vue'
import LeaveManagement from '../views/LeaveManagement.vue'
import ResourceSharing from '../views/ResourceSharing.vue'
import HistoryAnnouncement from '../views/HistoryAnnouncement.vue'
import Feedback from '../views/Feedback.vue'
import UpdateLog from '../views/UpdateLog.vue'
import UserAgreement from '../views/UserAgreement.vue'
import PrivacyPolicy from '../views/PrivacyPolicy.vue'

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: Login
    },
    {
      path: '/register',
      name: 'register',
      component: Register
    },
    {
      path: '/user-agreement',
      name: 'UserAgreement',
      component: UserAgreement
    },
    {
      path: '/privacy-policy',
      name: 'PrivacyPolicy',
      component: PrivacyPolicy
    },
    {
      path: '/',
      component: MainLayout,
      redirect: '/console',
      children: [
        {
          path: 'attendance',
          name: 'attendance',
          component: Attendance
        },
        {
          path: 'equipment',
          name: 'EquipmentManagement',
          component: () => import('../views/EquipmentManagement.vue'),
          meta: {
            title: '器材管理',
            requiresAuth: true
          }
        },
        {
          path: 'recruitment',
          name: 'recruitment',
          component: Recruitment
        },
        {
          path: 'group',
          name: 'Group',
          component: () => import('../views/Group.vue'),
          meta: {
            title: '小组管理',
            description: ''
          }
        },
        {
          path: 'pre-register',
          name: 'pre-register',
          component: PreRegister
        },
        {
          path: 'console',
          name: 'console',
          component: Console
        },
        {
          path: 'admin',
          name: 'admin',
          component: Admin
        },
        {
          path: 'group-management',
          name: 'group-management',
          component: GroupManagement
        },
        {
          path: 'pre-registered-user-management',
          name: 'pre-registered-user-management',
          component: PreRegisteredUserManagement
        },
        {
          path: 'about',
          name: 'about',
          component: About
        },
        {
          path: 'team-recruitment',
          name: 'TeamRecruitment',
          component: TeamRecruitment,
          meta: {
            title: '招募信息管理'
          }
        },
        {
          path: 'statistics',
          name: 'Statistics',
          component: () => import('../views/Statistics.vue'),
          meta: {
            title: '数据统计',
            requiresAuth: true
          }
        },
        {
          path: 'leave-management',
          name: 'LeaveManagement',
          component: LeaveManagement,
          meta: {
            title: '请假管理',
            requiresAuth: true,
            roles: ['LEADER']
          }
        },
        {
          path: 'resource-sharing',
          name: 'ResourceSharing',
          component: ResourceSharing,
          meta: {
            title: '资源共享',
            requiresAuth: true
          }
        },
        {
          path: 'history-announcement',
          name: 'HistoryAnnouncement',
          component: HistoryAnnouncement,
          meta: {
            title: '历史公告',
            requiresAuth: true
          }
        },
        {
          path: 'feedback',
          name: 'Feedback',
          component: Feedback,
          meta: {
            title: '我要反馈',
            requiresAuth: true
          }
        },
        {
          path: 'update-log',
          name: 'UpdateLog',
          component: UpdateLog,
          meta: {
            title: '关于更新'
          }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  // 不需要登录的路由
  if (to.path === '/login' || to.path === '/register' || 
      to.path === '/user-agreement' || to.path === '/privacy-policy') {
    next()
    return
  }

  // 检查是否登录
  if (!token) {
    next('/login')
    return
  }

  // 所有用户都可以访问"关于系统"和"关于更新"页面
  if (to.path === '/about' || to.path === '/update-log') {
    next()
    return
  }

  // 根据用户角色检查权限
  if (userRole === 'ADMIN') {
    // 管理员只能访问预注册和管理页面
    if (to.path === '/pre-register' || to.path === '/admin' || to.path === '/group-management' || 
        to.path === '/pre-registered-user-management' || to.path === '/console') {
      next()
    } else {
      next('/console')
    }
  } else if (userRole === 'LEADER') {
    // 组长可以访问考勤、器材借用、发布招募页面、小组管理、数据统计、请假管理、资源共享、历史公告和我要反馈
    if (to.path === '/attendance' || to.path === '/equipment' || to.path === '/recruitment' || 
        to.path === '/team-recruitment' || to.path === '/group' || to.path === '/statistics' ||
        to.path === '/leave-management' || to.path === '/resource-sharing' || 
        to.path === '/history-announcement' || to.path === '/feedback') {
      next()
    } else {
      next('/statistics')
    }
  } else if (userRole === 'USER') {
    // 普通用户可以访问考勤、器材借用、发布招募页面、资源共享、历史公告和我要反馈
    if (to.path === '/attendance' || to.path === '/equipment' || to.path === '/recruitment' || 
        to.path === '/team-recruitment' || to.path === '/resource-sharing' || 
        to.path === '/history-announcement' || to.path === '/feedback') {
      next()
    } else {
      next('/attendance')
    }
  } else {
    // 未知角色，重定向到登录页
    next('/login')
  }
})

export default router 