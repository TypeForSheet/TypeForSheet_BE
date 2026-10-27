CREATE TABLE oauth_nonces (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            nonce_hash CHAR(64) NOT NULL,
                            expires_at DATETIME(6) NOT NULL,

                            PRIMARY KEY (id),

                            CONSTRAINT uk_oauth_nonce_hash
                              UNIQUE (nonce_hash)
);
