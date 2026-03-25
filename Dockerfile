# Use official Node.js LTS Alpine image for a small footprint
FROM node:18-alpine

# Set working directory
WORKDIR /app

# Copy dependency manifests first to leverage Docker layer caching
COPY package*.json ./

# Install only production dependencies
RUN npm ci --omit=dev

# Copy application source code
COPY . .

# Ensure the non-root node user owns all application files
RUN chown -R node:node /app

# Expose the application port
EXPOSE 3000

# Run as non-root user for security
USER node

# Start the server
CMD ["node", "server.js"]
