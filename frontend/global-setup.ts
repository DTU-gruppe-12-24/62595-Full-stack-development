import { chromium, FullConfig } from '@playwright/test';

const TEST_USER = {
  name: 'Playwright Test User',
  email: 'playwright@test.dk',
  password: 'password',
};

async function globalSetup(config: FullConfig) {
  const baseURL = (config as any).use?.baseURL || 'http://localhost:5173';
  console.log("Using baseURL:", baseURL);

  // Register test user
  await fetch('http://localhost:8080/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(TEST_USER),
  }).catch(() => {});

  const browser = await chromium.launch();
  const page = await browser.newPage();

  console.log("Navigating to:", `${baseURL}/sign-in`);
  await page.goto(`${baseURL}/sign-in`);
  await page.waitForLoadState('networkidle');

  // Fill using generic selectors
  await page.locator('input[type="email"]').first().fill(TEST_USER.email);
  await page.locator('input[type="password"]').first().fill(TEST_USER.password);

  await page.getByRole('button', { name: /sign in/i }).click();

  await page.waitForURL(`${baseURL}/`);

  await page.context().storageState({ path: './storageState.json' });

  await browser.close();
}

export default globalSetup;