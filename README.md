# Application Overview

## 🔑 Login Functionality

To streamline development, I used a self-hosted authentication server. The API application acts as an OAuth resource, meaning you must include a token with every outgoing request to the application.

### 🚀 How to Login

To log in, use the following `curl` command. You can copy and paste it into Postman for testing:

```bash
curl --location 'https://authserver.obayd.online/realms/hahn_software/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=emrs_service' \
--data-urlencode 'client_secret=FghZfWxI1lBCYGzUi8R1CIBz5U9k16be' \
--data-urlencode 'username=hahn' \
--data-urlencode 'password=Azerty@2025'
```



#### Login Credentials (Examples for Testing) :

- Admin:`username=hahn` `password=Azerty@2025`
- HR Personnel:`username=hahn_Hr_persone` `password=Azerty@2025`
- Manager:`username=hahn_Manager` `password=Azerty@2025`


#### 🔄 Refreshing the Access Token :

If the access_token expires, you can use the refresh_token to obtain a new one. Use the following curl command:

```bash
curl --location 'https://authserver.obayd.online/realms/hahn_software/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=refresh_token' \
--data-urlencode 'client_id=emrs_service' \
--data-urlencode 'client_secret=FghZfWxI1lBCYGzUi8R1CIBz5U9k16be' \
--data-urlencode 'refresh_token=<your_refresh_token>'
```

Replace <your_refresh_token> with the actual refresh_token you received during login.

---

## 🛠️ API Functionality

For the APIs, all operations are designed to work in **batch mode**, meaning you can perform actions on multiple records at once. This applies to both employee and department endpoints.


### ⚠️ Notice:
To use the APIs, you **must include the `access_token`** in your requests for authorization. The `access_token` is obtained after logging in (see the [Login Functionality](#-login-functionality) section for details).

### Key Features:
- **Batch Operations**:
  - **Add Multiple Employees**: You can add multiple employees in a single request.
  - **Delete Multiple Employees**: Delete a batch of employee records at once.
  - **Update Multiple Employees**: Update multiple employee records simultaneously.
  - **Department Operations**: The same batch functionality applies to department endpoints, allowing you to add, delete, or update multiple departments in one request.

This batch approach ensures efficiency and scalability, especially when managing large datasets.

---

## 📝 Logs Functionality

To ensure transparency and accountability, I implemented a logging mechanism using **Aspect-Oriented Programming (AOP)**. Here's how it works:

### Key Features:
- **Custom Annotation (`@LogUserOperation`)**:
  - I created a custom annotation called `@LogUserOperation` to mark methods that require logging.
  - When a method annotated with `@LogUserOperation` is called, an **Aspect** is triggered automatically.

- **Aspect Logic**:
  - The Aspect intercepts the method execution and logs the following details to the database:
    - **User ID**: The ID of the user performing the operation.
    - **Username**: The username of the user.
    - **Operation**: The type of operation being performed (e.g., CREATE, UPDATE, DELETE).
    - **Method**: The name of the method being executed.
    - **IP Address**: The IP address of the user making the request.
    - **Status**: The status of the operation (`SUCCESS` or `FAILURE`).

- **Status Update**:
  - After the annotated method completes execution, the Aspect updates the log record with the final status of the operation (`SUCCESS` or `FAILURE`).


----


# Installation and Use

## 🐳 Using Docker

To set up the application using Docker, follow these steps:

### 1. Clone the Repository
First, clone the repository to your local machine:

```bash
git clone https://github.com/ObaidOnCall/emrs_task.git
cd emrs_task
```


### 2. Configure Environment Variables

The application uses environment variables defined in the .env file. You can modify these variables if needed:

`DB_NAME=hahn_db`
`DB_USER=hahn_user`
`DB_PASSWORD=hahn_password`
`DB_SCHEMA=public`
`HOST_PORT=8080`

### 3. Docker Compose File( ⚠️ Notice if you want to copie this docker compose config dirclty without cloning the enitre repo  , do not forget to copie the .env file  with it )

```yaml
services:
  postgres:
    image: postgres:16.2
    # volumes:
    #   - postgres_hahn_task_data:/var/lib/postgresql/data
    environment:
      POSTGRES_DB: ${DB_NAME}
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    networks:
      - hahn_task_network
    restart: unless-stopped

  hahn_task:
    # build: .
    image: lasthour/hahn_tasks:1.4
    environment:
      DB_HOST: postgres:5432
      DB_NAME: ${DB_NAME}
      DB_USER: ${DB_USER}
      DB_PASSWORD: ${DB_PASSWORD}
      DB_SCHEMA: ${DB_SCHEMA}
    ports:
      - '${HOST_PORT:-8080}:8080'
    restart: unless-stopped
    depends_on:
      - postgres
    networks:
      - hahn_task_network

volumes:
  postgres_hahn_task_data:
    driver: local

networks:
  hahn_task_network: {}
```


### 🔑 Key Notes:
- **🔨 Build from Source**: If you want to build the application from source instead of using the pre-built Docker image, uncomment the `# build: .` line under the `hahn_task` service. By default, the application uses the pre-built image hosted on Docker Hub (`lasthour/hahn_tasks:1.4`).


### 4. Docker Compose File

```bash
docker compose up -d
```

The application will be accessible at http://localhost:8080/swagger-ui/index.html#/ (or the port you specified in .env).
