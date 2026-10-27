CREATE TABLE users (
                     id BIGINT NOT NULL AUTO_INCREMENT,
                     nickname VARCHAR(50) NULL,
                     created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                     updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
        ON UPDATE CURRENT_TIMESTAMP(6),
                     PRIMARY KEY (id)
);

CREATE TABLE social_accounts (
                               id BIGINT NOT NULL AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               provider VARCHAR(20) NOT NULL,
                               provider_user_id VARCHAR(255) NOT NULL,
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),

                               PRIMARY KEY (id),

                               CONSTRAINT uk_social_provider_user
                                 UNIQUE (provider, provider_user_id),

                               CONSTRAINT fk_social_account_user
                                 FOREIGN KEY (user_id) REFERENCES users(id),

                               INDEX idx_social_account_user (user_id)
);
