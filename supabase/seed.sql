-- Optional demo content: community requests from organisations in Almaty.
-- Run after schema.sql (SQL Editor → New query → paste → Run). Safe to skip.
-- These rows have no account behind them (requester_id is null, org_name set),
-- so people can browse and apply to them from day one.

insert into public.tasks
  (org_name, title, description, title_i18n, description_i18n, category, area, starts_at, duration_h, volunteers_needed, skills, difficulty)
values
  ('Taza Almaty Initiative',
   'Sairan lake shore cleanup',
   'Join our Saturday cleanup along the Sairan lake promenade. Gloves, bags and water are provided. We will sort plastic for recycling and finish with a group photo.',
   '{"en":"Sairan lake shore cleanup","ru":"Уборка берега озера Сайран","kk":"Сайран көлінің жағасын тазалау"}',
   '{"en":"Join our Saturday cleanup along the Sairan lake promenade. Gloves, bags and water are provided. We will sort plastic for recycling and finish with a group photo.","ru":"Присоединяйтесь к субботней уборке на набережной озера Сайран. Перчатки, мешки и вода предоставляются. Отсортируем пластик на переработку и закончим общим фото.","kk":"Сайран көлі жағалауындағы сенбілікке қосылыңыз. Қолғап, қап және су беріледі. Пластикті қайта өңдеуге сұрыптап, ортақ суретке түсеміз."}',
   'eco', 'auezov', date_trunc('day', now()) + interval '3 days 9 hours', '~4', 12, '{teamwork,waste}', 'moderate'),

  ('Meirim Food Bank',
   'Sort donations at the food bank',
   'Help us sort and pack food parcels for 120 families before the weekend distribution. Training takes 10 minutes on site.',
   '{"en":"Sort donations at the food bank","ru":"Сортировка пожертвований в фудбанке","kk":"Азық-түлік банкінде қайырымдылықты сұрыптау"}',
   '{"en":"Help us sort and pack food parcels for 120 families before the weekend distribution. Training takes 10 minutes on site.","ru":"Помогите рассортировать и упаковать продуктовые наборы для 120 семей перед раздачей на выходных. Обучение на месте — 10 минут.","kk":"Демалыс күнгі таратуға дейін 120 отбасыға арналған азық-түлік себеттерін сұрыптап, орауға көмектесіңіз. Орнында 10 минуттық нұсқаулық беріледі."}',
   'food', 'turksib', date_trunc('day', now()) + interval '4 days 14 hours', '~3', 6, '{org,teamwork}', 'easy'),

  ('City Animal Shelter',
   'Walk dogs at the city shelter',
   'Our shelter dogs need walks and socialisation. Perfect for animal lovers. We will match you with calm dogs first. Please wear comfortable shoes.',
   '{"en":"Walk dogs at the city shelter","ru":"Выгул собак в городском приюте","kk":"Қалалық баспанадағы иттерді серуендету"}',
   '{"en":"Our shelter dogs need walks and socialisation. Perfect for animal lovers. We will match you with calm dogs first. Please wear comfortable shoes.","ru":"Собакам из приюта нужны прогулки и общение. Отлично подойдёт любителям животных. Сначала дадим спокойных собак. Наденьте удобную обувь.","kk":"Баспанадағы иттерге серуен мен қарым-қатынас керек. Жануар сүйетіндерге өте лайық. Алдымен жуас иттерді береміз. Ыңғайлы аяқ киім киіңіз."}',
   'animals', 'nauryzbay', date_trunc('day', now()) + interval '5 days 11 hours', '~2', 4, '{animals,calm}', 'easy'),

  ('Alatau Community Centre',
   'Teach seniors to use a smartphone',
   'A small group of pensioners at the community centre want to learn video calls, messengers and online banking safety. Slides are ready; you just guide them.',
   '{"en":"Teach seniors to use a smartphone","ru":"Научить пенсионеров пользоваться смартфоном","kk":"Зейнеткерлерді смартфон қолдануға үйрету"}',
   '{"en":"A small group of pensioners at the community centre want to learn video calls, messengers and online banking safety. Slides are ready; you just guide them.","ru":"Небольшая группа пенсионеров в общественном центре хочет научиться видеозвонкам, мессенджерам и безопасности онлайн-банкинга. Слайды готовы — нужно только помогать.","kk":"Қоғамдық орталықтағы зейнеткерлер тобы бейне қоңырау, мессенджерлер және онлайн-банкинг қауіпсіздігін үйренгісі келеді. Слайдтар дайын, тек бағыт беру керек."}',
   'tech', 'alatau', date_trunc('day', now()) + interval '2 days 15 hours', '2', 2, '{phones,patience,explain}', 'easy'),

  ('Zhetysu Residents Council',
   'Set up a Nauryz courtyard celebration',
   'Our courtyard is hosting a small Nauryz gathering for neighbours. We need help setting up tables, a yurt-style tent and decorations, then cleaning up afterwards.',
   '{"en":"Set up a Nauryz courtyard celebration","ru":"Подготовить праздник Наурыз во дворе","kk":"Аулада Наурыз мерекесін дайындау"}',
   '{"en":"Our courtyard is hosting a small Nauryz gathering for neighbours. We need help setting up tables, a yurt-style tent and decorations, then cleaning up afterwards.","ru":"Во дворе пройдёт небольшой праздник Наурыз для соседей. Нужна помощь с установкой столов, шатра в стиле юрты и украшений, а потом с уборкой.","kk":"Ауламызда көршілерге арналған шағын Наурыз мерекесі өтеді. Үстелдер, киіз үй тәрізді шатыр мен безендіру орнатуға, кейін жинастыруға көмек керек."}',
   'events', 'zhetysu', date_trunc('day', now()) + interval '6 days 12 hours', '~5', 5, '{setup,teamwork,creativity}', 'moderate');
