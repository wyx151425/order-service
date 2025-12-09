-- 添加新的列，保存创建该行数据的用户
ALTER TABLE orders
    ADD COLUMN created_by varchar(255);

-- 添加新的列，保存最后更新该行数据的用户
ALTER TABLE orders
    ADD COLUMN last_modified_by varchar(255);
