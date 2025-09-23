-- Creación del schema respectivo.
CREATE SCHEMA IF NOT EXISTS pos;

-- Se pone el schema como predeterminado
ALTER DATABASE api_pos SET search_path TO pos;

-- pos.users definition

-- Drop table

-- DROP TABLE pos.users;

CREATE TABLE pos.users (
	user_id varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	active bool NULL,
	complete_name varchar(255) NULL,
	"password" varchar(255) NULL,
	user_role varchar(255) NULL,
	username varchar(255) NULL,
	created_by varchar(255) NULL,
	updated_by varchar(255) NULL,
	CONSTRAINT ukr43af9ap4edm43mmtq01oddj6 UNIQUE (username),
	CONSTRAINT users_pkey PRIMARY KEY (user_id),
	CONSTRAINT users_user_role_check CHECK (((user_role)::text = ANY ((ARRAY['ADMIN'::character varying, 'USER'::character varying])::text[])))
);

INSERT INTO pos.users
(user_id, created_at, updated_at, active, complete_name, "password", user_role, username, created_by, updated_by)
VALUES('51c06a69-2ad3-4192-81be-9b8cccbf82e0', '2025-09-01 18:13:43.227', '2025-09-01 18:13:43.227', true, 'Usuario Administrador', '$2a$10$UQDcJ1WrgN0I1OUyjpRLZOK3fKpo4UFnnDZUkg13bJpb71F2vWc9S', 'ADMIN', 'admin', 'SYSTEM', 'SYSTEM');

-- pos.clients definition

-- Drop table

-- DROP TABLE pos.clients;

CREATE TABLE pos.clients (
	client_id varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	active bool NOT NULL,
	birthdate date NULL,
	complete_name varchar(255) NOT NULL,
	created_by varchar(255) NOT NULL,
	email varchar(255) NULL,
	phone_number varchar(255) NULL,
	updated_by varchar(255) NULL,
	CONSTRAINT clients_pkey PRIMARY KEY (client_id),
	CONSTRAINT uksrv16ica2c1csub334bxjjb59 UNIQUE (email)
);


-- pos.payment_methods definition

-- Drop table

-- DROP TABLE pos.payment_methods;

CREATE TABLE pos.payment_methods (
    code VARCHAR(2) PRIMARY KEY,
    description VARCHAR(100) NOT NULL,
    visible BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO pos.payment_methods (code, description, visible) VALUES
('01', 'Efectivo', true),
('02', 'Tarjeta', true),
('03', 'Cheque',false),
('04', 'Transferencia o depósito bancario', false),
('05', 'Recaudado por un tercero', false),
('06', 'SINPE Móvil',true),
('07', 'Plataforma digital', false),
('99', 'Otros', true);


-- pos.items definition

-- Drop table

-- DROP TABLE pos.items;

CREATE TABLE pos.items (
	item_id varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	active bool NOT NULL,
	item_code varchar(255) NULL,
	created_by varchar(255) NOT NULL,
	item_description varchar(255) NULL,
	image_url varchar(255) NULL,
	item_name varchar(255) NULL,
	item_price numeric(38, 2) NULL,
	item_type varchar(255) NOT NULL,
	updated_by varchar(255) NULL,
	CONSTRAINT items_item_type_check CHECK (((item_type)::text = ANY ((ARRAY['PRODUCT'::character varying, 'SERVICE'::character varying])::text[]))),
	CONSTRAINT items_pkey PRIMARY KEY (item_id),
	CONSTRAINT ukn3wi2ihmiiviolnt3ifaxppsk UNIQUE (item_code)
);

-- pos.inventories definition

-- Drop table

-- DROP TABLE pos.inventories;

CREATE TABLE pos.inventories (
	inventory_id varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	created_by varchar(255) NULL,
	max_stock int4 NULL,
	minimum_stock int4 NULL,
	stock int4 NULL,
	item varchar(255) NOT NULL,
	updated_by varchar(255) NULL,
	CONSTRAINT inventories_pkey PRIMARY KEY (inventory_id),
	CONSTRAINT ukmnll2roki25vwt4tsawqtflq3 UNIQUE (item)
);


-- pos.inventories foreign keys

ALTER TABLE pos.inventories ADD CONSTRAINT fk4mxomieau8i6ghobtqlku6dik FOREIGN KEY (item) REFERENCES pos.items(item_id);


-- pos.invoices definition

-- Drop table

-- DROP TABLE pos.invoices;

CREATE TABLE pos.invoices (
	invoice_id varchar(255) NOT NULL,
	created_at timestamp(6) NOT NULL,
	updated_at timestamp(6) NULL,
	annulled bool NULL,
	created_by varchar(255) NULL,
	invoice_date timestamp(6) NULL,
	subtotal numeric(38, 2) NULL,
	total numeric(38, 2) NULL,
	client varchar(255) NULL,
	updated_by varchar(255) NULL,
	payment_method varchar(255) NOT NULL,
	CONSTRAINT invoices_pkey PRIMARY KEY (invoice_id)
);


-- pos.invoices foreign keys

ALTER TABLE pos.invoices ADD CONSTRAINT fklyyiyv9xog3wnmbpos1qoefar FOREIGN KEY (client) REFERENCES pos.clients(client_id);
ALTER TABLE pos.invoices ADD CONSTRAINT fktdwjehuax67oeeogcjroc5qxr FOREIGN KEY (payment_method) REFERENCES pos.payment_methods(code);

-- pos.invoice_details definition

-- Drop table

-- DROP TABLE pos.invoice_details;

CREATE TABLE pos.invoice_details (
	invoice_detail_id varchar(255) NOT NULL,
	amount int4 NULL,
	subtotal numeric(38, 2) NULL,
	total numeric(38, 2) NULL,
	invoice varchar(255) NOT NULL,
	item varchar(255) NOT NULL,
	CONSTRAINT invoice_details_pkey PRIMARY KEY (invoice_detail_id)
);


-- pos.invoice_details foreign keys

ALTER TABLE pos.invoice_details ADD CONSTRAINT fk21ri5x7l1idlyd95wfkes3oeh FOREIGN KEY (item) REFERENCES pos.items(item_id);
ALTER TABLE pos.invoice_details ADD CONSTRAINT fkrscjnx6uld5mxyio2hr0gmny FOREIGN KEY (invoice) REFERENCES pos.invoices(invoice_id);