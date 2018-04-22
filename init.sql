CREATE SCHEMA test AUTHORIZATION test;

CREATE TABLE IF NOT EXISTS test.user
(
  id integer NOT NULL,
  count_pong NUMERIC,
  PRIMARY KEY (id)
)
WITH (
OIDS = FALSE
);

ALTER TABLE test.user
  OWNER to test;

CREATE OR REPLACE FUNCTION test.insrease_count(id_ INT) RETURNS VOID AS
$$
BEGIN
  LOOP
    UPDATE test.user SET count_pong = count_pong + 1 WHERE id = id_;
    IF found THEN
      RETURN;
    END IF;
    BEGIN
      INSERT INTO test.user(id, count_pong) VALUES (id_, 1);
      RETURN;
      EXCEPTION WHEN unique_violation THEN
    END;
  END LOOP;
END;
$$
LANGUAGE plpgsql;

SELECT test.insrease_count(2);

-- UPDATE test.user SET count_pong = count_pong + 1 WHERE id = 2;

