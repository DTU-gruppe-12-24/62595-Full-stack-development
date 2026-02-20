CREATE TABLE recipe_ingredients(
    recipe_id CHAR(36) NOT NULL,
    ingredient_id CHAR(36) NOT NULL,
    amount FLOAT,
    unit VARCHAR(255),

    PRIMARY KEY (recipe_id, ingredient_id),

    CONSTRAINT fk_recipe_ingredients_recipe
        FOREIGN KEY (recipe_id) REFERENCES recipes(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recipe_ingredients_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
        ON DELETE CASCADE
);
