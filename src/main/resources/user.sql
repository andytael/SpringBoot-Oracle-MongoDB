-- USER SQL
CREATE USER TESTUSER IDENTIFIED BY <<replace-with-your-password>>;

-- ADD ROLES
GRANT CONNECT, RESOURCE TO TESTUSER; -- IS THIS NEEDED?
GRANT DB_DEVELOPER_ROLE TO TESTUSER;

-- QUOTA
ALTER USER TESTUSER QUOTA UNLIMITED ON DATA;

-- REST ENABLE
BEGIN
    ORDS_ADMIN.ENABLE_SCHEMA(
            p_enabled => TRUE,
            p_schema => 'TESTUSER',
            p_url_mapping_type => 'BASE_PATH',
            p_url_mapping_pattern => 'testuser',
            p_auto_rest_auth=> FALSE
    );
    -- ENABLE DATA SHARING
    C##ADP$SERVICE.DBMS_SHARE.ENABLE_SCHEMA(
            SCHEMA_NAME => 'TESTUSER',
            ENABLED => TRUE
    );
    commit;
END;
/