CREATE TABLE IF NOT EXISTS audit_logs (
    id          SERIAL PRIMARY KEY,
    occurred_at TIMESTAMPTZ NOT NULL,
    level       VARCHAR(16) NOT NULL,
    category    VARCHAR(64) NOT NULL,
    message     TEXT NOT NULL,
    payload     JSONB,
    correlation_id VARCHAR(64)
    );