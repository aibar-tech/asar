# Connecting ASAR to a real database (Supabase)

Without settings, `index.html` runs in **demo mode**: no server, data lives in the browser tab,
and the email code is shown on screen. Follow these steps to get a real database, real accounts
and real 6-digit codes sent by email. Allow about 15 minutes.

## 1. Create the project

1. Sign up at [supabase.com](https://supabase.com) (free plan is enough) and click **New project**.
2. Pick a name (e.g. `asar`), set a database password (save it), choose a region close to
   Kazakhstan (e.g. *Central EU (Frankfurt)*), and create the project.

## 2. Create the tables and security rules

1. Open **SQL Editor → New query**.
2. Paste the whole of [`supabase/schema.sql`](supabase/schema.sql) and click **Run**.
3. Optional: run [`supabase/seed.sql`](supabase/seed.sql) the same way to add five community
   requests in Kazakh, Russian and English, so the feed isn't empty on day one.

## 3. Turn on email codes

In **Authentication**:

1. **Sign In / Providers → Email**: make sure *Email* is enabled and **Confirm email** is on.
   Set *Email OTP Expiration* to `600` seconds and *Email OTP Length* to `6`.
2. **Emails → Templates**: edit two templates so they contain the code (`{{ .Token }}`)
   instead of only a link.

   **Confirm sign up**. Subject: `Your ASAR code / Код ASAR / ASAR коды`

   ```html
   <h2>ASAR</h2>
   <p>Your code to confirm your account:</p>
   <p style="font-size:28px;font-weight:bold;letter-spacing:6px">{{ .Token }}</p>
   <p>Код для подтверждения аккаунта · Аккаунтты растау коды: <b>{{ .Token }}</b></p>
   <p style="color:#666">The code expires in 10 minutes. If you didn't sign up for ASAR, ignore this email.</p>
   ```

   **Magic link** (used for the second step of sign-in). Subject: `Your ASAR sign-in code / Код для входа / Кіру коды`

   ```html
   <h2>ASAR</h2>
   <p>Your sign-in code:</p>
   <p style="font-size:28px;font-weight:bold;letter-spacing:6px">{{ .Token }}</p>
   <p>Код для входа · Кіру коды: <b>{{ .Token }}</b></p>
   <p style="color:#666">Someone entered your password on ASAR. If it wasn't you, change your password.</p>
   ```

3. **URL Configuration**: set *Site URL* to the address where you host the site (step 5).

### Who can receive the emails

Supabase's built-in email sender only delivers to addresses of your Supabase team members and
allows just a few emails per hour. That's fine for testing with your own address. For anyone
else to sign up, connect your own email provider under **Emails → SMTP Settings**. Free options
include [Resend](https://resend.com) and [Brevo](https://www.brevo.com): create an account, verify
a sending domain or address, and paste the SMTP host, port, user and password there.
**Rate Limits** on the same page controls how many codes can be sent per hour.

## 4. Add your keys

In **Project Settings → API** (or *Data API*), copy:

- **Project URL**, e.g. `https://abcdefghijkl.supabase.co`
- **anon public** key

Paste them into [`config.js`](config.js):

```js
window.ASAR_CONFIG = {
  supabaseUrl: "https://abcdefghijkl.supabase.co",
  supabaseAnonKey: "eyJhbGciOi...",
};
```

The anon key is meant to be public. What each person can read or change is decided by the
rules in `schema.sql`, not by hiding the key. Never put the **service_role** key in this file.

## 5. Put the site online

Upload `index.html` and `config.js` to any static host, or use GitHub Pages (below):

- **Netlify Drop**: open [app.netlify.com/drop](https://app.netlify.com/drop) and drag `index.html` and `config.js` in.
- **GitHub Pages**: this repo is ready for it. In the repo go to **Settings → Pages**, set *Source* to **Deploy from a branch**, pick this branch and **/ (root)**, and save. The site appears at `https://<owner>.github.io/<repo>/` within a minute.
- **Vercel** works too.

Then set that address as the *Site URL* in step 3.

## 6. Check it works

1. Open the site and click **Create account**. You should get an email with a 6-digit code.
2. Enter it: you're signed in and see the requests from the database.
3. Sign out and **Sign in** again: after the password you get a second email with a new code.

## How the two-step sign-in is enforced

| Step | What happens |
| --- | --- |
| Password | `signInWithPassword`. On success the browser records a *login proof*; the database only accepts that record from a session that really signed in with a password. |
| Email code | The password session is dropped and a 6-digit code is emailed (`signInWithOtp`). Entering it opens the real session. |
| Access check | Every table rule calls `is_two_factor()`: the session must come from an email code **and** a password check must have happened in the 15 minutes before it. A new sign-up counts once its email is confirmed with the code. |

So someone who only knows the password can't get in without the inbox, and someone with access
to the inbox but not the password can't read or change any data either.

Not included yet: "forgot password" and changing the email address.
