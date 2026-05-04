import { expect, Page } from '@playwright/test';
import { test } from '../setup';

async function ensureGroup(
    request: Page["request"]
) {
    const authToken = (await request.storageState()).origins[0].localStorage.find(a => a.name == "auth_token")?.value;
    const headers = { "Authorization": `Bearer ${authToken}`, "Content-Type": "application/json" };
    const response = await request.fetch('/api/group/me', { method: "GET", headers });
    if (response.status() < 200 || response.status() >= 300)
        throw Error("API Failed: " + await response.text());
    const groups = await response.json();
    if (groups.length > 0) return groups[0].group;

    const name = `Playwright Group ${Date.now()}`;
    const created = await request.fetch('/api/group', { method: "POST", data: { name }, headers });
    if (created.status() < 200 || created.status() >= 300)
        throw Error("API Failed: " + await created.text());
    return created.json();
}

async function addShoppingItem(
    request: Page["request"],
    groupId: string,
    ingredientName: string
) {
    const authToken = (await request.storageState()).origins[0].localStorage.find(a => a.name == "auth_token")?.value;
    const response = await request.fetch(`/api/shopping-list/${groupId}`, {
        method: "POST",
        data: {
            ingredientName,
            amount: 1,
            unit: 'piece',
        },
        headers: { "Authorization": `Bearer ${authToken}` }
    });
    if (response.status() < 200 || response.status() >= 300)
        throw Error("API Failed: " + await response.text());
    return response.json();
}

async function selectGroup(page: Page, groupName: string) {
    await page.reload();
    await page.waitForLoadState('networkidle');

    await expect(page.getByLabel('Active group')).toBeVisible();
    expect((await page.getByRole("option", { name: groupName }).all()).length).toBeGreaterThan(0);
    await page.getByLabel('Active group').selectOption({ label: groupName });
    await page.waitForLoadState('networkidle');
    await expect(page.getByRole("heading", { name: "Items" })).toBeVisible();
}

async function clearShoppingList(page: Page) {
    await page.getByRole('button', { name: 'Clear list' }).click();
    const clearDialog = page.getByRole('heading', { name: 'Clear shopping list' }).locator("..").locator("..");
    await clearDialog.waitFor();
    await clearDialog.getByRole('button', { name: 'Clear list' }).click();
    await page.waitForTimeout(50);
}

test.describe('Shopping list', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/');
        await page.getByRole('link', { name: 'Shopping List', exact: true }).click();
        await page.waitForURL("/shopping");
    });

    test('can add an item from the dialog', async ({ page }) => {
        const group = await ensureGroup(page.request);
        const ingredientName = `Milk ${Date.now()}`;
        await page.request.post('/api/ingredients', { data: { name: ingredientName } });

        await selectGroup(page, group.name);

        await page.getByRole('button', { name: 'Add item' }).click();
        await expect(page.getByRole('heading', { name: 'Add ingredient' })).toBeVisible();

        await page.getByLabel('Ingredient').fill(ingredientName);
        await page.getByText(ingredientName).first().click();
        await page.getByLabel('Amount').fill('2');
        await page.getByRole('button', { name: 'Add', exact: true }).click();

        await page.getByText(ingredientName).waitFor();
        await expect(page.getByText(ingredientName)).toBeVisible();
    });

    test('can toggle and remove items', async ({ page }) => {
        const group = await ensureGroup(page.request);
        await selectGroup(page, group.name);
        await clearShoppingList(page);

        await expect(page.getByText('Items (0 left)')).toBeVisible();

        const item = await addShoppingItem(page.request, group.id, `Bread ${Date.now()}`);
        await page.reload();
        await selectGroup(page, group.name);

        await page.getByText('Items (1 left)').waitFor();

        const itemRow = page.getByText(item.ingredientName).locator("..").locator("input");
        expect(await itemRow.isChecked()).toBe(false);
        await itemRow.check();
        expect(await itemRow.isChecked()).toBe(true);
        await page.waitForLoadState("networkidle");
        await page.reload();
        await selectGroup(page, group.name);

        const itemRow2 = page.getByText(item.ingredientName).locator("..").locator("input");
        expect(await itemRow2.isChecked()).toBe(true);

        await page.getByText('Items (0 left)').waitFor();
        await expect(page.getByText('Items (0 left)')).toBeVisible();

        await page.locator('.list-item', { hasText: item.ingredientName }).getByRole('button', { name: '✕' }).click();
        await page.waitForTimeout(50);
        await expect(page.getByText(item.ingredientName)).not.toBeVisible();
    });

    test('can remove bought items and clear the list', async ({ page }) => {
        const group = await ensureGroup(page.request);
        await selectGroup(page, group.name);
        await clearShoppingList(page);

        const boughtItem = await addShoppingItem(page.request, group.id, `Butter ${Date.now()}`);
        const keepItem = await addShoppingItem(page.request, group.id, `Eggs ${Date.now()}`);
        await page.reload();
        await selectGroup(page, group.name);

        const boughtRow = page.getByText(boughtItem.ingredientName);
        await boughtRow.waitFor();
        await boughtRow.click();

        await page.getByText('Items (1 left)').waitFor();
        await expect(page.getByText('Items (1 left)')).toBeVisible();

        await page.getByRole('button', { name: 'Remove bought' }).click();
        const removeDialog = page.getByRole('heading', { name: 'Remove bought items' }).locator('..').locator('..');
        await removeDialog.getByRole('button', { name: 'Remove bought items' }).click();
        await page.waitForTimeout(50);
        await expect(page.getByText(boughtItem.ingredientName)).not.toBeVisible();
        await expect(page.getByText(keepItem.ingredientName)).toBeVisible();

        await clearShoppingList(page);
        await expect(page.getByText('No items yet, add some above.')).toBeVisible();
    });
});
