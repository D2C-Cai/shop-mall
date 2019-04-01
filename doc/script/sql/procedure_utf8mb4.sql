/*
 * 将当前数据库中所有表的字符集转换成utf8mb4
 */

DROP PROCEDURE IF EXISTS UP_CHANGE_UTF8MB4;

DELIMITER $$

CREATE PROCEDURE UP_CHANGE_UTF8MB4()
  COMMENT '将当前数据库中所有表的字符集转换成utf8mb4'
BEGIN
  DECLARE $i INT;
  DECLARE $cnt INT;
  DECLARE $NAME VARCHAR(64);

  #创建临时表,代替游标
  DROP TABLE IF EXISTS tmp_Table_name;
  CREATE TEMPORARY TABLE tmp_Table_name (
    id INT NOT NULL AUTO_INCREMENT,
    table_name VARCHAR(64) NOT NULL,
    PRIMARY KEY (`id`)
  );

  #插入要处理的表名到临时表中
  INSERT INTO tmp_Table_name (table_name)
    SELECT
      table_name
    FROM information_schema.`TABLES`
    WHERE TABLE_TYPE = 'BASE TABLE'
    AND TABLE_SCHEMA = DATABASE();

  #循环处理每一张表,改表的字符集
  SET $i = 1;
  SELECT
    COUNT(1) INTO $cnt
  FROM tmp_Table_name;
  WHILE $i <= $cnt DO
    SELECT
      table_name INTO $NAME
    FROM tmp_Table_name
    WHERE id = $i;
    
    SET @asql = CONCAT('ALTER TABLE ', $NAME, '  CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci; ');
    PREPARE asql FROM @asql;
    EXECUTE asql;
    
    SET @asql = CONCAT('ALTER TABLE ', $NAME, ' CONVERT TO CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci; ');
    PREPARE asql FROM @asql;
    SELECT @asql;
    EXECUTE asql;

    SET $i = $i + 1;
  END WHILE;
  DEALLOCATE PREPARE asql;
  DROP TABLE tmp_Table_name;
END$$
DELIMITER;