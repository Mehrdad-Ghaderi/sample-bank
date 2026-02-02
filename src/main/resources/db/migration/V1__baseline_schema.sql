-- Customer table
CREATE TABLE customer_entity (
                                 id UUID PRIMARY KEY,
                                 business_id BIGINT NOT NULL UNIQUE,
                                 phone_number VARCHAR(20) NOT NULL UNIQUE,
                                 name VARCHAR(255) NOT NULL,
                                 status VARCHAR(20) NOT NULL,
                                 version BIGINT NOT NULL DEFAULT 0,
                                 created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                                 updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Account table
CREATE TABLE account_entity (
                                id UUID PRIMARY KEY,
                                number VARCHAR(50) NOT NULL UNIQUE,
                                customer_id UUID NOT NULL REFERENCES customer_entity(id),
                                balance NUMERIC(19,4) NOT NULL DEFAULT 0,
                                currency VARCHAR(3) NOT NULL,
                                status VARCHAR(20) NOT NULL,
                                version BIGINT NOT NULL DEFAULT 0,
                                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                                updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

-- Transaction table
CREATE TABLE transaction_entity (
                                    id UUID PRIMARY KEY,
                                    sender_account_id UUID NOT NULL REFERENCES account_entity(id),
                                    receiver_account_id UUID NOT NULL REFERENCES account_entity(id),
                                    amount NUMERIC(19,4) NOT NULL,
                                    currency VARCHAR(3) NOT NULL,
                                    transaction_time TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
                                    type VARCHAR(20) NOT NULL
);
