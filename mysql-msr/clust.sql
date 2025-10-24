-- 유저 생성에 대해 원할 하지 않아 다음과 같이 옵션을 추가하여 생성
DROP USER IF EXISTS 'replication_user'@'172.18.%';
DROP USER IF EXISTS 'replication_user'@'%';

CREATE USER 'replication_user'@'172.18.%'
  IDENTIFIED WITH caching_sha2_password BY '1q2w3e4r!!';

GRANT REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'replication_user'@'172.18.%';
FLUSH PRIVILEGES;

-- 1. Master 서버 SQL 5.7  이하
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%' IDENTIFIED BY '1q2w3e4r!!';
FLUSH PRIVILEGES;
SHOW MASTER STATUS;

-- 8.0 이상
CREATE USER 'replication_user'@'%' IDENTIFIED BY '1q2w3e4r!!';
GRANT REPLICATION SLAVE ON *.* TO 'replication_user'@'%';
FLUSH PRIVILEGES;
SHOW MASTER STATUS;

-- 2. Slave 서버 SQL
-- STOP SLAVE;
-- RESET SLAVE ALL;

-- 5.7
CHANGE MASTER TO
    MASTER_HOST='mysql-master_a',
    MASTER_USER='replication_user',
    MASTER_PASSWORD='1q2w3e4r!!',
    MASTER_LOG_FILE='<TODO>',
    MASTER_LOG_POS=<TODO>
    FOR CHANNEL 'master_a_channel';
START SLAVE FOR CHANNEL 'master_a_channel';;

SHOW SLAVE STATUS;

--8.0 이상
STOP REPLICA;

CHANGE REPLICATION SOURCE TO
  SOURCE_HOST='mysql-master-a',
  SOURCE_PORT=3306,
  SOURCE_USER='replication_user',
  SOURCE_PASSWORD='1q2w3e4r!!',
  GET_SOURCE_PUBLIC_KEY = 1, -- 인증키 x
  -- GTID 쓰면 아래 2줄 대신 AUTO_POSITION=1 사용
  SOURCE_LOG_FILE='<TODO>',
  SOURCE_LOG_POS=<TODO>
  FOR CHANNEL 'master_a_channel';
START REPLICA FOR CHANNEL 'master_a_channel';

SHOW REPLICA STATUS;


SHOW TABLES;

#