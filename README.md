# Domainseller

A domain marketplace web application where users can browse, search, and inquire about premium domain names for sale.

## Features

- Browse a curated list of available domain names
- Search domains by name, category, or price range
- View domain details (price, category, description)
- Contact the seller to make an offer or purchase a domain
- RESTful JSON API for domain listings

## Tech Stack

- **Backend**: Node.js + Express
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Data**: JSON file-based domain inventory

## Project Structure

```
Domainseller/
├── server.js          # Express server entry point
├── package.json       # Node.js project metadata & dependencies
├── data/
│   └── domains.json   # Domain inventory data
├── routes/
│   └── domains.js     # Domain API routes
└── public/
    ├── index.html     # Main frontend page
    ├── css/
    │   └── style.css  # Stylesheet
    └── js/
        └── main.js    # Frontend JavaScript
```

## Getting Started

### Prerequisites

- [Node.js](https://nodejs.org/) v14 or higher
- npm (bundled with Node.js)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/chirag627/Domainseller.git
   cd Domainseller
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the server**
   ```bash
   npm start
   ```

4. **Open in your browser**
   ```
   http://localhost:3000
   ```

### Development mode (auto-restart on file changes)

```bash
npm run dev
```

## API Endpoints

| Method | Endpoint              | Description                        |
|--------|-----------------------|------------------------------------|
| GET    | `/api/domains`        | List all available domains         |
| GET    | `/api/domains/:id`    | Get details of a single domain     |
| GET    | `/api/domains/search` | Search domains by query parameters |
| POST   | `/api/domains/contact`| Submit a purchase inquiry          |

### Query Parameters for Search

| Parameter  | Type   | Description                        |
|------------|--------|------------------------------------|
| `q`        | string | Search by domain name or keyword   |
| `category` | string | Filter by category (e.g. `tech`)   |
| `minPrice` | number | Minimum price filter               |
| `maxPrice` | number | Maximum price filter               |

## License

MIT
