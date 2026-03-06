import { useCallback, useState } from 'react'
import {
  ReactFlow,
  Background,
  Controls,
  MiniMap,
  useNodesState,
  useEdgesState,
  type NodeMouseHandler,
  type Node,
} from '@xyflow/react'
import '@xyflow/react/dist/style.css'
import { initialNodes, initialEdges, type TechNodeData } from '../data/tech-stack-nodes'
import DetailPanel from '../components/DetailPanel'

const LAYER_MINIMAP_COLORS: Record<string, string> = {
  model: '#ef4444',
  framework: '#f97316',
  capability: '#3b82f6',
  orchestration: '#8b5cf6',
  application: '#10b981',
}

function TechStackMap() {
  const [nodes, , onNodesChange] = useNodesState(initialNodes)
  const [edges, , onEdgesChange] = useEdgesState(initialEdges)
  const [selectedNode, setSelectedNode] = useState<{ id: string; data: TechNodeData } | null>(null)

  const onNodeClick: NodeMouseHandler = useCallback((_event, node: Node) => {
    if (node.id.startsWith('layer-')) return
    const data = node.data as TechNodeData
    if (data.layer) {
      setSelectedNode({ id: node.id, data })
    }
  }, [])

  const minimapColor = useCallback((node: Node) => {
    const data = node.data as TechNodeData
    return LAYER_MINIMAP_COLORS[data?.layer] ?? '#334155'
  }, [])

  return (
    <div className="relative h-full w-full">
      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onNodeClick={onNodeClick}
        onPaneClick={() => setSelectedNode(null)}
        fitView
        fitViewOptions={{ padding: 0.15 }}
        minZoom={0.3}
        maxZoom={2}
        proOptions={{ hideAttribution: true }}
      >
        <Background color="#1e293b" gap={20} size={1} />
        <Controls position="bottom-left" />
        <MiniMap
          nodeColor={minimapColor}
          nodeStrokeWidth={0}
          maskColor="rgba(0,0,0,0.6)"
          style={{ background: '#0f172a', borderColor: '#1e293b', borderRadius: 8 }}
          position="bottom-right"
        />
      </ReactFlow>

      <div className="absolute top-4 left-4 bg-gray-900/90 backdrop-blur px-4 py-3 rounded-xl border border-gray-800 max-w-[220px]">
        <h2 className="text-sm font-bold text-white mb-2">AI 应用开发技术栈全景图</h2>
        <div className="space-y-1 mb-3">
          {Object.entries(LAYER_MINIMAP_COLORS).reverse().map(([key, color]) => (
            <div key={key} className="flex items-center gap-2 text-xs text-gray-400">
              <span className="w-2.5 h-2.5 rounded-full shrink-0" style={{ backgroundColor: color }} />
              {{
                application: '应用层 — 我的项目',
                orchestration: '编排层 — 让 AI 会思考',
                capability: '能力层 — 让 AI 能做事',
                framework: '框架层 — 开发工具链',
                model: '模型层 — AI 的大脑',
              }[key]}
            </div>
          ))}
        </div>
        <div className="border-t border-gray-700 pt-2 space-y-1">
          <p className="text-[10px] text-gray-500 mb-1">连线颜色 = 项目归属</p>
          <div className="flex items-center gap-2 text-xs text-gray-400">
            <span className="w-5 h-0.5 shrink-0" style={{ backgroundColor: '#10b981' }} />
            工作助手
          </div>
          <div className="flex items-center gap-2 text-xs text-gray-400">
            <span className="w-5 h-0.5 shrink-0" style={{ backgroundColor: '#f59e0b' }} />
            岗位需求分析
          </div>
          <div className="flex items-center gap-2 text-xs text-gray-400">
            <span className="w-5 h-0.5 shrink-0" style={{ backgroundColor: '#6366f1' }} />
            共用
          </div>
        </div>
      </div>

      <DetailPanel node={selectedNode} onClose={() => setSelectedNode(null)} />
    </div>
  )
}

export default TechStackMap
