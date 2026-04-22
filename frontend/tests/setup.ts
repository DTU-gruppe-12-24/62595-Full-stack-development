import { test as base } from '@playwright/test';

export const test = base;

test.beforeEach(async ({ request }) => {
    await request.delete('http://localhost:8080/api/test/reset');
});