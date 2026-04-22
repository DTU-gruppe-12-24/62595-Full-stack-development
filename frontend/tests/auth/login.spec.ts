import { test } from '../setup';
import { expect } from '@playwright/test';

test('global setup logs user in successfully', async ({ page }) => {
  await page.goto('/');
  await page.getByRole('button', { name: /Account/i }).click();
  await expect(page.getByRole('button', { name: /log out/i })).toBeVisible();
});