/* =========================================================
   DomainSeller — Frontend JavaScript
   ========================================================= */

const API_BASE = '/api/domains';

// ---- DOM references ----
const grid         = document.getElementById('domain-grid');
const resultCount  = document.getElementById('result-count');
const searchInput  = document.getElementById('search-input');
const searchBtn    = document.getElementById('search-btn');
const filterCat    = document.getElementById('filter-category');
const filterMin    = document.getElementById('filter-min');
const filterMax    = document.getElementById('filter-max');
const filterBtn    = document.getElementById('filter-btn');
const resetBtn     = document.getElementById('reset-btn');
const modalOverlay = document.getElementById('modal-overlay');
const modalClose   = document.getElementById('modal-close');
const modalBody    = document.getElementById('modal-body');
const inqForm      = document.getElementById('inquiry-form');
const inqDomainSel = document.getElementById('inq-domain');
const formFeedback = document.getElementById('form-feedback');
const yearSpan     = document.getElementById('year');

// ---- State ----
let allDomains = [];

// ---- Init ----
document.addEventListener('DOMContentLoaded', () => {
  yearSpan.textContent = new Date().getFullYear();
  fetchDomains();
  bindEvents();
});

// ---- Fetch & Render ----
async function fetchDomains(params = {}) {
  const url = buildUrl(params);
  try {
    const res  = await fetch(url);
    const data = await res.json();
    allDomains = data.domains || [];
    renderGrid(allDomains);
    populateInquiryDropdown(allDomains);
  } catch (err) {
    grid.innerHTML = '<p class="empty-state">⚠️ Failed to load domains. Please try again.</p>';
    console.error('Fetch error:', err);
  }
}

function buildUrl(params) {
  const hasSearchParams =
    params.q || params.category || params.minPrice !== undefined || params.maxPrice !== undefined;

  if (!hasSearchParams) {
    return API_BASE;
  }

  const qs = new URLSearchParams();
  if (params.q)        qs.set('q',        params.q);
  if (params.category) qs.set('category', params.category);
  if (params.minPrice !== undefined && params.minPrice !== '') qs.set('minPrice', params.minPrice);
  if (params.maxPrice !== undefined && params.maxPrice !== '') qs.set('maxPrice', params.maxPrice);
  return `${API_BASE}/search?${qs.toString()}`;
}

function renderGrid(domains) {
  resultCount.textContent = `${domains.length} domain${domains.length !== 1 ? 's' : ''} found`;

  if (domains.length === 0) {
    grid.innerHTML = '<p class="empty-state">😕 No domains match your search. Try different filters.</p>';
    return;
  }

  grid.innerHTML = domains.map(domainCardHTML).join('');

  grid.querySelectorAll('.domain-card').forEach((card) => {
    card.addEventListener('click', () => {
      const id = parseInt(card.dataset.id, 10);
      openModal(id);
    });
  });
}

function domainCardHTML(domain) {
  return `
    <div class="domain-card" data-id="${domain.id}" tabindex="0" role="button" aria-label="View details for ${escapeHtml(domain.name)}">
      <span class="domain-card-name">${escapeHtml(domain.name)}</span>
      <span class="domain-card-category">${escapeHtml(domain.category)}</span>
      <p class="domain-card-desc">${escapeHtml(domain.description)}</p>
      <div class="domain-card-footer">
        <span class="domain-card-price">$${domain.price.toLocaleString()}</span>
        <button class="btn-details">View Details</button>
      </div>
    </div>`;
}

function populateInquiryDropdown(domains) {
  inqDomainSel.innerHTML = '<option value="">— Select a domain —</option>';
  domains.forEach((d) => {
    const opt = document.createElement('option');
    opt.value = d.id;
    opt.textContent = `${d.name} — $${d.price.toLocaleString()}`;
    inqDomainSel.appendChild(opt);
  });
}

// ---- Modal ----
function openModal(id) {
  const domain = allDomains.find((d) => d.id === id);
  if (!domain) return;

  modalBody.innerHTML = `
    <h3 id="modal-title">${escapeHtml(domain.name)}</h3>
    <span class="badge">${escapeHtml(domain.category)}</span>
    <p>${escapeHtml(domain.description)}</p>
    <div class="modal-price">$${domain.price.toLocaleString()}</div>
    <button class="btn-inquire" data-id="${domain.id}">Make an Inquiry</button>`;

  modalBody.querySelector('.btn-inquire').addEventListener('click', () => {
    closeModal();
    selectDomainInForm(domain.id);
    document.getElementById('contact-section').scrollIntoView({ behavior: 'smooth' });
  });

  modalOverlay.removeAttribute('hidden');
  modalClose.focus();
}

function closeModal() {
  modalOverlay.setAttribute('hidden', '');
}

function selectDomainInForm(id) {
  inqDomainSel.value = id;
}

// ---- Form Submission ----
async function handleFormSubmit(e) {
  e.preventDefault();
  clearFeedback();

  const name     = document.getElementById('inq-name').value.trim();
  const email    = document.getElementById('inq-email').value.trim();
  const domainId = inqDomainSel.value;
  const message  = document.getElementById('inq-message').value.trim();

  if (!name || !email || !domainId) {
    showFeedback('Please fill in all required fields.', 'error');
    return;
  }

  const submitBtn = inqForm.querySelector('.btn-submit');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Sending…';

  try {
    const res  = await fetch(`${API_BASE}/contact`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, domainId, message }),
    });
    const data = await res.json();

    if (!res.ok) {
      showFeedback(data.error || 'Something went wrong. Please try again.', 'error');
    } else {
      showFeedback(data.message, 'success');
      inqForm.reset();
    }
  } catch (err) {
    showFeedback('Network error. Please check your connection and try again.', 'error');
    console.error('Form submit error:', err);
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = 'Send Inquiry';
  }
}

// ---- Events ----
function bindEvents() {
  searchBtn.addEventListener('click', runSearch);
  searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') runSearch();
  });

  filterBtn.addEventListener('click', runFilter);
  resetBtn.addEventListener('click', () => {
    searchInput.value  = '';
    filterCat.value    = '';
    filterMin.value    = '';
    filterMax.value    = '';
    fetchDomains();
  });

  modalClose.addEventListener('click', closeModal);
  modalOverlay.addEventListener('click', (e) => {
    if (e.target === modalOverlay) closeModal();
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeModal();
  });

  inqForm.addEventListener('submit', handleFormSubmit);
}

function runSearch() {
  const q = searchInput.value.trim();
  fetchDomains({ q });
}

function runFilter() {
  const params = {};
  const q        = searchInput.value.trim();
  const category = filterCat.value;
  const minPrice = filterMin.value;
  const maxPrice = filterMax.value;

  if (q)        params.q        = q;
  if (category) params.category = category;
  if (minPrice) params.minPrice = minPrice;
  if (maxPrice) params.maxPrice = maxPrice;

  fetchDomains(params);
}

// ---- Helpers ----
function escapeHtml(str) {
  const map = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' };
  return String(str).replace(/[&<>"']/g, (c) => map[c] || c);
}

function showFeedback(msg, type) {
  formFeedback.textContent = msg;
  formFeedback.className   = `form-feedback ${type}`;
}

function clearFeedback() {
  formFeedback.textContent = '';
  formFeedback.className   = 'form-feedback';
}
