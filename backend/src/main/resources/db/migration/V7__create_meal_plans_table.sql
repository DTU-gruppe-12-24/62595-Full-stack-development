CREATE TABLE meal_plans (
    id CHAR(36) NOT NULL,
    group_id CHAR(36) NOT NULL,
    recipe_id CHAR(36) NOT NULL,
    scheduled_date DATE NOT NULL,
    meal_slot VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT  fk_meal_plans_group
        FOREIGN KEY (group_id) REFERENCES group_table(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_meal_plans_recipe
        FOREIGN KEY (recipe_id) REFERENCES recipes(id)
        ON DELETE CASCADE
);