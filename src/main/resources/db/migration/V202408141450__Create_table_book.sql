CREATE TABLE "book" (
    "id" varchar(64) PRIMARY KEY,
    "title" varchar(100) NOT NULL,
    "author" varchar(50) NOT NULL,
    "price" numeric(10,2) NOT NULL,
    "category" varchar(10) NOT NULL,
    "count" int NOT NULL,
    "created_by" varchar(20) DEFAULT 'system',
    "created_at" timestamp DEFAULT CURRENT_TIMESTAMP,
    "updated_by" varchar(20) DEFAULT 'system',
    "updated_at" timestamp DEFAULT CURRENT_TIMESTAMP,
    "deleted_at" timestamp NULL
);

CREATE UNIQUE INDEX book_store_unique ON book USING btree (title, author);