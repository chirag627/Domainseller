const express = require('express');
const cors = require('cors');
const rateLimit = require('express-rate-limit');
const path = require('path');

const domainsRouter = require('./routes/domains');

const app = express();
const PORT = process.env.PORT || 3000;

// ---- Rate limiters ----

// Strict limiter for API routes
const apiLimiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 minutes
  max: 100,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: 'You have made too many requests. Please wait a few minutes and try again.' },
});

// More relaxed limiter for serving the front-end pages
const staticLimiter = rateLimit({
  windowMs: 15 * 60 * 1000,
  max: 500,
  standardHeaders: true,
  legacyHeaders: false,
  message: { error: 'Too many page requests. Please wait a moment and refresh.' },
});

// ---- Core middleware ----
app.use(cors());
app.use(express.json());
app.use(express.urlencoded({ extended: false }));

// ---- Static files ----
app.use(express.static(path.join(__dirname, 'public')));

// ---- API routes ----
app.use('/api/domains', apiLimiter, domainsRouter);

// 404 for any other /api/* path (catches typos or unsupported endpoints)
app.use('/api', (req, res) => {
  res.status(404).json({ error: 'The API endpoint you requested does not exist.' });
});

// ---- SPA catch-all ----
app.get('*', staticLimiter, (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// ---- Global error handler ----
// Must be defined with four parameters so Express recognises it as an error handler.
// eslint-disable-next-line no-unused-vars
app.use((err, req, res, next) => {
  console.error('Unhandled server error:', err);

  // Always respond with JSON for API requests; fall back to the SPA for page requests.
  if (req.path.startsWith('/api/')) {
    const status = err.status || err.statusCode || 500;
    const message =
      status >= 500
        ? 'Something went wrong on our end. Please try again later.'
        : err.message || 'An unexpected error occurred. Please try again.';
    res.status(status).json({ error: message });
  } else {
    res.status(500).sendFile(path.join(__dirname, 'public', 'index.html'));
  }
});

// ---- Start server ----
const server = app.listen(PORT, () => {
  console.log(`Domainseller server running at http://localhost:${PORT}`);
});

server.on('error', (err) => {
  if (err.code === 'EADDRINUSE') {
    console.error(
      `Port ${PORT} is already in use. ` +
      'Please stop the other process or set a different PORT environment variable.'
    );
  } else {
    console.error('Server failed to start:', err.message);
  }
  process.exit(1);
});

module.exports = app;
