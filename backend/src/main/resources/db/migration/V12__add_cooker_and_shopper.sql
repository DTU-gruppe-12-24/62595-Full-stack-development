ALTER TABLE meal_plans ADD COLUMN cooker_id CHAR(36) NULL;
ALTER TABLE meal_plans ADD CONSTRAINT fk_meal_plans_cooker
    FOREIGN KEY (cooker_id) REFERENCES user_table(id) ON DELETE SET NULL;

ALTER TABLE group_table ADD COLUMN current_shopper_id CHAR(36) NULL;
ALTER TABLE group_table ADD CONSTRAINT fk_group_shopper
    FOREIGN KEY (current_shopper_id) REFERENCES user_table(id) ON DELETE SET NULL;