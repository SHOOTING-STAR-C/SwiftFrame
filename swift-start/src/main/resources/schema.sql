-- 用户表
CREATE TABLE IF NOT EXISTS swift_users (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    account_non_expired BOOLEAN DEFAULT TRUE,
    account_non_locked BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    failed_login_attempts INT DEFAULT 0,
    lock_until DATETIME,
    password_changed_at DATETIME,
    last_login_at DATETIME,
    last_login_ip VARCHAR(45),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色表
CREATE TABLE IF NOT EXISTS swift_roles (
    role_id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 权限表
CREATE TABLE IF NOT EXISTS swift_authorities (
    authority_id BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS swift_user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    assigned_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    assigned_by VARCHAR(50),
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES swift_users(user_id),
    FOREIGN KEY (role_id) REFERENCES swift_roles(role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS swift_role_authorities (
    role_id BIGINT NOT NULL,
    authority_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, authority_id),
    FOREIGN KEY (role_id) REFERENCES swift_roles(role_id),
    FOREIGN KEY (authority_id) REFERENCES swift_authorities(authority_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 业务区域表 (示例)
CREATE TABLE IF NOT EXISTS bussarea (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
