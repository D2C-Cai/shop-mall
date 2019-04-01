/*
 * 将当前数据库中所有表的名字转变为大写
 */

CREATE PROCEDURE `UPPERCASE_TABLENAMES`(IN dbname VARCHAR(200))
  COMMENT '将当前数据库中所有表的名字转变为大写'
BEGIN
  DECLARE done INT DEFAULT 0;
  DECLARE oldname VARCHAR(200);
  DECLARE cur CURSOR FOR SELECT table_name FROM information_schema.TABLES WHERE table_schema = dbname;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = 1;
OPEN cur;
REPEAT
  FETCH cur INTO oldname;
  SET @newname = UPPER(oldname);
  SET @isNotSame = @newname <> BINARY oldname;
  IF NOT done && @isNotSame THEN
    SET @SQL = CONCAT('rename table `',oldname,'` to `', LOWER(@newname), '_tmp` ');
    PREPARE tmpstmt FROM @SQL;
    EXECUTE tmpstmt;
    SET @SQL = CONCAT('rename table `',LOWER(@newname),'_tmp` to `',@newname, '`');
    PREPARE tmpstmt FROM @SQL;
    EXECUTE tmpstmt;
    DEALLOCATE PREPARE tmpstmt;
  END IF;
UNTIL done END REPEAT;
CLOSE cur;
END