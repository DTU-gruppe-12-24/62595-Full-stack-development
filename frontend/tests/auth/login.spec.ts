import { test } from '../setup';
import { expect } from '@playwright/test';

test('global setup logs user in successfully', async ({ page }) => {
  await page.goto('/');
  await expect(page.getByRole('link', { name: /log out/i })).toBeVisible();
});