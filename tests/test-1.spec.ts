import { test, expect } from '@playwright/test';

test('can create, edit, and delete a group', async ({ page }) => {
  const originalName = `MyFavGroup-${Date.now()}`;
  const updatedName = `Foodies4Life-${Date.now()}`;

  await page.goto('/');

  // Create
  await page.getByRole('link', { name: 'Groups' }).click();
  await page.getByRole('button', { name: 'Create' }).click();
  await page.getByRole('textbox', { name: 'Group name...' }).fill(originalName);
  await page.locator('footer').getByRole('button', { name: 'Create' }).click();
  await expect(page.getByText(originalName)).toBeVisible({ timeout: 5000 });

  // Edit
  const row = page.getByText(originalName).locator('..');
  await row.getByRole('button', { name: 'Edit' }).click();
  await page.getByRole('textbox', { name: 'Group name...' }).fill(updatedName);
  await page.getByRole('button', { name: 'Save' }).click();
  await expect(page.getByText(updatedName)).toBeVisible({ timeout: 5000 });

  // Delete
  const updatedRow = page.getByText(updatedName).locator('..');
  await updatedRow.getByRole('button', { name: 'Delete' }).click();
  await page.getByRole('button', { name: 'Yes' }).click();
  await expect(page.getByText(updatedName)).not.toBeVisible({ timeout: 5000 });
});