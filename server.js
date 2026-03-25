const express = require('express');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
const path = require('path');

const domainsRouter = require('./routes/domains');

const app = express();
const PORT = process.env.PORT || 3000;

// Rate limiter — applies to all API routes
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100,                  // max 100 requests per window per IP
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: 'Too many requests. Please try again later.' },
});

// Rate limiter — applies to static/page routes
const staticLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 500,
  standardHeaders: true,
  legacyHeaders: false,
});

// Middleware
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// Serve static frontend files
app.use(express.static(path.join(__dirname, 'public')));

// API routes (rate-limited)
app.use('/api/domains', apiLimiter, domainsRouter);

// Catch-all: serve the SPA entry point for any non-API route (rate-limited)
app.get('*', staticLimiter, (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, () => {
  console.log(`Domainseller server running at http://localhost:${PORT}`);
});

module.exports = app;
