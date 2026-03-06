import { Outlet, NavLink } from 'react-router-dom'

const navItems = [
  { to: '/', label: '首页' },
  { to: '/tech-stack', label: '技术栈全景图' },
]

function Layout() {
  return (
    <div className="flex h-screen bg-gray-950 text-white">
      <aside className="w-56 shrink-0 border-r border-gray-800 bg-gray-900 flex flex-col">
        <div className="p-5 border-b border-gray-800">
          <h1 className="text-lg font-bold tracking-tight">
            <span className="text-blue-400">AI</span> 知识图谱
          </h1>
          <p className="text-xs text-gray-500 mt-1">应用开发学习可视化</p>
        </div>
        <nav className="flex-1 p-3 space-y-1">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              className={({ isActive }) =>
                `block px-3 py-2 rounded-lg text-sm transition-colors ${
                  isActive
                    ? 'bg-blue-600/20 text-blue-400 font-medium'
                    : 'text-gray-400 hover:text-white hover:bg-gray-800'
                }`
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="p-4 border-t border-gray-800 text-xs text-gray-600">
          ai-learning-demos
        </div>
      </aside>
      <main className="flex-1 overflow-hidden">
        <Outlet />
      </main>
    </div>
  )
}

export default Layout
