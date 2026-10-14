const API_BASE_URL = ''

async function request(path, options = {}) {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      ...(options.body ? { 'Content-Type': 'application/json' } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    const message = await response.text().catch(() => '')
    throw new Error(message || `Request failed (${response.status})`)
  }
  if (response.status === 204) return null
  const text = await response.text()
  if (!text.trim()) return null
  try { return JSON.parse(text) } catch { throw new Error(`Server returned invalid JSON (${response.status})`) }
}

const json = (method, body) => ({ method, body: JSON.stringify(body) })

export const api = {
  getRepositories: () => request('/api/repositories'),
  getRepository: (id) => request(`/api/repositories/${id}`),
  createRepository: (data) => request('/api/repositories', json('POST', data)),
  updateRepository: (id, data) => request(`/api/repositories/${id}`, json('PUT', data)),
  deleteRepository: (id) => request(`/api/repositories/${id}`, { method: 'DELETE' }),

  getPullRequests: () => request('/api/pull-requests'),
  getPullRequest: (id) => request(`/api/pull-requests/${id}`),
  createPullRequest: (data) => request('/api/pull-requests', json('POST', data)),
  updatePullRequest: (id, data) => request(`/api/pull-requests/${id}`, json('PUT', data)),
  deletePullRequest: (id) => request(`/api/pull-requests/${id}`, { method: 'DELETE' }),

  getReviewFindings: (pullRequestId) => request(`/api/review-findings/pull-request/${pullRequestId}`),
  getAllReviewFindings: () => request('/api/review-findings'),
  getReviewFinding: (id) => request(`/api/review-findings/${id}`),
  createReviewFinding: (data) => request('/api/review-findings', json('POST', data)),
  updateFindingStatus: (id, status) => request(`/api/review-findings/${id}/status?status=${encodeURIComponent(status)}`, { method: 'PUT' }),
  deleteReviewFinding: (id) => request(`/api/review-findings/${id}`, { method: 'DELETE' }),

  getReviews: () => request('/api/reviews'),
  getReviewsForPullRequest: (id) => request(`/api/reviews/pull-request/${id}`),
  createReview: (data) => request('/api/reviews', json('POST', data)),

  getReviewJobs: () => request('/api/review-jobs'),
  getReviewJobsForPullRequest: (id) => request(`/api/review-jobs/pull-request/${id}`),
  getReviewJob: (id) => request(`/api/review-jobs/${id}`),
  createReviewJob: (pullRequestId) => request(`/api/review-jobs/${pullRequestId}`, { method: 'POST' }),
  processReviewJob: (jobId) => request(`/api/review-jobs/${jobId}/process`, { method: 'POST' }),
  startReviewJob: (jobId) => request(`/api/review-jobs/${jobId}/start`, { method: 'PUT' }),
  completeReviewJob: (jobId) => request(`/api/review-jobs/${jobId}/complete`, { method: 'PUT' }),
  failReviewJob: (jobId, errorMessage) => request(`/api/review-jobs/${jobId}/fail?errorMessage=${encodeURIComponent(errorMessage)}`, { method: 'PUT' }),

  getPullRequestDiff: (repositoryFullName, pullRequestNumber) => request(`/api/github/pull-request-diff?repositoryFullName=${encodeURIComponent(repositoryFullName)}&pullRequestNumber=${encodeURIComponent(pullRequestNumber)}`),
}
