CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(18) NOT NULL PRIMARY KEY,
  password VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  rolename VARCHAR(10) NOT NULL
);
/* 以下のコードをファイルの末尾に追記 */
CREATE TABLE IF NOT EXISTS access_authorization (
  rolename VARCHAR(10) NOT NULL,
  uri VARCHAR(255) NOT NULL,
  PRIMARY KEY(rolename, uri)
);