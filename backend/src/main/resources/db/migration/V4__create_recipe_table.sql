CREATE TABLE recipes (
    id CHAR(36) NOT NULL,
    owner_id CHAR(36) NOT NULL,
    group_id CHAR(36),

    name VARCHAR(255) NOT NULL,
    description TEXT,
    instructions TEXT,
    meal_type VARCHAR(255),
    servings INT,
    prep_time_minutes INT,
    image_url VARCHAR(255),
    last_made DATE,

    PRIMARY KEY (id),

    CONSTRAINT fk_recipe_owner
        FOREIGN KEY (owner_id) REFERENCES user_table(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recipe_group
        FOREIGN KEY (group_id) REFERENCES group_table(id)
        ON DELETE SET NULL
);
