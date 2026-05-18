# ChaosPilot Frontend

This folder contains the Day 7 React UI for ChaosPilot.

## Setup

```bash
cd frontend/chaos-ui
npm install
```

## Run

```bash
npm run dev
```

The app will start on `http://localhost:3000` and proxy `/api` requests to `http://localhost:8080`.

## Build

```bash
npm run build
```

## Deploy to GitHub Pages

This repository includes a GitHub Actions workflow that builds the frontend and publishes the production output to the `gh-pages` branch.

The site will be available at:

- `https://Priyansh-6216.github.io/ChaosPilot/`

If you want to deploy manually, run:

```bash
npm run build
```
```
