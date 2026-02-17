CREATE TABLE group_members (
    user_id CHAR(36) NOT NULL,
    group_id CHAR(36) NOT NULL,
    role VARCHAR(255),

    PRIMARY KEY (user_id, group_id),

    CONSTRAINT fk_group_members_user
        FOREIGN KEY (user_id) REFERENCES user_table(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_group_members_group
        FOREIGN KEY (group_id) REFERENCES group_table(id)
        ON DELETE CASCADE
);