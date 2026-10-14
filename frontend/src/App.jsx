import { useCallback, useEffect, useMemo, useState } from 'react'
import {
  Activity, AlertCircle, AlertTriangle, ArrowDownRight, ArrowUpRight, Bot, Check,
  CheckCheck, ChevronDown, ChevronLeft, ChevronRight, CircleHelp, Clock3, Code2,
  ExternalLink, FileCode2, Filter, GitBranch, GitPullRequest, LayoutDashboard,
  LoaderCircle, LockKeyhole, Menu, Plus, RefreshCw, Search, Settings2, Shield,
  ShieldAlert, Sparkles, Trash2, X, Zap,
} from 'lucide-react'
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts'
import { api } from './api'
import './App.css'

const NAV = [
  { label: 'Overview', icon: LayoutDashboard, group: 'WORKSPACE' },
  { label: 'Repositories', icon: Code2, group: 'WORKSPACE' },
  { label: 'Pull Requests', icon: GitPullRequest, group: 'WORKSPACE' },
  { label: 'Review Findings', icon: ShieldAlert, group: 'WORKSPACE' },
  { label: 'Review History', icon: Activity, group: 'INSIGHTS' },
  { label: 'AI Agents', icon: Bot, group: 'INSIGHTS' },
  { label: 'Settings', icon: Settings2, group: 'SYSTEM' },
]
const AGENTS = [
  { key: 'BUG', name: 'Bug Hunter', description: 'Detects unsafe operations and likely runtime defects.', icon: AlertTriangle, color: 'amber' },
  { key: 'SECURITY', name: 'Sentinel', description: 'Scans changed lines for exposed credentials and risky patterns.', icon: Shield, color: 'violet' },
  { key: 'QUALITY', name: 'Code Quality', description: 'Flags debugging leftovers and maintainability smells.', icon: Code2, color: 'cyan' },
]
const arr = (value) => Array.isArray(value) ? value : []
const dateValue = (value) => value ? new Date(value) : null
const dateLabel = (value) => { const d = dateValue(value); return d && !Number.isNaN(d.getTime()) ? d.toLocaleString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' }) : '—' }
const shortDate = (value) => { const d = dateValue(value); return d && !Number.isNaN(d.getTime()) ? d.toLocaleDateString([], { month: 'short', day: 'numeric' }) : '—' }
const statusLabel = (status) => (status || 'UNKNOWN').replaceAll('_', ' ')
const severityClass = (severity) => (severity || 'LOW').toLowerCase()
const agentLabel = (type) => ({ BUG: 'Bug Hunter', SECURITY: 'Sentinel', QUALITY: 'Code Quality' }[type] || type || 'AI Agent')

function Badge({ children, tone = 'neutral' }) { return <span className={`badge badge-${tone}`}>{children}</span> }
function Empty({ icon: Icon = FileCode2, title, detail, action }) {
  return <div className="empty-state"><span className="empty-icon"><Icon size={22} /></span><h3>{title}</h3><p>{detail}</p>{action}</div>
}
function Modal({ title, subtitle, onClose, children }) {
  return <div className="modal-backdrop" onMouseDown={(e) => { if (e.target === e.currentTarget) onClose() }}><section className="modal"><header className="modal-head"><div><h2>{title}</h2>{subtitle && <p>{subtitle}</p>}</div><button className="icon-button" onClick={onClose} aria-label="Close"><X size={18} /></button></header>{children}</section></div>
}
function Metric({ label, value, detail, icon: Icon, accent = 'violet' }) {
  return <article className="metric-card"><div className={`metric-icon ${accent}`}><Icon size={18} /></div><div className="metric-label">{label}</div><div className="metric-value">{value}</div><div className="metric-detail">{detail}</div></article>
}
function FindingCard({ finding, onStatus, busy }) {
  const [expanded, setExpanded] = useState(false)
  return <article className="finding-card"><div className={`finding-severity ${severityClass(finding.severity)}`} /><div className="finding-main"><div className="finding-title-row"><div className="finding-title">{finding.message || 'Finding without a message'}</div><Badge tone={severityClass(finding.severity)}>{finding.severity || 'LOW'}</Badge></div><div className="finding-meta"><span><Bot size={13} /> {agentLabel(finding.agentType)}</span><span><FileCode2 size={13} /> {finding.filePath || 'File not provided'}{finding.lineNumber ? `:${finding.lineNumber}` : ''}</span><Badge tone={finding.status === 'RESOLVED' ? 'success' : finding.status === 'IGNORED' ? 'neutral' : 'warning'}>{statusLabel(finding.status)}</Badge></div>{expanded && <div className="finding-detail">Finding ID: #{finding.id} · Pull request ID: {finding.pullRequestId}</div>}</div><div className="finding-actions"><button className="text-button" onClick={() => setExpanded(!expanded)}>{expanded ? 'Less' : 'Details'}</button><select aria-label="Finding status" value={finding.status || 'OPEN'} disabled={busy} onChange={(e) => onStatus(finding, e.target.value)}><option value="OPEN">Open</option><option value="RESOLVED">Resolved</option><option value="IGNORED">Ignored</option></select></div></article>
}
function PageHeader({ eyebrow, title, description, action }) { return <div className="page-header"><div><div className="eyebrow">{eyebrow}</div><h1>{title}</h1><p>{description}</p></div>{action}</div> }

export default function App() {
  const [active, setActive] = useState('Overview')
  const [repositories, setRepositories] = useState([])
  const [pullRequests, setPullRequests] = useState([])
  const [findings, setFindings] = useState([])
  const [jobs, setJobs] = useState([])
  const [reviews, setReviews] = useState([])
  const [selectedPrId, setSelectedPrId] = useState('')
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)
  const [starting, setStarting] = useState(false)
  const [busyFindingId, setBusyFindingId] = useState(null)
  const [query, setQuery] = useState('')
  const [findingFilter, setFindingFilter] = useState('ALL')
  const [jobFilter, setJobFilter] = useState('ALL')
  const [toast, setToast] = useState(null)
  const [error, setError] = useState('')
  const [modal, setModal] = useState('')
  const [mobileNav, setMobileNav] = useState(false)
  const [repoForm, setRepoForm] = useState({ owner: '', name: '', fullName: '', defaultBranch: 'main' })
  const [prForm, setPrForm] = useState({ repositoryFullName: '', pullRequestNumber: '', title: '', author: '', sourceBranch: '', targetBranch: 'main' })
  const [saving, setSaving] = useState(false)

  const notify = useCallback((message, type = 'success') => {
    setToast({ message, type })
    window.setTimeout(() => setToast(null), 3600)
  }, [])

  const loadData = useCallback(async ({ quiet = false } = {}) => {
    if (quiet) setRefreshing(true); else setLoading(true)
    setError('')
    try {
      const [repoData, prData, jobData, findingData, reviewData] = await Promise.all([
        api.getRepositories(), api.getPullRequests(), api.getReviewJobs(), api.getAllReviewFindings(), api.getReviews(),
      ])
      setRepositories(arr(repoData)); setPullRequests(arr(prData)); setJobs(arr(jobData)); setFindings(arr(findingData)); setReviews(arr(reviewData))
      setSelectedPrId((current) => {
        if (current && arr(prData).some((pr) => String(pr.id) === String(current))) return String(current)
        return arr(prData)[0] ? String(arr(prData)[0].id) : ''
      })
    } catch (e) {
      setError(e.message || 'Could not connect to the API.')
    } finally { setLoading(false); setRefreshing(false) }
  }, [])

  useEffect(() => { loadData() }, [loadData])

  const selectedPr = useMemo(() => pullRequests.find((pr) => String(pr.id) === String(selectedPrId)), [pullRequests, selectedPrId])
  const selectedFindings = useMemo(() => selectedPr ? findings.filter((f) => String(f.pullRequestId) === String(selectedPr.id)) : findings, [findings, selectedPr])
  const visibleFindings = useMemo(() => selectedFindings.filter((f) => {
    const matchesFilter = findingFilter === 'ALL' || f.agentType === findingFilter || f.severity === findingFilter || f.status === findingFilter
    const text = `${f.message || ''} ${f.filePath || ''} ${f.agentType || ''} ${f.severity || ''} ${f.status || ''}`.toLowerCase()
    return matchesFilter && text.includes(query.toLowerCase())
  }), [selectedFindings, findingFilter, query])
  const visibleJobs = useMemo(() => jobs.filter((job) => {
    const pr = pullRequests.find((p) => String(p.id) === String(job.pullRequestId))
    const text = `${pr?.title || ''} ${pr?.repositoryFullName || ''} ${job.status || ''} ${job.id || ''}`.toLowerCase()
    return (jobFilter === 'ALL' || job.status === jobFilter) && text.includes(query.toLowerCase())
  }), [jobs, pullRequests, jobFilter, query])
  const openCount = findings.filter((f) => f.status === 'OPEN').length
  const criticalCount = findings.filter((f) => ['CRITICAL', 'HIGH'].includes(f.severity) && f.status === 'OPEN').length
  const completedCount = jobs.filter((j) => j.status === 'COMPLETED').length
  const failedCount = jobs.filter((j) => j.status === 'FAILED').length
  const chartData = useMemo(() => Array.from({ length: 7 }, (_, i) => {
    const d = new Date(); d.setDate(d.getDate() - (6 - i)); d.setHours(0, 0, 0, 0)
    const next = new Date(d); next.setDate(next.getDate() + 1)
    const dayJobs = jobs.filter((j) => { const dt = dateValue(j.createdAt); return dt && dt >= d && dt < next })
    return { day: d.toLocaleDateString([], { weekday: 'short' }), reviews: dayJobs.length, findings: findings.filter((f) => { const dt = dateValue(f.createdAt); return dt && dt >= d && dt < next }).length }
  }), [jobs, findings])

  const changeFindingStatus = async (finding, status) => {
    setBusyFindingId(finding.id)
    try {
      await api.updateFindingStatus(finding.id, status)
      setFindings((old) => old.map((f) => f.id === finding.id ? { ...f, status } : f))
      notify(`Finding #${finding.id} marked ${status.toLowerCase()}.`)
    } catch (e) { notify(e.message || 'Could not update finding.', 'error') }
    finally { setBusyFindingId(null) }
  }

  const runReview = async (pullRequestId = selectedPr?.id) => {
    const reviewPr = pullRequests.find((pr) => String(pr.id) === String(pullRequestId))
    if (!reviewPr) { notify('Create or select a pull request first.', 'error'); setActive('Pull Requests'); return }
    setStarting(true)
    try {
      const job = await api.createReviewJob(reviewPr.id)
      const jobId = job?.id
      if (!jobId) throw new Error('The API did not return a review job ID.')
      await api.processReviewJob(jobId)
      await loadData({ quiet: true })
      notify(`Review job #${jobId} processed. Check the findings and history.`)
    } catch (e) { notify(e.message || 'Review could not be started.', 'error'); await loadData({ quiet: true }) }
    finally { setStarting(false) }
  }

  const saveRepository = async (e) => {
    e.preventDefault(); setSaving(true)
    const fullName = repoForm.fullName.trim() || `${repoForm.owner.trim()}/${repoForm.name.trim()}`
    try {
      await api.createRepository({ ...repoForm, owner: repoForm.owner.trim(), name: repoForm.name.trim(), fullName, defaultBranch: repoForm.defaultBranch.trim() || 'main' })
      setModal(''); setRepoForm({ owner: '', name: '', fullName: '', defaultBranch: 'main' }); await loadData({ quiet: true }); setActive('Repositories'); notify('Repository added.')
    } catch (e2) { notify(e2.message || 'Could not add repository.', 'error') } finally { setSaving(false) }
  }
  const savePullRequest = async (e) => {
    e.preventDefault(); setSaving(true)
    try {
      await api.createPullRequest({ ...prForm, pullRequestNumber: Number(prForm.pullRequestNumber), repositoryFullName: prForm.repositoryFullName.trim(), title: prForm.title.trim(), author: prForm.author.trim(), sourceBranch: prForm.sourceBranch.trim(), targetBranch: prForm.targetBranch.trim() || 'main' })
      setModal(''); setPrForm({ repositoryFullName: '', pullRequestNumber: '', title: '', author: '', sourceBranch: '', targetBranch: 'main' }); await loadData({ quiet: true }); setActive('Pull Requests'); notify('Pull request added.')
    } catch (e2) { notify(e2.message || 'Could not add pull request.', 'error') } finally { setSaving(false) }
  }
  const deleteRepository = async (repo) => {
    if (!window.confirm(`Delete repository ${repo.fullName}? This may fail if pull requests still reference it.`)) return
    try { await api.deleteRepository(repo.id); await loadData({ quiet: true }); notify('Repository deleted.') } catch (e) { notify(e.message || 'Could not delete repository.', 'error') }
  }
  const deletePullRequest = async (pr) => {
    if (!window.confirm(`Delete pull request #${pr.pullRequestNumber}?`)) return
    try { await api.deletePullRequest(pr.id); await loadData({ quiet: true }); notify('Pull request deleted.') } catch (e) { notify(e.message || 'Could not delete pull request.', 'error') }
  }

  const headerAction = active === 'Repositories'
    ? <button className="primary-button" onClick={() => setModal('repository')}><Plus size={16} /> Add repository</button>
    : active === 'Pull Requests'
      ? <button className="primary-button" onClick={() => { setPrForm((f) => ({ ...f, repositoryFullName: repositories[0]?.fullName || '' })); setModal('pull-request') }}><Plus size={16} /> Add pull request</button>
      : active === 'Overview'
        ? <button className="primary-button" onClick={runReview} disabled={starting || !selectedPr}><Sparkles size={16} /> {starting ? 'Reviewing…' : 'Run AI review'}</button>
        : <button className="secondary-button" onClick={() => loadData({ quiet: true })} disabled={refreshing}><RefreshCw size={15} className={refreshing ? 'spin' : ''} /> Refresh data</button>

  return <div className="app-shell">
    <aside className={`sidebar ${mobileNav ? 'sidebar-open' : ''}`}>
      <div className="brand"><div className="brand-mark"><Sparkles size={21} /></div><div className="brand-copy"><strong>PRISM<span>AI</span></strong><small>INTELLIGENT CODE REVIEW</small></div><button className="mobile-close icon-button" onClick={() => setMobileNav(false)}><X size={18} /></button></div>
      <div className="workspace-switch"><div className="workspace-avatar">P</div><div className="workspace-copy"><strong>Personal workspace</strong><small>Developer plan</small></div><ChevronDown size={15} /></div>
      {['WORKSPACE', 'INSIGHTS', 'SYSTEM'].map((group) => <div className="nav-group" key={group}><div className="nav-label">{group}</div>{NAV.filter((n) => n.group === group).map(({ label, icon: Icon }) => <button key={label} className={`nav-item ${active === label ? 'active' : ''}`} onClick={() => { setActive(label); setMobileNav(false); setQuery('') }}><Icon size={17} strokeWidth={1.8} /><span>{label}</span>{label === 'Review Findings' && openCount > 0 && <span className="nav-count">{openCount}</span>}</button>)}</div>)}
      <div className="sidebar-bottom"><div className="status-card"><div className="status-dot" /><div><strong>API workspace</strong><small>{error ? 'Connection issue' : loading ? 'Connecting…' : 'Dashboard connected'}</small></div><button className="icon-button small" onClick={() => loadData({ quiet: true })} title="Refresh API status"><RefreshCw size={14} /></button></div><div className="user-card"><div className="user-avatar">D</div><div><strong>Developer</strong><small>Local environment</small></div><span className="user-more">•••</span></div></div>
    </aside>
    {mobileNav && <button className="nav-scrim" onClick={() => setMobileNav(false)} aria-label="Close navigation" />}
    <main className="main-area">
      <header className="topbar"><div className="topbar-left"><button className="mobile-menu icon-button" onClick={() => setMobileNav(true)}><Menu size={19} /></button><span className="breadcrumb">Workspace</span><ChevronRight size={14} /><strong>{active}</strong></div><div className="topbar-right"><div className="api-indicator"><span /> API connected</div><button className="icon-button" onClick={() => loadData({ quiet: true })} title="Refresh data"><RefreshCw size={16} className={refreshing ? 'spin' : ''} /></button><div className="top-avatar">D</div></div></header>
      <div className="content-wrap">
        {error && <div className="error-banner"><AlertCircle size={17} /><div><strong>Unable to load dashboard data</strong><p>{error}. Confirm the Spring Boot backend is running on port 8080 and the Vite proxy is configured.</p></div><button className="text-button" onClick={() => loadData()}><RefreshCw size={14} /> Retry</button></div>}
        {loading && !repositories.length && !pullRequests.length ? <div className="loading-screen"><LoaderCircle size={27} className="spin" /><strong>Connecting to PRISM AI</strong><span>Loading live data from your Spring Boot API…</span></div> : <>
          {active === 'Overview' && <>
            <PageHeader eyebrow="YOUR CODE, UNDER THE LENS" title="Good code starts with clarity." description="A real-time view of your repositories, pull requests, and AI-powered code reviews." action={headerAction} />
            <div className="overview-context"><div className="context-left"><span className="context-dot" /> Reviewing pull request</div><select value={selectedPrId} onChange={(e) => setSelectedPrId(e.target.value)} aria-label="Select pull request">{pullRequests.length ? pullRequests.map((pr) => <option key={pr.id} value={pr.id}>{pr.repositoryFullName} · #{pr.pullRequestNumber}</option>) : <option value="">No pull requests available</option>}</select></div>
            <div className="metrics-grid"><Metric label="Repositories" value={repositories.length} detail="Connected to workspace" icon={Code2} accent="violet" /><Metric label="Pull requests" value={pullRequests.length} detail="Available for review" icon={GitPullRequest} accent="cyan" /><Metric label="Open findings" value={openCount} detail={`${criticalCount} high / critical`} icon={ShieldAlert} accent="amber" /><Metric label="Completed reviews" value={completedCount} detail={`${failedCount} failed jobs`} icon={CheckCheck} accent="green" /></div>
            <div className="dashboard-grid"><section className="panel activity-panel"><div className="panel-heading"><div><div className="section-kicker">ACTIVITY</div><h2>Review activity</h2><p>Jobs created over the last seven days</p></div><Badge tone="violet"><Activity size={12} /> 7 DAYS</Badge></div><div className="chart-wrap">{jobs.length ? <ResponsiveContainer width="100%" height="100%"><AreaChart data={chartData} margin={{ top: 12, right: 8, left: -18, bottom: 0 }}><defs><linearGradient id="reviewsGradient" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#a78bfa" stopOpacity={0.28} /><stop offset="100%" stopColor="#a78bfa" stopOpacity={0} /></linearGradient></defs><CartesianGrid stroke="#242837" strokeDasharray="3 5" vertical={false} /><XAxis dataKey="day" axisLine={false} tickLine={false} tick={{ fill: '#777f95', fontSize: 11 }} dy={9} /><YAxis allowDecimals={false} axisLine={false} tickLine={false} tick={{ fill: '#777f95', fontSize: 11 }} /><Tooltip contentStyle={{ background: '#151925', border: '1px solid #303548', borderRadius: 10, color: '#f4f4f8' }} /><Area type="monotone" dataKey="reviews" name="Review jobs" stroke="#a78bfa" strokeWidth={2.5} fill="url(#reviewsGradient)" activeDot={{ r: 4, fill: '#a78bfa' }} /></AreaChart></ResponsiveContainer> : <Empty icon={Activity} title="No review activity yet" detail="Run a review to start building your activity timeline." />}</div><div className="chart-legend"><span><i className="legend-dot violet-dot" /> Review jobs</span><span className="chart-note">Based on API job timestamps</span></div></section>
              <section className="panel agent-panel"><div className="panel-heading"><div><div className="section-kicker">MULTI-AGENT ENGINE</div><h2>Your AI reviewers</h2><p>Specialists working in parallel</p></div><span className="agent-orb"><Sparkles size={18} /></span></div><div className="agent-list">{AGENTS.map(({ key, name, description, icon: Icon, color }) => <div className="agent-item" key={key}><div className={`agent-icon ${color}`}><Icon size={17} /></div><div className="agent-copy"><strong>{name}</strong><p>{description}</p></div><Badge tone="success">Ready</Badge></div>)}</div><button className="panel-link" onClick={() => setActive('AI Agents')}>Explore agent details <ArrowUpRight size={15} /></button></section></div>
            <section className="panel findings-panel"><div className="panel-heading"><div><div className="section-kicker">SIGNAL OVER NOISE</div><h2>Latest findings</h2><p>{selectedPr ? `Results for ${selectedPr.repositoryFullName} #${selectedPr.pullRequestNumber}` : 'Findings returned by your review API'}</p></div><button className="secondary-button compact" onClick={() => setActive('Review Findings')}>View all <ArrowUpRight size={14} /></button></div>{selectedFindings.length ? <div className="finding-list">{selectedFindings.slice(0, 4).map((f) => <FindingCard key={f.id} finding={f} onStatus={changeFindingStatus} busy={busyFindingId === f.id} />)}</div> : <Empty icon={ShieldAlert} title="Nothing flagged yet" detail={selectedPr ? 'Run an AI review to inspect this pull request.' : 'Add a pull request, then run a review.'} action={!selectedPr && <button className="secondary-button" onClick={() => setActive('Pull Requests')}>Set up a pull request <ChevronRight size={14} /></button>} />}</section>
          </>}

          {active === 'Repositories' && <><PageHeader eyebrow="SOURCE CONTROL" title="Repositories" description="Manage the repositories connected to your review workspace." action={headerAction} /><div className="toolbar"><div className="search-box"><Search size={16} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search repositories…" /></div><Badge>{repositories.length} total</Badge></div><section className="panel table-panel">{repositories.filter((r) => `${r.fullName} ${r.owner} ${r.name} ${r.defaultBranch}`.toLowerCase().includes(query.toLowerCase())).length ? <div className="data-table-wrap"><table className="data-table"><thead><tr><th>Repository</th><th>Default branch</th><th>Pull requests</th><th>Review status</th><th /></tr></thead><tbody>{repositories.filter((r) => `${r.fullName} ${r.owner} ${r.name} ${r.defaultBranch}`.toLowerCase().includes(query.toLowerCase())).map((repo) => { const count = pullRequests.filter((p) => p.repositoryFullName === repo.fullName).length; return <tr key={repo.id}><td><div className="repo-cell"><div className="table-icon"><Code2 size={17} /></div><div><strong>{repo.fullName}</strong><small>Repository ID #{repo.id}</small></div></div></td><td><span className="branch-chip"><GitBranch size={13} /> {repo.defaultBranch || 'main'}</span></td><td>{count}</td><td><Badge tone={count ? 'success' : 'neutral'}>{count ? 'Configured' : 'No PRs yet'}</Badge></td><td><button className="icon-button danger-hover" onClick={() => deleteRepository(repo)} title="Delete repository"><Trash2 size={15} /></button></td></tr> })}</tbody></table></div> : <Empty icon={Code2} title="No repositories found" detail={query ? 'Try another search term.' : 'Add a repository to start organizing code reviews.'} action={!query && <button className="primary-button" onClick={() => setModal('repository')}><Plus size={15} /> Add repository</button>} />}</section></>}

          {active === 'Pull Requests' && <><PageHeader eyebrow="REVIEW QUEUE" title="Pull requests" description="Choose a change set, inspect its review history, and launch the analysis pipeline." action={headerAction} /><div className="toolbar"><div className="search-box"><Search size={16} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search title, repository, author…" /></div><Badge>{pullRequests.length} pull requests</Badge></div><div className="pr-grid">{pullRequests.filter((pr) => `${pr.title} ${pr.repositoryFullName} ${pr.author} ${pr.pullRequestNumber} ${pr.sourceBranch}`.toLowerCase().includes(query.toLowerCase())).map((pr) => { const prJobs = jobs.filter((j) => String(j.pullRequestId) === String(pr.id)); const latest = [...prJobs].sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0))[0]; const count = findings.filter((f) => String(f.pullRequestId) === String(pr.id) && f.status === 'OPEN').length; return <article className={`pr-card ${String(selectedPrId) === String(pr.id) ? 'pr-card-selected' : ''}`} key={pr.id}><div className="pr-card-top"><div className="pr-icon"><GitPullRequest size={18} /></div><Badge tone={latest?.status === 'COMPLETED' ? 'success' : latest?.status === 'FAILED' ? 'danger' : 'neutral'}>{statusLabel(latest?.status || 'NOT REVIEWED')}</Badge></div><h3>{pr.title || `Pull request #${pr.pullRequestNumber}`}</h3><p className="pr-repo">{pr.repositoryFullName} <span>#{pr.pullRequestNumber}</span></p><div className="branch-flow"><span>{pr.sourceBranch || 'source'}</span><ChevronRight size={13} /><span>{pr.targetBranch || 'main'}</span></div><div className="pr-card-stats"><span><ShieldAlert size={14} /> {count} open findings</span><span><Clock3 size={14} /> {dateLabel(latest?.createdAt)}</span></div><div className="pr-card-actions"><button className="secondary-button compact" onClick={() => { setSelectedPrId(String(pr.id)); setActive('Review Findings') }}>Findings <ChevronRight size={14} /></button><button className="primary-button compact" disabled={starting} onClick={() => { setSelectedPrId(String(pr.id)); runReview(pr.id) }}><Sparkles size={14} /> Review</button><button className="icon-button danger-hover" onClick={() => deletePullRequest(pr)} title="Delete pull request"><Trash2 size={15} /></button></div></article> })}</div>{!pullRequests.length && <section className="panel"><Empty icon={GitPullRequest} title="Your review queue is empty" detail="Add a pull request with a repository full name and PR number." action={<button className="primary-button" onClick={() => setModal('pull-request')}><Plus size={15} /> Add pull request</button>} /></section>}</>}

          {active === 'Review Findings' && <><PageHeader eyebrow="CODE INTELLIGENCE" title="Review findings" description="Triage issues returned by the backend. Status changes are saved through the API." action={<button className="secondary-button" onClick={() => loadData({ quiet: true })}><RefreshCw size={15} /> Refresh findings</button>} /><div className="filter-row"><div className="search-box"><Search size={16} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search messages, files, agents…" /></div><div className="filter-control"><Filter size={15} /><select value={findingFilter} onChange={(e) => setFindingFilter(e.target.value)}><option value="ALL">All findings</option><option value="OPEN">Open</option><option value="RESOLVED">Resolved</option><option value="IGNORED">Ignored</option><option value="CRITICAL">Critical</option><option value="HIGH">High severity</option><option value="BUG">Bug Hunter</option><option value="SECURITY">Security</option><option value="QUALITY">Quality</option></select></div><div className="filter-control"><GitPullRequest size={15} /><select value={selectedPrId} onChange={(e) => setSelectedPrId(e.target.value)}><option value="">All pull requests</option>{pullRequests.map((pr) => <option key={pr.id} value={pr.id}>#{pr.pullRequestNumber} · {pr.repositoryFullName}</option>)}</select></div></div><div className="findings-summary"><div><span className="summary-dot red" /> <strong>{selectedFindings.filter((f) => f.status === 'OPEN').length}</strong> open</div><div><span className="summary-dot green" /> <strong>{selectedFindings.filter((f) => f.status === 'RESOLVED').length}</strong> resolved</div><div><span className="summary-dot gray" /> <strong>{selectedFindings.filter((f) => f.status === 'IGNORED').length}</strong> ignored</div><span className="summary-spacer" /><span className="muted-text">{visibleFindings.length} displayed</span></div><section className="panel finding-list-panel">{visibleFindings.length ? <div className="finding-list">{visibleFindings.map((f) => <FindingCard key={f.id} finding={f} onStatus={changeFindingStatus} busy={busyFindingId === f.id} />)}</div> : <Empty icon={ShieldAlert} title="No matching findings" detail={findings.length ? 'Change the search or filter to see more results.' : 'Run an AI review to generate findings from the backend.'} action={findings.length > 0 && <button className="secondary-button" onClick={() => { setQuery(''); setFindingFilter('ALL') }}>Clear filters</button>} />}</section></>}

          {active === 'Review History' && <><PageHeader eyebrow="AUDIT TRAIL" title="Review history" description="Every queued, completed, and failed review job returned by your backend." action={headerAction} /><div className="metrics-grid history-metrics"><Metric label="Total jobs" value={jobs.length} detail="All recorded review jobs" icon={Activity} accent="violet" /><Metric label="Completed" value={completedCount} detail="Successfully processed" icon={CheckCheck} accent="green" /><Metric label="Failed" value={failedCount} detail="Jobs needing attention" icon={AlertCircle} accent="amber" /><Metric label="In progress" value={jobs.filter((j) => ['QUEUED', 'RUNNING'].includes(j.status)).length} detail="Queued or running" icon={Clock3} accent="cyan" /></div><div className="toolbar"><div className="search-box"><Search size={16} /><input value={query} onChange={(e) => setQuery(e.target.value)} placeholder="Search jobs or pull requests…" /></div><div className="filter-control"><Filter size={15} /><select value={jobFilter} onChange={(e) => setJobFilter(e.target.value)}><option value="ALL">All statuses</option><option value="QUEUED">Queued</option><option value="RUNNING">Running</option><option value="COMPLETED">Completed</option><option value="FAILED">Failed</option></select></div></div><section className="panel table-panel">{visibleJobs.length ? <div className="data-table-wrap"><table className="data-table"><thead><tr><th>Review job</th><th>Pull request</th><th>Status</th><th>Created</th><th>Started</th><th>Completed</th></tr></thead><tbody>{visibleJobs.slice().sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0)).map((job) => { const pr = pullRequests.find((p) => String(p.id) === String(job.pullRequestId)); return <tr key={job.id}><td><div className="job-id"><span className="job-mini-icon"><Activity size={14} /></span><div><strong>Job #{job.id}</strong><small>{job.errorMessage || `Pull request ID ${job.pullRequestId}`}</small></div></div></td><td><strong>{pr ? `#${pr.pullRequestNumber} · ${pr.repositoryFullName}` : `PR ID ${job.pullRequestId}`}</strong><small className="table-subline">{pr?.title || 'Pull request'}</small></td><td><Badge tone={job.status === 'COMPLETED' ? 'success' : job.status === 'FAILED' ? 'danger' : job.status === 'RUNNING' ? 'warning' : 'neutral'}>{statusLabel(job.status)}</Badge></td><td>{dateLabel(job.createdAt)}</td><td>{dateLabel(job.startedAt)}</td><td>{dateLabel(job.completedAt)}</td></tr> })}</tbody></table></div> : <Empty icon={Activity} title="No review jobs match" detail="Try another filter or run a review to create a job." />}</section></>}

          {active === 'AI Agents' && <><PageHeader eyebrow="SPECIALIST ANALYSIS" title="AI agents" description="PRISM's focused review agents. These descriptions reflect the analyzers in your current backend." action={<button className="primary-button" onClick={runReview} disabled={starting || !selectedPr}><Sparkles size={15} /> {starting ? 'Reviewing…' : 'Run a review'}</button>} /><div className="agent-hero"><div className="agent-hero-glow"><Sparkles size={28} /></div><div><div className="section-kicker">PRISM REVIEW ENGINE</div><h2>Three lenses. One clearer codebase.</h2><p>Each analyzer inspects the pull request diff and persists any detected findings through the review engine.</p></div><div className="agent-hero-stat"><strong>03</strong><span>Specialist agents</span></div></div><div className="agent-cards">{AGENTS.map(({ key, name, description, icon: Icon, color }) => { const count = findings.filter((f) => f.agentType === key).length; return <article className="agent-detail-card" key={key}><div className={`agent-detail-icon ${color}`}><Icon size={22} /></div><div className="agent-detail-head"><Badge tone="success">Configured</Badge><span className="agent-type">{key}</span></div><h3>{name}</h3><p>{description}</p><div className="agent-capabilities"><div><Check size={14} /> Inspects added diff lines</div><div><Check size={14} /> Saves findings to review API</div><div><Check size={14} /> Supports status triage</div></div><div className="agent-detail-foot"><span>Recorded findings</span><strong>{count}</strong></div></article> })}</div><div className="info-note"><CircleHelp size={17} /><p><strong>Current implementation note</strong> The backend analyzers use pattern-based checks; they are not a hosted LLM unless you add an LLM provider to the Spring Boot service.</p></div></>}

          {active === 'Settings' && <><PageHeader eyebrow="WORKSPACE CONFIGURATION" title="Settings" description="Review the current local setup and refresh the live API state." action={<button className="secondary-button" onClick={() => loadData({ quiet: true })}><RefreshCw size={15} /> Test connection</button>} /><div className="settings-layout"><section className="panel settings-panel"><div className="settings-section-head"><div className="settings-symbol"><Activity size={18} /></div><div><h2>Backend connection</h2><p>Requests are routed through the Vite development proxy.</p></div><Badge tone={error ? 'danger' : loading ? 'warning' : 'success'}>{error ? 'Error' : loading ? 'Checking' : 'Connected'}</Badge></div><div className="settings-row"><div><strong>Frontend origin</strong><small>Vite development server</small></div><code>localhost:5173</code></div><div className="settings-row"><div><strong>Backend origin</strong><small>Spring Boot API</small></div><code>localhost:8080</code></div><div className="settings-row"><div><strong>API prefix</strong><small>All dashboard requests</small></div><code>/api</code></div><div className="settings-row"><div><strong>Repository records</strong><small>Loaded from GET /api/repositories</small></div><strong>{repositories.length}</strong></div><div className="settings-row"><div><strong>Pull request records</strong><small>Loaded from GET /api/pull-requests</small></div><strong>{pullRequests.length}</strong></div><div className="settings-row"><div><strong>Finding records</strong><small>Loaded from GET /api/review-findings</small></div><strong>{findings.length}</strong></div></section><section className="panel settings-panel"><div className="settings-section-head"><div className="settings-symbol cyan"><LockKeyhole size={18} /></div><div><h2>Review behavior</h2><p>How this frontend interacts with your API.</p></div></div><div className="settings-row"><div><strong>Finding status changes</strong><small>Persisted with PUT /api/review-findings/{'{id}'}/status</small></div><Badge tone="success">Live API</Badge></div><div className="settings-row"><div><strong>Review execution</strong><small>Creates a job then calls POST /process</small></div><Badge tone="success">Live API</Badge></div><div className="settings-row"><div><strong>Repository / PR changes</strong><small>Create and delete operations use backend endpoints</small></div><Badge tone="success">Live API</Badge></div><div className="settings-footnote"><AlertTriangle size={15} /><span>Keep database credentials in environment variables; never commit secrets into application.yml.</span></div></section></div></>}
        </>}
        <footer className="app-footer"><span><span className="footer-mark">✳</span> PRISM AI <span className="footer-divider">/</span> Code review, with clarity.</span><span>Built for focused reviews <span className="footer-heart">✦</span></span></footer>
      </div>
    </main>

    {modal === 'repository' && <Modal title="Add repository" subtitle="Connect a GitHub repository to the workspace." onClose={() => setModal('')}><form className="modal-form" onSubmit={saveRepository}><label>Owner<input required value={repoForm.owner} onChange={(e) => setRepoForm({ ...repoForm, owner: e.target.value })} placeholder="e.g. spring-projects" /></label><label>Repository name<input required value={repoForm.name} onChange={(e) => setRepoForm({ ...repoForm, name: e.target.value })} placeholder="e.g. spring-boot" /></label><label>Full name <span className="optional-label">Optional if owner/name provided</span><input value={repoForm.fullName} onChange={(e) => setRepoForm({ ...repoForm, fullName: e.target.value })} placeholder="owner/repository" /></label><label>Default branch<input value={repoForm.defaultBranch} onChange={(e) => setRepoForm({ ...repoForm, defaultBranch: e.target.value })} placeholder="main" /></label><div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setModal('')}>Cancel</button><button className="primary-button" disabled={saving}><Plus size={15} /> {saving ? 'Saving…' : 'Add repository'}</button></div></form></Modal>}
    {modal === 'pull-request' && <Modal title="Add pull request" subtitle="Register the pull request your review agents should inspect." onClose={() => setModal('')}><form className="modal-form" onSubmit={savePullRequest}><label>Repository<select required value={prForm.repositoryFullName} onChange={(e) => setPrForm({ ...prForm, repositoryFullName: e.target.value })}><option value="">Select a repository</option>{repositories.map((r) => <option key={r.id} value={r.fullName}>{r.fullName}</option>)}</select></label><label>Pull request number<input type="number" min="1" required value={prForm.pullRequestNumber} onChange={(e) => setPrForm({ ...prForm, pullRequestNumber: e.target.value })} placeholder="e.g. 42" /></label><label>Title<input required value={prForm.title} onChange={(e) => setPrForm({ ...prForm, title: e.target.value })} placeholder="Describe the change" /></label><label>Author<input required value={prForm.author} onChange={(e) => setPrForm({ ...prForm, author: e.target.value })} placeholder="GitHub username" /></label><div className="form-two"><label>Source branch<input required value={prForm.sourceBranch} onChange={(e) => setPrForm({ ...prForm, sourceBranch: e.target.value })} placeholder="feature/my-change" /></label><label>Target branch<input required value={prForm.targetBranch} onChange={(e) => setPrForm({ ...prForm, targetBranch: e.target.value })} placeholder="main" /></label></div>{!repositories.length && <div className="form-warning"><AlertCircle size={15} /> Add a repository first, then create a pull request.</div>}<div className="modal-actions"><button type="button" className="secondary-button" onClick={() => setModal('')}>Cancel</button><button className="primary-button" disabled={saving || !repositories.length}><Plus size={15} /> {saving ? 'Saving…' : 'Add pull request'}</button></div></form></Modal>}
    {toast && <div className={`toast toast-${toast.type}`}><span className="toast-icon">{toast.type === 'error' ? <AlertCircle size={17} /> : <Check size={17} />}</span><span>{toast.message}</span><button className="icon-button" onClick={() => setToast(null)}><X size={15} /></button></div>}
  </div>
}
