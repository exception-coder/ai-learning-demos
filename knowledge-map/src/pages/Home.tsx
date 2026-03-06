import { Link } from 'react-router-dom'
import { motion } from 'framer-motion'

const cards = [
  {
    title: '技术栈全景图',
    desc: '围绕「工作助手」和「岗位需求分析」两个真实项目，展示五层技术栈调用链路',
    to: '/tech-stack',
    color: 'from-blue-600 to-cyan-500',
    layers: ['工作助手', '岗位需求分析', 'RAG', 'Agent', 'Spring AI'],
  },
]

function Home() {
  return (
    <div className="h-full flex flex-col items-center justify-center p-10">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="text-center mb-12"
      >
        <h1 className="text-4xl font-bold mb-3">
          <span className="text-blue-400">AI 应用开发</span> 知识图谱
        </h1>
        <p className="text-gray-400 text-lg">
          交互式可视化，让技术栈关系一目了然
        </p>
      </motion.div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 max-w-5xl">
        {cards.map((card, i) => (
          <motion.div
            key={card.to}
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.4, delay: i * 0.1 }}
          >
            <Link
              to={card.to}
              className="block group rounded-2xl border border-gray-800 bg-gray-900 p-6 hover:border-gray-600 transition-all hover:shadow-lg hover:shadow-blue-500/5"
            >
              <div
                className={`h-2 w-16 rounded-full bg-gradient-to-r ${card.color} mb-4`}
              />
              <h3 className="text-lg font-semibold mb-2 group-hover:text-blue-400 transition-colors">
                {card.title}
              </h3>
              <p className="text-sm text-gray-400 mb-4">{card.desc}</p>
              <div className="flex flex-wrap gap-1.5">
                {card.layers.map((tag) => (
                  <span
                    key={tag}
                    className="px-2 py-0.5 text-xs rounded-full bg-gray-800 text-gray-400"
                  >
                    {tag}
                  </span>
                ))}
              </div>
            </Link>
          </motion.div>
        ))}

        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.4, delay: 0.2 }}
          className="rounded-2xl border border-dashed border-gray-700 p-6 flex items-center justify-center"
        >
          <p className="text-gray-600 text-sm text-center">
            更多知识图谱<br />持续添加中...
          </p>
        </motion.div>
      </div>
    </div>
  )
}

export default Home
