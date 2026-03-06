import { motion, AnimatePresence } from 'framer-motion'
import type { TechNodeData } from '../data/tech-stack-nodes'

interface DetailPanelProps {
  node: { id: string; data: TechNodeData } | null
  onClose: () => void
}

const LAYER_META: Record<string, { label: string; color: string }> = {
  model:         { label: '模型层', color: '#ef4444' },
  framework:     { label: '框架层', color: '#f97316' },
  capability:    { label: '能力层', color: '#3b82f6' },
  orchestration: { label: '编排层', color: '#8b5cf6' },
  application:   { label: '应用层', color: '#10b981' },
}

const PROJECT_META: Record<string, { label: string; color: string }> = {
  work: { label: '工作助手', color: '#10b981' },
  job:  { label: '岗位需求分析', color: '#f59e0b' },
  both: { label: '两个项目共用', color: '#6366f1' },
}

function DetailPanel({ node, onClose }: DetailPanelProps) {
  return (
    <AnimatePresence>
      {node && (
        <motion.div
          key={node.id}
          initial={{ x: 320, opacity: 0 }}
          animate={{ x: 0, opacity: 1 }}
          exit={{ x: 320, opacity: 0 }}
          transition={{ type: 'spring', damping: 25, stiffness: 300 }}
          className="absolute right-0 top-0 h-full w-80 bg-gray-900 border-l border-gray-800 shadow-2xl z-50 flex flex-col"
        >
          <div className="p-5 border-b border-gray-800 flex items-start justify-between">
            <div>
              <span
                className="inline-block px-2 py-0.5 rounded text-xs font-medium mb-2"
                style={{
                  backgroundColor: LAYER_META[node.data.layer]?.color + '20',
                  color: LAYER_META[node.data.layer]?.color,
                }}
              >
                {LAYER_META[node.data.layer]?.label}
              </span>
              <h2 className="text-lg font-bold">{node.data.label}</h2>
              {node.data.project && PROJECT_META[node.data.project] && (
                <span
                  className="inline-block px-2 py-0.5 rounded text-xs font-medium mt-1"
                  style={{
                    backgroundColor: PROJECT_META[node.data.project]?.color + '20',
                    color: PROJECT_META[node.data.project]?.color,
                  }}
                >
                  {PROJECT_META[node.data.project]?.label}
                </span>
              )}
            </div>
            <button
              onClick={onClose}
              className="text-gray-500 hover:text-white transition-colors text-xl leading-none"
            >
              ×
            </button>
          </div>
          <div className="p-5 flex-1 overflow-auto space-y-5">
            <section>
              <h3 className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-2">简介</h3>
              <p className="text-gray-300 text-sm leading-relaxed">
                {node.data.description}
              </p>
            </section>

            {node.data.implementation && (
              <section>
                <h3 className="text-xs font-semibold text-blue-400 uppercase tracking-wider mb-2">实现思路</h3>
                <pre className="text-gray-300 text-sm leading-relaxed whitespace-pre-wrap font-sans">
                  {node.data.implementation}
                </pre>
              </section>
            )}

            {node.data.bestPractice && (
              <section>
                <h3 className="text-xs font-semibold text-emerald-400 uppercase tracking-wider mb-2">最佳实践</h3>
                <pre className="text-gray-300 text-sm leading-relaxed whitespace-pre-wrap font-sans">
                  {node.data.bestPractice}
                </pre>
              </section>
            )}
          </div>
        </motion.div>
      )}
    </AnimatePresence>
  )
}

export default DetailPanel
