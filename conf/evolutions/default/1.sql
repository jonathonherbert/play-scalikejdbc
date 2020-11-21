-- !Ups

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE rule
(
  rule_id uuid DEFAULT uuid_generate_v4(),
  name character varying COLLATE pg_catalog."default" NOT NULL,
  description character varying COLLATE pg_catalog."default" NOT NULL
);

CREATE TABLE feedback
(
  id SERIAL PRIMARY KEY,
  rule_id uuid NOT NULL,
  email character varying(100) COLLATE pg_catalog."default" NOT NULL,
  description text COLLATE pg_catalog."default"
);

-- !Downs

DROP TABLE rule;
DROP TABLE feedback;