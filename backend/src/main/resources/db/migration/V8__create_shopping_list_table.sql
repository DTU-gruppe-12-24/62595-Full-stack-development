CREATE TABLE shopping_lists (
    id CHAR(36) NOT NULL,
    group_id CHAR (36) NOT NULL,
    ingredient_id CHAR(36) NOT NULL,
    amount FLOAT NOT NULL,
    is_bought BOOLEAN NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_shopping_lists_group
        FOREIGN KEY (group_id) REFERENCES group_table(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_shopping_lists_ingredient
        FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
        ON DELETE CASCADE
);