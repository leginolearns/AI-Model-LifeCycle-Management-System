CREATE DATABASE IF NOT EXISTS ai_model_lifecycle;

USE ai_model_lifecycle;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS datasets (
    dataset_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    source VARCHAR(150),
    records INT NOT NULL
);

CREATE TABLE IF NOT EXISTS ai_models (
    model_id INT PRIMARY KEY,
    model_name VARCHAR(150) NOT NULL,
    algorithm VARCHAR(100) NOT NULL,
    description VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS model_versions (
    version_id INT PRIMARY KEY,
    model_id INT NOT NULL,
    version_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    accuracy DOUBLE,
    dataset_id INT,
    FOREIGN KEY (model_id) REFERENCES ai_models(model_id),
    FOREIGN KEY (dataset_id) REFERENCES datasets(dataset_id)
);

CREATE TABLE IF NOT EXISTS deployments (
    deployment_id INT PRIMARY KEY,
    version_id INT NOT NULL,
    environment VARCHAR(100),
    endpoint VARCHAR(255),
    status VARCHAR(50),
    FOREIGN KEY (version_id) REFERENCES model_versions(version_id)
);

CREATE TABLE IF NOT EXISTS monitoring_records (
    record_id INT PRIMARY KEY,
    deployment_id INT NOT NULL,
    metric VARCHAR(100),
    value DOUBLE,
    status VARCHAR(50),
    timestamp DATETIME,
    FOREIGN KEY (deployment_id) REFERENCES deployments(deployment_id)
);

SHOW TABLES;
