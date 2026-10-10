# ASAR — volunteer marketplace

**Asar** is the Kazakh tradition of neighbours coming together to help each other. This website
brings it online for Almaty: people **post requests for help**, neighbours **volunteer**, and
anyone can switch between the two roles in one tap.

Available in **Kazakh, Russian and English**. The switcher is in the header; the site starts in
the browser's language and remembers the choice.

## What it does

- **Requests feed** with search, categories (elderly care, moving, eco, education, food, animals,
  events, digital help) and sorting.
- **Create a request** with title, description, category, district, date, time and team size.
- **Apply to help**, then the requester **accepts or declines** volunteers and **marks the task
  completed** with a rating and review.
- **Profile** with rating, completed tasks, hours, history and verified achievements.
- **Two-step sign-in**: password, then a 6-digit code sent by email.
- **ASAR Assist (simulated AI)**: rule-based keyword logic in the browser, not a real AI model.
  It suggests category, difficulty, team size and skills while you write a request, and flags
  implausible reviews (e.g. "one volunteer carried a 500 kg piano alone") for a person to
  double-check, without calling anyone a fraud.

## Files

| File | Purpose |
| --- | --- |
| `index.html` | The whole website: one file, no build step. |
| `config.js` | Supabase URL and public key. Empty = demo mode. |
| `supabase/schema.sql` | Database tables and security rules (row-level security). |
| `supabase/seed.sql` | Optional demo requests in three languages. |
| `SETUP.md` | Step-by-step guide to connect the database, email codes and hosting. |

## Two modes

- **Demo mode** (default, `config.js` empty): no server. Data lives in the browser tab and the
  email code is shown on screen. Just open `index.html`.
- **Connected mode**: a real Supabase (Postgres) database for accounts, requests, applications
  and reviews, with real 6-digit codes sent by email. The database itself enforces both sign-in
  steps, so skipping one in the browser gives no access to data. Setup takes about 15 minutes;
  see [SETUP.md](SETUP.md).

## Put it online with GitHub Pages

In this repo: **Settings → Pages → Source: Deploy from a branch**, choose this branch and
**/ (root)**, then **Save**. The site appears at `https://<owner>.github.io/<repo>/`.
Add that address as the *Site URL* in Supabase (SETUP.md, step 3).

## Demo script (about 2 minutes)

1. **Create account** (or, in demo mode, *Explore with a demo account*).
2. **Create** → tap a quick-start example → *Use suggestions* → *Publish request*.
3. Switch to **Volunteer** → open the request → *Apply to help*.
4. Switch to **Requester** → **My Tasks** → *Accept*.
5. Open the request → *Mark as completed* → rate the volunteer. Tap *Try an unusual claim* to see
   the piano example flagged for verification.
6. **Profile** shows the review, rating and achievement.
