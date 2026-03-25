const express = require('express');
const router = express.Router();
const fs = require('fs');
const path = require('path');

const DATA_FILE = path.join(__dirname, '..', 'data', 'domains.json');

// Load domain data once at startup to avoid blocking the event loop on every request.
let domainsCache = null;

function loadDomains() {
  if (!domainsCache) {
    const raw = fs.readFileSync(DATA_FILE, 'utf-8');
    domainsCache = JSON.parse(raw);
  }
  return domainsCache;
}

// GET /api/domains/search  — must be registered before /:id to avoid route conflict
router.get('/search', (req, res) => {
  const { q, category, minPrice, maxPrice } = req.query;
  let domains = loadDomains().filter((d) => d.available);

  if (q) {
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

  if (minPrice !== undefined) {
    const min = parseFloat(minPrice);
    if (!isNaN(min)) {
      domains = domains.filter((d) => d.price >= min);
    }
  }

  if (maxPrice !== undefined) {
    const max = parseFloat(maxPrice);
    if (!isNaN(max)) {
      domains = domains.filter((d) => d.price <= max);
    }
  }

  res.json({ count: domains.length, domains });
});

// GET /api/domains — list all available domains
router.get('/', (req, res) => {
  const domains = loadDomains().filter((d) => d.available);
  res.json({ count: domains.length, domains });
});

// GET /api/domains/:id — get a single domain by id
router.get('/:id', (req, res) => {
  const id = parseInt(req.params.id, 10);
  if (isNaN(id)) {
    return res.status(400).json({ error: 'Invalid domain id.' });
  }

  const domain = loadDomains().find((d) => d.id === id);
  if (!domain) {
    return res.status(404).json({ error: 'Domain not found.' });
  }

  res.json(domain);
});

// POST /api/domains/contact — submit a purchase inquiry
router.post('/contact', (req, res) => {
  const { name, email, domainId, message } = req.body;

  if (!name || !email || !domainId) {
    return res
      .status(400)
      .json({ error: 'name, email, and domainId are required.' });
  }

  const emailRegex = /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/;
  if (!emailRegex.test(email)) {
    return res.status(400).json({ error: 'Invalid email address.' });
  }

  const domain = loadDomains().find((d) => d.id === parseInt(domainId, 10));
  if (!domain) {
    return res.status(404).json({ error: 'Domain not found.' });
  }

  // In a production app you would persist the inquiry or send an email here.
  console.log(
    `Inquiry received: ${name} <${email}> is interested in "${domain.name}" — "${message || '(no message)'}"`
  );

  res.status(201).json({
    success: true,
    message: `Thank you, ${name}! Your inquiry for "${domain.name}" has been received. We will contact you at ${email} shortly.`,
  });
});

module.exports = router;
