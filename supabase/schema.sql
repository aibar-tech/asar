-- ASAR database schema for Supabase (Postgres).
-- Run once in the Supabase dashboard: SQL Editor → New query → paste → Run.
--
-- Security model
--   * Every table has row-level security (RLS) on.
--   * Data is only readable/writable by sessions that passed BOTH factors:
--       1. password  (proved by a row in login_proofs, which can only be
--                     written by a session that signed in with a password)
--       2. email code (the current session was created by a one-time code)
--     or by a session created by confirming the sign-up code.
--     public.is_two_factor() checks this on the server, so skipping a step in
--     the browser does not give access to any data.

create extension if not exists pgcrypto;

-- ---------------------------------------------------------------- tables

create table if not exists public.profiles (
  id         uuid primary key references auth.users (id) on delete cascade,
  name       text not null check (char_length(name) between 1 and 80),
  created_at timestamptz not null default now()
);

create table if not exists public.tasks (
  id                uuid primary key default gen_random_uuid(),
  requester_id      uuid references public.profiles (id) on delete cascade default auth.uid(),
  org_name          text,                -- seeded community organisations (no account)
  title             text not null check (char_length(title) between 4 and 140),
  description       text not null check (char_length(description) between 15 and 2000),
  title_i18n        jsonb,               -- optional {"en","ru","kk"} for seeded rows
  description_i18n  jsonb,
  category          text not null check (category in ('elderly','moving','eco','education','food','animals','events','tech')),
  area              text not null check (area in ('almaly','bostandyk','medeu','auezov','zhetysu','turksib','nauryzbay','alatau')),
  starts_at         timestamptz not null,
  duration_h        text not null default '2',
  volunteers_needed int not null check (volunteers_needed between 1 and 30),
  skills            text[] not null default '{}',
  difficulty        text not null default 'moderate' check (difficulty in ('easy','moderate','demanding')),
  completed         boolean not null default false,
  created_at        timestamptz not null default now(),
  check (requester_id is not null or org_name is not null)
);

create table if not exists public.applications (
  id           uuid primary key default gen_random_uuid(),
  task_id      uuid not null references public.tasks (id) on delete cascade,
  volunteer_id uuid not null references public.profiles (id) on delete cascade default auth.uid(),
  message      text not null default '' check (char_length(message) <= 600),
  status       text not null default 'pending' check (status in ('pending','accepted','rejected')),
  created_at   timestamptz not null default now(),
  unique (task_id, volunteer_id)
);

create table if not exists public.reviews (
  id           uuid primary key default gen_random_uuid(),
  task_id      uuid not null references public.tasks (id) on delete cascade,
  volunteer_id uuid not null references public.profiles (id) on delete cascade,
  author_id    uuid not null references public.profiles (id) on delete cascade default auth.uid(),
  rating       int not null check (rating between 1 and 5),
  text         text not null default '' check (char_length(text) <= 1000),
  verification jsonb,                    -- result of the simulated plausibility check
  created_at   timestamptz not null default now(),
  unique (task_id, volunteer_id)
);

-- One row per successful password check. Written by the browser right after
-- signInWithPassword; the insert policy only accepts password sessions.
create table if not exists public.login_proofs (
  id         bigint generated always as identity primary key,
  user_id    uuid not null references auth.users (id) on delete cascade default auth.uid(),
  created_at timestamptz not null default now()
);

create index if not exists applications_task_idx on public.applications (task_id);
create index if not exists reviews_task_idx on public.reviews (task_id);
create index if not exists login_proofs_user_idx on public.login_proofs (user_id, created_at desc);

-- ---------------------------------------------------------------- profile on sign-up

create or replace function public.handle_new_user()
returns trigger language plpgsql security definer set search_path = public as $$
begin
  insert into public.profiles (id, name)
  values (new.id, coalesce(nullif(trim(new.raw_user_meta_data ->> 'name'), ''), split_part(new.email, '@', 1)))
  on conflict (id) do nothing;
  return new;
end $$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
  after insert on auth.users
  for each row execute function public.handle_new_user();

-- ---------------------------------------------------------------- two-factor check

-- Time the current session was created by an emailed code (null if it wasn't).
create or replace function public.code_session_at()
returns timestamptz language sql stable set search_path = public as $$
  select to_timestamp(max((e ->> 'timestamp')::bigint))
  from jsonb_array_elements(coalesce(auth.jwt() -> 'amr', '[]'::jsonb)) e
  where e ->> 'method' in ('otp', 'magiclink', 'email/signup');
$$;

create or replace function public.is_two_factor()
returns boolean language sql stable security definer set search_path = public as $$
  select auth.uid() is not null
     and public.code_session_at() is not null
     and (
       -- login: a password check for this user shortly before the code was used
       exists (
         select 1 from public.login_proofs p
         where p.user_id = auth.uid()
           and p.created_at between public.code_session_at() - interval '15 minutes'
                                and public.code_session_at() + interval '1 minute'
       )
       -- sign-up: this code session is the one that confirmed the new account
       or exists (
         select 1 from auth.users u
         where u.id = auth.uid()
           and u.email_confirmed_at between public.code_session_at() - interval '2 minutes'
                                        and public.code_session_at() + interval '2 minutes'
       )
     );
$$;

-- ---------------------------------------------------------------- row-level security

alter table public.profiles     enable row level security;
alter table public.tasks        enable row level security;
alter table public.applications enable row level security;
alter table public.reviews      enable row level security;
alter table public.login_proofs enable row level security;

-- profiles
drop policy if exists "profiles read" on public.profiles;
create policy "profiles read" on public.profiles for select to authenticated
  using (public.is_two_factor());
drop policy if exists "profiles update own" on public.profiles;
create policy "profiles update own" on public.profiles for update to authenticated
  using (id = auth.uid() and public.is_two_factor())
  with check (id = auth.uid());

-- tasks
drop policy if exists "tasks read" on public.tasks;
create policy "tasks read" on public.tasks for select to authenticated
  using (public.is_two_factor());
drop policy if exists "tasks insert own" on public.tasks;
create policy "tasks insert own" on public.tasks for insert to authenticated
  with check (requester_id = auth.uid() and org_name is null and completed = false and public.is_two_factor());
drop policy if exists "tasks update own" on public.tasks;
create policy "tasks update own" on public.tasks for update to authenticated
  using (requester_id = auth.uid() and public.is_two_factor())
  with check (requester_id = auth.uid());
drop policy if exists "tasks delete own" on public.tasks;
create policy "tasks delete own" on public.tasks for delete to authenticated
  using (requester_id = auth.uid() and public.is_two_factor());

-- applications
drop policy if exists "applications read" on public.applications;
create policy "applications read" on public.applications for select to authenticated
  using (public.is_two_factor());
drop policy if exists "applications apply" on public.applications;
create policy "applications apply" on public.applications for insert to authenticated
  with check (
    volunteer_id = auth.uid() and status = 'pending' and public.is_two_factor()
    and exists (select 1 from public.tasks t where t.id = task_id and not t.completed)
  );
-- Only the task's requester may change an application, and only its status.
drop policy if exists "applications decide" on public.applications;
create policy "applications decide" on public.applications for update to authenticated
  using (public.is_two_factor() and exists (select 1 from public.tasks t where t.id = task_id and t.requester_id = auth.uid()))
  with check (exists (select 1 from public.tasks t where t.id = task_id and t.requester_id = auth.uid()));
revoke update on public.applications from authenticated;
grant update (status) on public.applications to authenticated;
-- Volunteers may withdraw their own pending application.
drop policy if exists "applications withdraw" on public.applications;
create policy "applications withdraw" on public.applications for delete to authenticated
  using (volunteer_id = auth.uid() and status = 'pending' and public.is_two_factor());

-- A requester cannot accept more volunteers than the task needs.
create or replace function public.check_team_size()
returns trigger language plpgsql security definer set search_path = public as $$
begin
  if new.status = 'accepted' and old.status is distinct from 'accepted' then
    if (select count(*) from public.applications a where a.task_id = new.task_id and a.status = 'accepted')
       >= (select volunteers_needed from public.tasks where id = new.task_id) then
      raise exception 'team_full';
    end if;
  end if;
  return new;
end $$;
drop trigger if exists applications_team_size on public.applications;
create trigger applications_team_size before update on public.applications
  for each row execute function public.check_team_size();

-- reviews: written by the requester, about an accepted volunteer
drop policy if exists "reviews read" on public.reviews;
create policy "reviews read" on public.reviews for select to authenticated
  using (public.is_two_factor());
drop policy if exists "reviews write" on public.reviews;
create policy "reviews write" on public.reviews for insert to authenticated
  with check (
    author_id = auth.uid() and public.is_two_factor()
    and exists (select 1 from public.tasks t where t.id = task_id and t.requester_id = auth.uid())
    and exists (select 1 from public.applications a where a.task_id = reviews.task_id and a.volunteer_id = reviews.volunteer_id and a.status = 'accepted')
  );

-- login proofs: only a password session may record one, for itself
drop policy if exists "login proof insert" on public.login_proofs;
create policy "login proof insert" on public.login_proofs for insert to authenticated
  with check (
    user_id = auth.uid()
    and coalesce(auth.jwt() -> 'amr', '[]'::jsonb) @> '[{"method":"password"}]'::jsonb
  );

grant usage on schema public to authenticated;
grant select, insert, delete on public.profiles, public.tasks, public.applications, public.reviews to authenticated;
grant update on public.profiles, public.tasks to authenticated;
grant insert on public.login_proofs to authenticated;
grant execute on function public.is_two_factor(), public.code_session_at() to authenticated;
