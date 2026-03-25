const express = require('express');
const router = express.Router();
const fs = require('fs');
const path = require('path');

const DATA_FILE = path.join(__dirname, '..', 'data', 'domains.json');

// Input length limits
const MAX_NAME_LEN    = 100;
const MAX_EMAIL_LEN   = 254;
const MAX_MESSAGE_LEN = 1000;

// Load domain data once at startup; cache it for all subsequent requests.
let domainsCache = null;

function loadDomains() {
  if (!domainsCache) {
    let raw;
    try {
      raw = fs.readFileSync(DATA_FILE, 'utf-8');
    } catch {
      throw Object.assign(
        new Error('Domain data is temporarily unavailable. Please try again later.'),
        { status: 503 }
      );
    }

    try {
      domainsCache = JSON.parse(raw);
    } catch {
      throw Object.assign(
        new Error('Domain data is temporarily unavailable. Please try again later.'),
        { status: 503 }
      );
    }
  }
  return domainsCache;
}

// Helper — wraps an async route handler and forwards any thrown errors to the global
// Express error handler instead of leaving them as unhandled promise rejections.
function asyncHandler(fn) {
  return (req, res, next) => Promise.resolve(fn(req, res, next)).catch(next);
}

// ── GET /api/domains/search ──────────────────────────────────────────────────
// Must be registered before /:id to avoid route conflict.
router.get('/search', asyncHandler((req, res) => {
  const { q, category, minPrice, maxPrice } = req.query;
  let domains = loadDomains().filter((d) => d.available);

  if (q) {
    if (typeof q !== 'string' || q.length > 200) {
      return res.status(400).json({ error: 'Search term is too long. Please use fewer characters.' });
    }
    const term = q.toLowerCase();
    domains = domains.filter(
      (d) =>
        d.name.toLowerCase().includes(term) ||
        d.description.toLowerCase().includes(term)
    );
  }

  if (category) {
    domains = domains.filter(
      (d) => d.category.toLowerCase() === category.toLowerCase()
    );
  }

  const minDefined = minPrice !== undefined;
  const maxDefined = maxPrice !== undefined;
  const min = minDefined ? parseFloat(minPrice) : NaN;
  const max = maxDefined ? parseFloat(maxPrice) : NaN;

  if (minDefined) {
    if (isNaN(min) || min < 0) {
      return res.status(400).json({ error: 'Minimum price must be a positive number.' });
    }
    domains = domains.filter((d) => d.price >= min);
  }

  if (maxDefined) {
    if (isNaN(max) || max < 0) {
      return res.status(400).json({ error: 'Maximum price must be a positive number.' });
    }
    domains = domains.filter((d) => d.price <= max);
  }

  if (minDefined && maxDefined && !isNaN(min) && !isNaN(max) && min > max) {
    return res.status(400).json({
      error: 'Minimum price cannot be greater than the maximum price.',
    });
  }

  res.json({ count: domains.length, domains });
}));

// ── GET /api/domains ─────────────────────────────────────────────────────────
router.get('/', asyncHandler((req, res) => {
  const domains = loadDomains().filter((d) => d.available);
  res.json({ count: domains.length, domains });
}));

// ── GET /api/domains/:id ─────────────────────────────────────────────────────
router.get('/:id', asyncHandler((req, res) => {
  const id = parseInt(req.params.id, 10);
  if (isNaN(id) || id <= 0) {
    return res.status(400).json({ error: 'Please provide a valid domain ID.' });
  }

  const domain = loadDomains().find((d) => d.id === id);
  if (!domain) {
    return res.status(404).json({ error: 'We could not find the domain you are looking for.' });
  }

  res.json(domain);
}));

// ── POST /api/domains/contact ─────────────────────────────────────────────────
router.post('/contact', asyncHandler((req, res) => {
  const { name, email, domainId, message } = req.body || {};

  // Required field presence
  if (!name || !email || !domainId) {
    return res.status(400).json({
      error: 'Please provide your name, email address, and select a domain before submitting.',
    });
  }

  // Type checks (protect against non-string values)
  if (typeof name !== 'string' || typeof email !== 'string') {
    return res.status(400).json({ error: 'Invalid input. Please check your details and try again.' });
  }

  const trimmedName    = name.trim();
  const trimmedEmail   = email.trim();
  const trimmedMessage = typeof message === 'string' ? message.trim() : '';

  // Length limits
  if (trimmedName.length === 0) {
    return res.status(400).json({ error: 'Your name cannot be empty.' });
  }
  if (trimmedName.length > MAX_NAME_LEN) {
    return res.status(400).json({
      error: `Your name is too long. Please keep it under ${MAX_NAME_LEN} characters.`,
    });
  }
  if (trimmedEmail.length > MAX_EMAIL_LEN) {
    return res.status(400).json({
      error: 'The email address you entered is too long. Please use a shorter email.',
    });
  }
  if (trimmedMessage.length > MAX_MESSAGE_LEN) {
    return res.status(400).json({
      error: `Your message is too long. Please keep it under ${MAX_MESSAGE_LEN} characters.`,
    });
  }

  // Email format
  const emailRegex = /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(trimmedEmail)) {
    return res.status(400).json({
      error: 'The email address you entered does not appear to be valid. Please check it and try again.',
    });
  }

  // Domain lookup
  const parsedId = parseInt(domainId, 10);
  if (isNaN(parsedId) || parsedId <= 0) {
    return res.status(400).json({ error: 'Please select a valid domain from the list.' });
  }

  const domain = loadDomains().find((d) => d.id === parsedId);
  if (!domain) {
    return res.status(404).json({
      error: 'The selected domain could not be found. Please refresh the page and try again.',
    });
  }

  if (!domain.available) {
    return res.status(409).json({
      error: `Sorry, "${domain.name}" is no longer available. Please choose another domain.`,
    });
  }

  // In production, you would send a notification email or persist the inquiry here.
  console.log(
    `Inquiry: ${trimmedName} <${trimmedEmail}> is interested in "${domain.name}"` +
    (trimmedMessage ? ` — "${trimmedMessage}"` : ' — (no message)')
  );

  res.status(201).json({
    success: true,
    message:
      `Thank you, ${trimmedName}! Your inquiry for "${domain.name}" has been received. ` +
      `We will get back to you at ${trimmedEmail} shortly.`,
  });
}));

module.exports = router;
