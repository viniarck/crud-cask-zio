CREATE TABLE "person" (
  "uuid" varchar PRIMARY KEY,
  "name" varchar unique,
  "age" int,
  "deleted_at" timestamp null,
  "updated_at" timestamp null,
  "created_at" timestamp null
);

CREATE TABLE "contact" (
  "uuid" varchar PRIMARY KEY,
  "email" varchar unique null,
  "phone" varchar unique null,
  "person_uuid" varchar,
  "deleted_at" timestamp null,
  "updated_at" timestamp null,
  "created_at" timestamp null
);

ALTER TABLE "contact" ADD FOREIGN KEY ("person_uuid") REFERENCES "person" ("uuid");
