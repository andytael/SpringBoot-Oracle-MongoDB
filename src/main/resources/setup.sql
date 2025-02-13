create table products (
    product_id varchar2(36) primary key,
    name varchar2(40),
    category_id varchar2(36),
    price number(10)
);

create table category (
    category_id varchar2(36) default sys_guid() primary key,
    category_name varchar2(40)
);

insert into category (category_name) VALUES ("Electronics");
insert into category (category_name) values ("Mechanical");

-- foreign keys?
-- sequences auto generation
