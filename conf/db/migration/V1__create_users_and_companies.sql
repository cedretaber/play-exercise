CREATE TABLE "companies" (
  "id" bigserial PRIMARY KEY,
  "name" TEXT NOT NULL
);

CREATE TABLE "users" (
  "id" bigserial PRIMARY KEY,
  "name" TEXT NOT NULL,
  "company_id" bigint REFERENCES "companies" ("id")
);