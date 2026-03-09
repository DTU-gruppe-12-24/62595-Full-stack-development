import { test } from '../setup';
import { expect } from '@playwright/test';

test('user can log in manually', async ({ page }) => {
  await page.goto('/login');

  await page.getByLabel('Email').fill('playwright@test.dk');
  await page.getByLabel('Password').fill('password');
  await page.getByRole('button', { name: /login/i }).click();

  await expect(page).toHaveURL('/');

  await expect(page.getByRole('button', { name: /logout/i })).toBeVisible();
});