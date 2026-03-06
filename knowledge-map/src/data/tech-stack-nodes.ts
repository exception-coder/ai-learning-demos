import type { Node, Edge } from '@xyflow/react'
import { descriptions } from './tech-stack-descriptions'

export interface TechNodeData {
  label: string
  description: string
  implementation?: string
  bestPractice?: string
  layer: 'model' | 'framework' | 'capability' | 'orchestration' | 'application'
  project?: 'work' | 'job' | 'both'
  [key: string]: unknown
}

const LAYER_COLORS: Record<string, { bg: string; border: string; text: string }> = {
  model:         { bg: '#fef2f2', border: '#ef4444', text: '#dc2626' },
  framework:     { bg: '#fff7ed', border: '#f97316', text: '#ea580c' },
  capability:    { bg: '#eff6ff', border: '#3b82f6', text: '#2563eb' },
  orchestration: { bg: '#f5f3ff', border: '#8b5cf6', text: '#7c3aed' },
  application:   { bg: '#ecfdf5', border: '#10b981', text: '#059669' },
}

const LAYER_LABELS: Record<string, string> = {
  model: '模型层 — AI 的"大脑"',
  framework: '框架层 — 开发工具链',
  capability: '能力层 — 让 AI 能"做事"',
  orchestration: '编排层 — 让 AI 能"思考"',
  application: '应用层 — 我的项目',
}

function makeStyle(layer: string) {
  const c = LAYER_COLORS[layer]
  return {
    background: c.bg,
    border: `2px solid ${c.border}`,
    borderRadius: 12,
    padding: '12px 18px',
    fontSize: 13,
    color: c.text,
    fontWeight: 600,
    minWidth: 140,
    textAlign: 'center' as const,
  }
}

function makeLabelStyle(layer: string) {
  const c = LAYER_COLORS[layer]
  return {
    background: c.border,
    color: '#fff',
    borderRadius: 8,
    padding: '8px 20px',
    fontSize: 14,
    fontWeight: 700,
    minWidth: 200,
    textAlign: 'center' as const,
  }
}

// ─── Layer label nodes (left column) ─────────────────────────
const layerLabels: Node[] = [
  { id: 'layer-app',   position: { x: -280, y: 0 },   data: { label: LAYER_LABELS.application },   style: makeLabelStyle('application'),   draggable: true },
  { id: 'layer-orch',  position: { x: -280, y: 180 },  data: { label: LAYER_LABELS.orchestration }, style: makeLabelStyle('orchestration'), draggable: true },
  { id: 'layer-cap',   position: { x: -280, y: 380 },  data: { label: LAYER_LABELS.capability },    style: makeLabelStyle('capability'),    draggable: true },
  { id: 'layer-fw',    position: { x: -280, y: 580 },  data: { label: LAYER_LABELS.framework },     style: makeLabelStyle('framework'),     draggable: true },
  { id: 'layer-model', position: { x: -280, y: 740 },  data: { label: LAYER_LABELS.model },         style: makeLabelStyle('model'),         draggable: true },
]

// ═══════════════════════════════════════════════════════════════
//  应用层 — 两个真实项目
// ═══════════════════════════════════════════════════════════════
const appNodes: Node<TechNodeData>[] = [
  {
    id: 'app-work',
    position: { x: 100, y: 0 },
    data: {
      label: '工作助手',
      description: descriptions['app-work'],
      implementation: '1. 前端：聊天式对话界面，支持文本输入和日志上传\n2. 后端：Spring Boot + Spring AI，ReactAgent 处理对话\n3. 存储：日志文本→Embedding→存入向量数据库\n4. 查询：用户提问→RAG 检索相关日志→LLM 生成回答\n5. 记忆：对话历史 + 用户偏好持久化到数据库',
      bestPractice: '• 日志录入时立即向量化，不要批量处理\n• 敏感信息（密码）加密存储，RAG 检索后解密返回\n• 对话历史保留最近 N 轮，避免 Token 超限\n• 定期对旧日志做摘要压缩，减少向量存储成本',
      layer: 'application',
      project: 'work',
    },
    style: { ...makeStyle('application'), minWidth: 200 },
  },
  {
    id: 'app-job',
    position: { x: 500, y: 0 },
    data: {
      label: '岗位需求分析',
      description: descriptions['app-job'],
      implementation: '1. 数据采集：爬取/API对接招聘平台数据\n2. JD 解析：LLM 结构化提取岗位要求（技能、学历、年限）\n3. 薪资分析：RAG 检索同类岗位→统计中位数/分位数\n4. 匹配评估：输入简历+JD→LLM 逐项对比→输出匹配度评分\n5. 报告生成：结构化数据 + LLM 生成自然语言分析报告',
      bestPractice: '• 招聘数据定期更新，标注数据时效性\n• JD 解析用 JSON Mode 输出结构化结果\n• 匹配评估用 ReflectionAgent 自查遗漏项\n• 薪资数据经 Human in the Loop 人工校验后再输出',
      layer: 'application',
      project: 'job',
    },
    style: { ...makeStyle('application'), minWidth: 200 },
  },
]

// ═══════════════════════════════════════════════════════════════
//  编排层 — 两个项目用到的 Agent 模式
// ═══════════════════════════════════════════════════════════════
const orchNodes: Node<TechNodeData>[] = [
  {
    id: 'orch-react',
    position: { x: 60, y: 180 },
    data: {
      label: 'ReactAgent',
      description: descriptions['orch-react'],
      implementation: '分级实现策略：\n\n【初版：纯 RAG（够用 80% 场景）】\n用户提问 → Embedding → 向量检索 → Top-K 结果拼入 Prompt → LLM 回答\n一步到位，不需要 Agent\n\n【进阶：ReactAgent（复杂多步问题）】\n当用户问题需要多次检索或跨数据源查询时：\n1. Thought：分析问题需要几步\n2. Action：调用检索/计算工具\n3. Observation：拿到中间结果\n4. 重复直到能给出最终回答\n\nSpring AI 实现：ChatClient + tools() 注册工具',
      bestPractice: '• 先做纯 RAG 版本，能解决大部分简单问答\n• 发现有些问题一次检索回答不了，再引入 ReactAgent\n• 不要一上来就上 Agent，过度设计会增加复杂度和延迟\n• Agent 模式下限制最大循环次数（3-5 次），防止死循环\n• 简单问题走 RAG 快速通道，复杂问题才走 Agent 慢通道',
      layer: 'orchestration',
      project: 'work',
    },
    style: makeStyle('orchestration'),
  },
  {
    id: 'orch-memory',
    position: { x: 250, y: 180 },
    data: {
      label: '长期记忆',
      description: descriptions['orch-memory'],
      implementation: '三层记忆架构：\n1. 短期记忆：当前对话历史（内存中的消息列表）\n2. 中期记忆：近期对话摘要（定期压缩存数据库）\n3. 长期记忆：所有工作日志的向量索引（向量数据库）\n\n查询时：短期直接拼接 + 中期按相关性检索 + 长期 RAG 检索',
      bestPractice: '• Spring AI 的 MessageChatMemoryAdvisor 管理短期记忆\n• 超过 Token 限制时自动摘要压缩旧对话\n• 用户画像（偏好、常用系统）单独存储，每次拼入 System Prompt\n• 敏感信息（密码）加密存储，检索时按权限解密',
      layer: 'orchestration',
      project: 'work',
    },
    style: makeStyle('orchestration'),
  },
  {
    id: 'orch-plan',
    position: { x: 440, y: 180 },
    data: {
      label: 'PlanExecuteAgent',
      description: descriptions['orch-plan'],
      implementation: '两阶段执行：\nPlan 阶段：LLM 根据用户输入生成执行计划（JSON 步骤列表）\nExecute 阶段：逐步执行每个步骤，每步调用对应 Tool\n\n示例计划：\n[{step:1, action:"parse_jd", input:"Java高级"},\n {step:2, action:"query_salary", input:"Java高级 P7"},\n {step:3, action:"extract_skills", input:"..."},\n {step:4, action:"generate_report", input:"..."}]',
      bestPractice: '• Plan 用结构化输出（JSON Mode），便于程序解析\n• 每步执行完检查结果，失败则让 LLM 重新规划\n• 复杂任务拆成子任务，每个子任务用 ReactAgent 执行\n• 保存执行进度，支持断点续做',
      layer: 'orchestration',
      project: 'job',
    },
    style: makeStyle('orchestration'),
  },
  {
    id: 'orch-reflect',
    position: { x: 660, y: 180 },
    data: {
      label: 'ReflectionAgent',
      description: descriptions['orch-reflect'],
      implementation: '两个 LLM 角色配合：\n1. Generator：生成匹配分析结果\n2. Reflector：审查分析结果，找出遗漏或错误\n\n流程：Generator 输出 → Reflector 评审 → 有问题则 Generator 修正 → 再评审 → 循环直到 Reflector 通过\n\nSpring AI 实现：两次 ChatClient 调用，不同 System Prompt',
      bestPractice: '• Reflector 的 Prompt 要具体列出检查项（如：是否遗漏学历要求、薪资范围是否合理）\n• 限制反思轮次（2-3 轮），避免无限循环\n• 记录每轮修改内容，可追溯分析质量提升过程\n• 最终结果附带置信度评分',
      layer: 'orchestration',
      project: 'job',
    },
    style: makeStyle('orchestration'),
  },
  {
    id: 'orch-workflow',
    position: { x: 150, y: 280 },
    data: {
      label: 'Workflow',
      description: descriptions['orch-workflow'],
      implementation: '数据入库流水线：\n1. 用户提交工作日志（文本/语音转文字）\n2. 文本预处理：清洗、分段（按段落或固定长度切分）\n3. 调用 Embedding 模型生成向量\n4. 向量 + 原文 + 元数据（日期、标签）存入向量数据库\n5. 可选：LLM 自动生成日志摘要和关键词\n\n工具选项：Spring Batch、n8n 可视化编排',
      bestPractice: '• 文本切分粒度很关键：太大检索不精准，太小丢失上下文\n• 推荐按段落切分 + 滑动窗口重叠 20%\n• 每条记录带上时间戳和来源标签，检索时可按时间过滤\n• 异步处理：日志提交后立即返回，后台异步向量化',
      layer: 'orchestration',
      project: 'work',
    },
    style: makeStyle('orchestration'),
  },
  {
    id: 'orch-human',
    position: { x: 540, y: 280 },
    data: {
      label: 'Human in the Loop',
      description: descriptions['orch-human'],
      implementation: '在 Agent 执行流中插入人工审批节点：\n1. Agent 执行到关键步骤时暂停\n2. 将中间结果推送给人类审核（WebSocket/消息队列）\n3. 人类确认/修改/拒绝\n4. Agent 根据反馈继续执行或重新规划\n\n实现：用状态机管理 Agent 状态（执行中/等待审批/已完成）',
      bestPractice: '• 只在高风险节点设置人工审批，不要每步都审\n• 设置审批超时时间，超时自动提醒或回退\n• 人类反馈记录下来，未来可训练模型减少审批需求\n• 提供"一键批准"和"批量审批"提升效率',
      layer: 'orchestration',
      project: 'job',
    },
    style: makeStyle('orchestration'),
  },
]

// ═══════════════════════════════════════════════════════════════
//  能力层 — 每个技术在两个项目中的具体工作
// ═══════════════════════════════════════════════════════════════
const capNodes: Node<TechNodeData>[] = [
  {
    id: 'cap-rag',
    position: { x: 60, y: 380 },
    data: {
      label: 'RAG',
      description: descriptions['cap-rag'],
      implementation: 'RAG 三步流程：\n1. Indexing（索引）：文档→切分→Embedding→存入向量数据库\n2. Retrieval（检索）：用户问题→Embedding→向量相似度搜索→返回 Top-K 文档\n3. Generation（生成）：检索结果拼入 Prompt→LLM 基于上下文生成回答\n\nSpring AI 实现：VectorStore.similaritySearch() + ChatClient.prompt()',
      bestPractice: '• 切分策略：RecursiveCharacterTextSplitter，chunk_size=500，overlap=100\n• 检索优化：混合检索（向量 + 关键词 BM25）效果最佳\n• 用 Reranker 对检索结果二次排序提升精度\n• Prompt 中明确告诉模型"仅基于提供的上下文回答，不确定就说不知道"\n• 返回引用来源，让用户可追溯',
      layer: 'capability',
      project: 'both',
    },
    style: makeStyle('capability'),
  },
  {
    id: 'cap-fc',
    position: { x: 240, y: 380 },
    data: {
      label: 'FunctionCall',
      description: descriptions['cap-fc'],
      implementation: 'Spring AI @Tool 注解实现：\n1. 定义 Tool 类，用 @Tool 标注方法\n2. 参数用 @ToolParam 描述含义\n3. 注册到 ChatClient：builder.defaultTools(myTool)\n4. 模型自动判断何时调用、传什么参数\n\n流程：用户问题→LLM 决定调用 Tool→框架执行→结果返回 LLM→生成最终回答',
      bestPractice: '• Tool 的 description 是模型选择的唯一依据，必须写清楚\n• 每个 Tool 做一件事，职责单一\n• Tool 返回值要结构化（JSON），方便模型理解\n• 做好 Tool 的异常处理，返回友好错误信息而非堆栈\n• 敏感操作（删除、修改）的 Tool 加确认机制',
      layer: 'capability',
      project: 'both',
    },
    style: makeStyle('capability'),
  },
  {
    id: 'cap-mcp',
    position: { x: 420, y: 380 },
    data: {
      label: 'MCP',
      description: descriptions['cap-mcp'],
      implementation: 'MCP Server 实现（Spring Boot）：\n1. 定义 Resources：暴露文件/数据库数据供 AI 读取\n2. 定义 Tools：暴露可执行函数（查询、写入）\n3. 定义 Prompts：提供可复用的提示词模板\n4. JSON-RPC 2.0 协议通信\n\nMCP Client 接入：连接 Server→列出能力清单→按需调用',
      bestPractice: '• 一个 MCP Server 专注一个领域（如"日志Server"、"招聘Server"）\n• Tool 参数用 JSON Schema 严格定义，避免歧义\n• 做好权限控制：不同角色可访问不同 Tools\n• Server 端做请求限流和审计日志\n• 优先用已有的社区 MCP Server，不要重复造轮子',
      layer: 'capability',
      project: 'both',
    },
    style: makeStyle('capability'),
  },
  {
    id: 'cap-skill',
    position: { x: 600, y: 380 },
    data: {
      label: 'Skill',
      description: descriptions['cap-skill'],
      implementation: 'Skill = 多个 Tool + Prompt 的打包组合：\n\n例如"薪资分析技能"包含：\n• Tool：query_salary_data（查薪资数据库）\n• Tool：calculate_percentile（计算分位数）\n• Prompt：分析模板（输出格式、评估维度）\n\nSpring AI 实现：一个 @Component 类封装相关 @Tool 方法',
      bestPractice: '• 一个 Skill 解决一类问题，不要做"万能 Skill"\n• Skill 内部的 Tool 之间松耦合，可独立使用\n• 对外暴露的 Skill 描述要清晰，Agent 靠描述选择\n• 单元测试覆盖每个 Skill 的核心场景',
      layer: 'capability',
      project: 'both',
    },
    style: makeStyle('capability'),
  },
  {
    id: 'cap-context',
    position: { x: 790, y: 380 },
    data: {
      label: '上下文工程',
      description: descriptions['cap-context'],
      implementation: '管理送给模型的所有信息：\n1. System Prompt：角色设定 + 行为约束\n2. 检索上下文：RAG 返回的相关文档片段\n3. 对话历史：近期对话记录\n4. 工具结果：Tool 执行返回的数据\n\n需要在 Token 限制内合理分配各部分的占比',
      bestPractice: '• Token 分配经验值：System 10% + 检索 40% + 历史 30% + 预留 20%\n• 重要信息放在 Prompt 头部和尾部（注意力分布更集中）\n• 检索结果按相关性排序，最相关的放最靠近问题的位置\n• 用 tiktoken 库预估 Token 数，超限时优先压缩对话历史',
      layer: 'capability',
      project: 'both',
    },
    style: makeStyle('capability'),
  },
]

// ═══════════════════════════════════════════════════════════════
//  框架层
// ═══════════════════════════════════════════════════════════════
const fwNodes: Node<TechNodeData>[] = [
  {
    id: 'fw-springai',
    position: { x: 100, y: 580 },
    data: {
      label: 'Spring AI',
      description: descriptions['fw-springai'],
      implementation: '核心 API：\n• ChatClient：对话入口，支持 prompt/stream/call\n• @Tool + @ToolParam：声明式工具定义\n• VectorStore：向量存储抽象（对接 Qdrant/Milvus/PgVector）\n• EmbeddingModel：文本向量化\n• Advisor：拦截器链（记忆管理、RAG 注入）',
      bestPractice: '• 用 ChatClient.Builder 注入，不要直接 new\n• 用构造函数注入，不要 @Autowired\n• 模型配置放 application.yml，支持多环境切换\n• 用 Advisor 模式统一处理记忆、日志、RAG，不要侵入业务代码',
      layer: 'framework',
      project: 'both',
    },
    style: makeStyle('framework'),
  },
  {
    id: 'fw-vectordb',
    position: { x: 320, y: 580 },
    data: {
      label: '向量数据库',
      description: descriptions['fw-vectordb'],
      implementation: '主流选项：\n• Qdrant：Rust 编写，性能好，推荐入门首选\n• Milvus：分布式架构，适合大规模数据\n• PgVector：PostgreSQL 插件，已有 PG 可直接用\n• Chroma：Python 生态，轻量级\n\nSpring AI 对接：配置 VectorStore Bean，框架自动处理',
      bestPractice: '• 开发阶段用 SimpleVectorStore（内存），生产用 Qdrant/Milvus\n• 向量维度与 Embedding 模型匹配（如 text-embedding-3-small = 1536维）\n• 建立合适的索引类型：HNSW（精度高）或 IVF（速度快）\n• 存储时带上元数据（时间、来源、标签），检索时可按条件过滤',
      layer: 'framework',
      project: 'both',
    },
    style: makeStyle('framework'),
  },
  {
    id: 'fw-java',
    position: { x: 540, y: 580 },
    data: {
      label: 'Java 17',
      description: descriptions['fw-java'],
      implementation: '技术栈组合：\n• Spring Boot 3.2 + Spring AI\n• Spring WebFlux（流式响应 SSE）\n• Spring Data JPA（业务数据持久化）\n• Spring Security（API 鉴权）\n• Maven 多模块管理',
      bestPractice: '• Java 17+ 使用 Record 类定义 DTO\n• 使用 text blocks（\"\"\"）编写 Prompt 模板\n• AI 调用异步化，避免阻塞主线程\n• 用 @Cacheable 缓存重复的 AI 调用结果',
      layer: 'framework',
      project: 'both',
    },
    style: makeStyle('framework'),
  },
  {
    id: 'fw-react',
    position: { x: 720, y: 580 },
    data: {
      label: 'React 前端',
      description: descriptions['fw-react'],
      implementation: '工作助手前端：\n• 聊天界面：消息列表 + 输入框 + 流式打字效果\n• SSE 接收：EventSource 接收后端流式响应\n• 日志上传：文件拖拽 + 文本编辑器\n\n岗位分析前端：\n• 表单输入：岗位名称、级别、JD 文本\n• 报告展示：结构化卡片 + 匹配度雷达图',
      bestPractice: '• 流式响应用 SSE（Server-Sent Events），不要 WebSocket\n• 聊天消息用 Markdown 渲染（支持代码块、表格）\n• 加载状态要友好：打字动画、进度提示\n• 移动端适配：响应式布局',
      layer: 'framework',
      project: 'both',
    },
    style: makeStyle('framework'),
  },
]

// ═══════════════════════════════════════════════════════════════
//  模型层
// ═══════════════════════════════════════════════════════════════
const modelNodes: Node<TechNodeData>[] = [
  {
    id: 'model-llm',
    position: { x: 60, y: 740 },
    data: {
      label: '大模型 (LLM)',
      description: descriptions['model-llm'],
      implementation: '推荐模型选择：\n• GPT-4o：综合能力最强，成本较高\n• DeepSeek-V3：性价比高，中文能力优秀\n• Qwen-Plus：阿里通义，国内访问快\n• Ollama 本地部署：Llama3/Qwen2，离线可用\n\nSpring AI 对接：配置 api-key + base-url，一行代码切换模型',
      bestPractice: '• 开发阶段用便宜模型（DeepSeek），上线切高质量模型\n• 简单任务用小模型，复杂推理用大模型（路由策略）\n• 设置 temperature：事实查询用 0，创意生成用 0.7\n• 做好降级：主模型超时/报错自动切备用模型',
      layer: 'model',
      project: 'both',
    },
    style: makeStyle('model'),
  },
  {
    id: 'model-prompt',
    position: { x: 240, y: 740 },
    data: {
      label: '提示词工程',
      description: descriptions['model-prompt'],
      implementation: '核心技巧：\n1. 角色设定：\"你是一个工作日志分析助手\"\n2. 任务说明：明确输入/输出格式\n3. Few-shot：提供 2-3 个示例\n4. 约束条件：\"仅基于提供的上下文回答\"\n5. 输出格式：JSON Mode 或 Markdown 模板\n\nSpring AI：用 @Value 加载外部 .st 模板文件',
      bestPractice: '• Prompt 模板放在 resources/prompts/ 目录，不要硬编码\n• 用 PromptTemplate 支持变量替换\n• 迭代优化：记录每次 Prompt 修改和效果变化\n• 复杂任务拆成多步 Prompt，比一个超长 Prompt 效果好\n• 用 System Prompt 设定角色，User Prompt 传具体问题',
      layer: 'model',
      project: 'both',
    },
    style: makeStyle('model'),
  },
  {
    id: 'model-embedding',
    position: { x: 420, y: 740 },
    data: {
      label: 'Embedding 模型',
      description: descriptions['model-embedding'],
      implementation: '推荐模型：\n• text-embedding-3-small（OpenAI）：1536维，性价比高\n• text-embedding-3-large（OpenAI）：3072维，精度更高\n• bge-large-zh（智源）：中文效果好，可本地部署\n• nomic-embed-text（Ollama）：本地运行，离线可用\n\nSpring AI：注入 EmbeddingModel Bean，调用 embed() 方法',
      bestPractice: '• 索引和查询必须用同一个 Embedding 模型\n• 中文场景优先选中文优化模型（bge-zh）\n• 批量向量化时用 batch API 降低成本\n• 模型更新后需要重新索引全部数据',
      layer: 'model',
      project: 'both',
    },
    style: makeStyle('model'),
  },
  {
    id: 'model-sft',
    position: { x: 660, y: 880 },
    data: {
      label: 'SFT 微调',
      description: descriptions['model-sft'],
      layer: 'model',
    },
    style: { ...makeStyle('model'), borderStyle: 'dashed' },
  },
  {
    id: 'model-rl',
    position: { x: 660, y: 1030 },
    data: {
      label: 'RL / RLHF',
      description: descriptions['model-rl'],
      layer: 'model',
    },
    style: { ...makeStyle('model'), borderStyle: 'dashed' },
  },
]

// ─── 额外：训练工具层（模型层下方） ──────────────────────────
const trainingNodes: Node<TechNodeData>[] = [
  {
    id: 'train-pytorch',
    position: { x: 460, y: 955 },
    data: {
      label: 'PyTorch',
      description: descriptions['train-pytorch'],
      layer: 'model',
    },
    style: { ...makeStyle('model'), borderStyle: 'dashed' },
  },
]

export const initialNodes: Node[] = [
  ...layerLabels,
  ...appNodes,
  ...orchNodes,
  ...capNodes,
  ...fwNodes,
  ...modelNodes,
  ...trainingNodes,
]

// ═══════════════════════════════════════════════════════════════
//  连线 — 展示调用关系
// ═══════════════════════════════════════════════════════════════
const edgeDefaults = {
  type: 'smoothstep' as const,
  animated: true,
  style: { stroke: '#475569', strokeWidth: 1.5 },
}

const workEdgeStyle = { stroke: '#10b981', strokeWidth: 2 }
const jobEdgeStyle  = { stroke: '#f59e0b', strokeWidth: 2 }
const bothEdgeStyle = { stroke: '#6366f1', strokeWidth: 1.5 }

export const initialEdges: Edge[] = [
  // ── 工作助手 调用链（绿色） ──────────────────────────────
  { id: 'e-work-react',     source: 'app-work',     target: 'orch-react',    ...edgeDefaults, style: workEdgeStyle, label: '对话驱动' },
  { id: 'e-work-memory',    source: 'app-work',     target: 'orch-memory',   ...edgeDefaults, style: workEdgeStyle, label: '记忆回溯' },
  { id: 'e-work-workflow',  source: 'app-work',     target: 'orch-workflow', ...edgeDefaults, style: workEdgeStyle, label: '日志入库' },
  { id: 'e-react-rag',      source: 'orch-react',   target: 'cap-rag',      ...edgeDefaults, style: workEdgeStyle },
  { id: 'e-react-fc',       source: 'orch-react',   target: 'cap-fc',       ...edgeDefaults, style: workEdgeStyle },
  { id: 'e-memory-context',source: 'orch-memory',   target: 'cap-context',  ...edgeDefaults, style: workEdgeStyle },
  { id: 'e-wf-skill',       source: 'orch-workflow', target: 'cap-skill',    ...edgeDefaults, style: workEdgeStyle },

  // ── 岗位需求分析 调用链（黄色） ────────────────────────────
  { id: 'e-job-plan',       source: 'app-job',      target: 'orch-plan',     ...edgeDefaults, style: jobEdgeStyle, label: '多步分析' },
  { id: 'e-job-reflect',    source: 'app-job',      target: 'orch-reflect',  ...edgeDefaults, style: jobEdgeStyle, label: '匹配评估' },
  { id: 'e-job-human',      source: 'app-job',      target: 'orch-human',    ...edgeDefaults, style: jobEdgeStyle, label: '人工校验' },
  { id: 'e-plan-fc',        source: 'orch-plan',    target: 'cap-fc',       ...edgeDefaults, style: jobEdgeStyle },
  { id: 'e-plan-rag',       source: 'orch-plan',    target: 'cap-rag',      ...edgeDefaults, style: jobEdgeStyle },
  { id: 'e-plan-mcp',       source: 'orch-plan',    target: 'cap-mcp',      ...edgeDefaults, style: jobEdgeStyle },
  { id: 'e-reflect-context',source: 'orch-reflect', target: 'cap-context',  ...edgeDefaults, style: jobEdgeStyle },
  { id: 'e-human-skill',    source: 'orch-human',   target: 'cap-skill',    ...edgeDefaults, style: jobEdgeStyle },

  // ── 能力层内部关系（蓝色虚线） ────────────────────────────
  { id: 'e-fc-mcp',         source: 'cap-fc',       target: 'cap-mcp',      ...edgeDefaults, animated: false, style: { stroke: '#3b82f6', strokeWidth: 2, strokeDasharray: '6 3' } },
  { id: 'e-mcp-skill',      source: 'cap-mcp',      target: 'cap-skill',    ...edgeDefaults, animated: false, style: { stroke: '#3b82f6', strokeWidth: 2, strokeDasharray: '6 3' } },

  // ── 能力层 → 框架层（紫色） ───────────────────────────────
  { id: 'e-rag-springai',   source: 'cap-rag',      target: 'fw-springai',  ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-rag-vectordb',   source: 'cap-rag',      target: 'fw-vectordb',  ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-fc-springai',    source: 'cap-fc',       target: 'fw-springai',  ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-mcp-springai',   source: 'cap-mcp',      target: 'fw-springai',  ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-skill-java',     source: 'cap-skill',    target: 'fw-java',      ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-context-springai',source:'cap-context',   target: 'fw-springai',  ...edgeDefaults, style: bothEdgeStyle },

  // ── 框架层 → 模型层 ─────────────────────────────────────
  { id: 'e-springai-llm',   source: 'fw-springai',  target: 'model-llm',      ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-springai-prompt', source: 'fw-springai',  target: 'model-prompt',   ...edgeDefaults, style: bothEdgeStyle },
  { id: 'e-vectordb-embed',  source: 'fw-vectordb',  target: 'model-embedding',...edgeDefaults, style: bothEdgeStyle },

  // ── 模型训练链路（灰色虚线 — 当前项目暂不涉及） ─────────
  { id: 'e-sft-llm',   source: 'model-sft',      target: 'model-llm',       ...edgeDefaults, animated: false, style: { stroke: '#64748b', strokeWidth: 1.5, strokeDasharray: '4 4' }, label: '微调产出模型' },
  { id: 'e-rl-sft',    source: 'model-rl',        target: 'model-sft',       ...edgeDefaults, animated: false, style: { stroke: '#64748b', strokeWidth: 1.5, strokeDasharray: '4 4' }, label: 'SFT 之后做 RL' },
  { id: 'e-pt-sft',    source: 'train-pytorch',   target: 'model-sft',       ...edgeDefaults, animated: false, style: { stroke: '#64748b', strokeWidth: 1.5, strokeDasharray: '4 4' }, label: '训练框架' },
  { id: 'e-pt-rl',     source: 'train-pytorch',   target: 'model-rl',        ...edgeDefaults, animated: false, style: { stroke: '#64748b', strokeWidth: 1.5, strokeDasharray: '4 4' }, label: '训练框架' },
]
