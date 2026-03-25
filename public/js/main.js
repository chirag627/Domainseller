/* =========================================================
   DomainSeller — Frontend JavaScript
   ========================================================= */

const API_BASE        = '/api/domains';
const MAX_MESSAGE_LEN = 1000; // must match MAX_MESSAGE_LEN in routes/domains.js

// Email validation regex — kept in sync with the server-side pattern
const EMAIL_REGEX = /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/;

// ---- DOM references ----
const grid          = document.getElementById('domain-grid');
const resultCount   = document.getElementById('result-count');
const searchInput   = document.getElementById('search-input');
const searchBtn     = document.getElementById('search-btn');
const filterCat     = document.getElementById('filter-category');
const filterMin     = document.getElementById('filter-min');
const filterMax     = document.getElementById('filter-max');
const filterBtn     = document.getElementById('filter-btn');
const resetBtn      = document.getElementById('reset-btn');
const filterError   = document.getElementById('filter-error');
const modalOverlay  = document.getElementById('modal-overlay');
const modalClose    = document.getElementById('modal-close');
const modalBody     = document.getElementById('modal-body');
const inqForm       = document.getElementById('inquiry-form');
const inqDomainSel  = document.getElementById('inq-domain');
const formFeedback  = document.getElementById('form-feedback');
const yearSpan      = document.getElementById('year');
const msgTextarea   = document.getElementById('inq-message');
const msgCounter    = document.getElementById('msg-counter');

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
  grid.setAttribute('aria-busy', 'true');
  const url = buildUrl(params);
  try {
    const res = await fetch(url);

    // Handle HTTP-level errors (rate-limit, server error, etc.)
    if (!res.ok) {
      let errMsg = 'Unable to load domains right now. Please try again later.';
      if (res.status === 429) {
        errMsg = 'You have made too many requests. Please wait a few minutes and try again.';
      } else if (res.status >= 500) {
        errMsg = 'A server error occurred. Please try again in a moment.';
      } else if (res.status === 400) {
        // Show the server's validation message if available
        try {
          const data = await res.json();
          errMsg = data.error || errMsg;
        } catch { /* ignore parse errors */ }
      }
      showGridError(errMsg);
      return;
    }

    const data = await res.json();
    allDomains = data.domains || [];
    renderGrid(allDomains);
    populateInquiryDropdown(allDomains);
  } catch (err) {
    // Network-level failure (offline, DNS error, etc.)
    showGridError('Could not connect to the server. Please check your internet connection and try again.');
    console.error('Fetch error:', err);
  } finally {
    grid.setAttribute('aria-busy', 'false');
  }
}

function showGridError(message) {
  resultCount.textContent = '';
  grid.innerHTML = `<p class="empty-state">⚠️ ${escapeHtml(message)}</p>`;
}

function buildUrl(params) {
  const hasSearchParams =
    params.q ||
    params.category ||
    params.minPrice !== undefined ||
    params.maxPrice !== undefined;

  if (!hasSearchParams) {
    return API_BASE;
  }

  const qs = new URLSearchParams();
  if (params.q)                                                        qs.set('q',        params.q);
  if (params.category)                                                  qs.set('category', params.category);
  if (params.minPrice !== undefined && params.minPrice !== '')          qs.set('minPrice', params.minPrice);
  if (params.maxPrice !== undefined && params.maxPrice !== '')          qs.set('maxPrice', params.maxPrice);
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
    // Mouse click
    card.addEventListener('click', () => {
      const id = parseInt(card.dataset.id, 10);
      openModal(id);
    });

    // Keyboard: Enter or Space activates the card (accessibility)
    card.addEventListener('keydown', (e) => {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        const id = parseInt(card.dataset.id, 10);
        openModal(id);
      }
    });
  });
}

function domainCardHTML(domain) {
  return `
    <div
      class="domain-card"
      data-id="${domain.id}"
      tabindex="0"
      role="button"
      aria-label="View details for ${escapeHtml(domain.name)}, priced at $${domain.price.toLocaleString()}"
    >
      <span class="domain-card-name">${escapeHtml(domain.name)}</span>
      <span class="domain-card-category">${escapeHtml(domain.category)}</span>
      <p class="domain-card-desc">${escapeHtml(domain.description)}</p>
      <div class="domain-card-footer">
        <span class="domain-card-price">$${domain.price.toLocaleString()}</span>
        <button class="btn-details" tabindex="-1" aria-hidden="true">View Details</button>
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
  const message  = msgTextarea.value.trim();

  // Client-side validation
  if (!name) {
    showFeedback('Please enter your name.', 'error');
    document.getElementById('inq-name').focus();
    return;
  }

  if (!email) {
    showFeedback('Please enter your email address.', 'error');
    document.getElementById('inq-email').focus();
    return;
  }

  // Basic email format check
  if (!EMAIL_REGEX.test(email)) {
    showFeedback('The email address you entered does not look valid. Please check it and try again.', 'error');
    document.getElementById('inq-email').focus();
    return;
  }

  if (!domainId) {
    showFeedback('Please select a domain from the list.', 'error');
    inqDomainSel.focus();
    return;
  }

  const submitBtn = inqForm.querySelector('.btn-submit');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Sending…';

  try {
    const res = await fetch(`${API_BASE}/contact`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, email, domainId, message }),
    });

    let data = {};
    try {
      data = await res.json();
    } catch { /* response had no JSON body */ }

    if (!res.ok) {
      let errMsg = 'Something went wrong. Please try again.';
      if (res.status === 429) {
        errMsg = 'You have sent too many inquiries. Please wait a few minutes before trying again.';
      } else if (res.status >= 500) {
        errMsg = 'A server error occurred. Please try again later.';
      } else {
        errMsg = data.error || errMsg;
      }
      showFeedback(errMsg, 'error');
    } else {
      showFeedback(
        data.message || 'Your inquiry has been sent! We will get back to you shortly.',
        'success'
      );
      inqForm.reset();
      updateCharCounter(); // reset counter after form reset
    }
  } catch (err) {
    showFeedback('Could not connect to the server. Please check your internet connection and try again.', 'error');
    console.error('Form submit error:', err);
  } finally {
    submitBtn.disabled = false;
    submitBtn.textContent = 'Send Inquiry';
  }
}

// ---- Events ----
function bindEvents() {
  // Search
  searchBtn.addEventListener('click', runSearch);
  searchInput.addEventListener('keydown', (e) => {
    if (e.key === 'Enter') runSearch();
  });

  // Filter
  filterBtn.addEventListener('click', runFilter);

  // Reset
  resetBtn.addEventListener('click', () => {
    searchInput.value = '';
    filterCat.value   = '';
    filterMin.value   = '';
    filterMax.value   = '';
    clearFilterError();
    fetchDomains();
  });

  // Modal
  modalClose.addEventListener('click', closeModal);
  modalOverlay.addEventListener('click', (e) => {
    if (e.target === modalOverlay) closeModal();
  });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') closeModal();
  });

  // Form
  inqForm.addEventListener('submit', handleFormSubmit);

  // Character counter for message textarea
  msgTextarea.addEventListener('input', updateCharCounter);
}

function runSearch() {
  const q = searchInput.value.trim();
  clearFilterError();
  fetchDomains({ q });
}

function runFilter() {
  clearFilterError();

  const minVal = filterMin.value;
  const maxVal = filterMax.value;

  // Client-side price range validation
  if (minVal !== '' && maxVal !== '') {
    const min = parseFloat(minVal);
    const max = parseFloat(maxVal);
    if (!isNaN(min) && !isNaN(max) && min > max) {
      showFilterError('Minimum price cannot be greater than the maximum price.');
      return;
    }
  }

  if (minVal !== '' && parseFloat(minVal) < 0) {
    showFilterError('Price cannot be negative.');
    return;
  }
  if (maxVal !== '' && parseFloat(maxVal) < 0) {
    showFilterError('Price cannot be negative.');
    return;
  }

  const params = {};
  const q        = searchInput.value.trim();
  const category = filterCat.value;

  if (q)        params.q        = q;
  if (category) params.category = category;
  if (minVal)   params.minPrice = minVal;
  if (maxVal)   params.maxPrice = maxVal;

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

function showFilterError(msg) {
  filterError.textContent = msg;
}

function clearFilterError() {
  filterError.textContent = '';
}

function updateCharCounter() {
  const len = msgTextarea.value.length;
  msgCounter.textContent = `${len} / ${MAX_MESSAGE_LEN}`;
  msgCounter.classList.toggle('near-limit', len >= MAX_MESSAGE_LEN * 0.9);
}
