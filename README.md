# Fun with types:  Value Object / Alias types, Nested Types

---

The objective is to re-enforce usage of more constraining types in Java so that we can leverage help from the
compiler. More often than not, the reason of having bad data in the data store is often caused by not being able
restricting values for a certain abstraction. We also want to make sure we serialize/deserialize and convert from
and to the database properly.

In addition, we want to experiment with nested type serialization and deserialization but mostly how to handle the
nested type with the database using R2DBC driver. Last we also want to experiment with the R2DBC driver on how to
handle one-to-many relationship (for instance the spring data jdbc does handle those relationship nicely but not R2DBC).

However, before we dive into representing simple Java Value Object and nested types, we need to have a working base
line with the database using testcontainers and docker-compose.

## 1. Minimal setup to get the application started locally with testcontainers or docker-compose

---

### 1.1 Prerequisite

1. Docker Desktop or Rancher installed locally.


2. In some situation (e.g. in a company; have software install policies), you may not be able to install either Docker
   Desktop nor Rancher. In such case, you can install `colima` with docker-engine and docker-compose (those are
   free and does not require Admin Privs on the Mac).


3. Also, if you are using Mac M1/M2/M3 with colima, you may need to start colima with arm architecture. See aside
   below.

&nbsp;

      ASIDE
      -----

      If you are using Mac M1/M2/M3 and you want to try colima, you can do the steps below.  In addition to install colima,
      you will need to install docker engine (not Docker Desktop - not the same thing) and docker-compose.
      
      > brew install colima
      > brew install docker docker-compose
      > colima start --arch aarch64 --vm-type vz --vz-rosetta  

&nbsp;  
Make sure your Docker Desktop or Rancher or `colima` are running.

&nbsp;

### 1.2 Running basic application

---

&nbsp;  
Checkout the `feature/min-testcontainers-setup` to see the minimum configuration you must provide to get the
application up and running locally with testcontainers or docker-compose. Note there are no new classes but just
configurations; mostly about postgres.

```shell
> git checkout feature/min-testcontainers-setup
```

&nbsp;  
&nbsp;

#### 1.2.1 Running basic application with TestContainers

---

With this method, the developer can start an application very quickly with no worries about creating a
docker-compose. With `spring-boot-3.2` and with `Testcontainers` dependency, it automatically create inside
`src/test/java/«your-package»/TestJava«XYZ»Application.java` an application that can be used for local development
and will start a Testcontainer with postgres (in this setup) and keep the data between startup.

&nbsp;  
Here is a checklist you must do in order to get this setup.
&nbsp;

1. Add in `src/main/resources` a file `application-unittest.properties` and we will use an active profile `unittest`
   for unit tests but also to start the application locally for the developer to manually test code quickly. This
   `application-unittest.properties` must contain the following:

   &nbsp;

    ```yaml
    spring.sql.init.mode=always
    spring.sql.init.schema-locations=classpath:/postgres/schema.sql
    ```

   &nbsp;     
   The `spring.sql.init.mode=always` will always execute the `schema.sql` script. So you need to write that script
   according to your needs. By default, it search the `schema.sql` script within `src/main/resources/` but you can
   put it within a another folder such as `src/main/resources/postgres/shcema.sql` and add that location within the
   `application-unittest.properties` under `spring.sql.init.schema-locations`.

   &nbsp;
2. Add a `schema.sql` script to `src/main/resources/postgres/` and write your own database objects.

   &nbsp;
3. Add the profile `unittest` by editing (in the present case) the `TestJavaFunWithTypesApplication.java`
   configuration (IntelliJ Idea); Right click on that file, and select `Modify Run Configuration...` and add the
   profile. You can also export the profile from a terminal: `> export SPRING_PROFILES_ACTIVE=unittest`.

&nbsp;  
To run the app with testcontainers from the terminal with profile `unittest`:

   ```shell
      > ./mvnw spring-boot:test-run  -Dspring-boot.run.profiles=unittest
   ```

&nbsp;  
You can check if postgres is running within docker engine:

```shell
   > docker ps -a 
   CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                         NAMES
   a229a0bfb9ce   postgres:16.1-alpine   "docker-entrypoint.s…"   49 minutes ago   Up 13 seconds   0.0.0.0:32783->5432/tcp, :::32783->5432/tcp   db-products
```

&nbsp;  
You can also login into Postgres within your running container named `db-products`.

```shell
   > docker exec -it db-products psql -U postgres
   postgres=#
```

&nbsp;  
From psql prompt, you can show the databases `\l` and you can switch to the `products` database: `\c products`.

Now you can develop your application with a database of your choice - this is a good setup for `inner development
loop`, a term coined by Thomas Vitale.

&nbsp;

#### 1.2.2 Running basic application with docker-compose

---


To run the application, we must be able to connect to a database. We can use docker-compose to start the database
within a OCI container. We already have included "Docker Compose Support" dependencies in our initial spring boot
application (from start.springboot.io). By doing so, it automatically created a docker-compose.yml (or compose.yaml)
at the root of the project. I did rename it to `docker-compose.yml` in this project.

&nbsp;  
We will need to modify two files: `docker-compose.yml` and `application-local.yml` and define a new profile `local`.

1. docker-compose.yml

   We need to define the database service with the needed environment variables for postgres.

   ```yaml
   version: '3'

   services:
      postgres:
         container_name: db-products
         image: 'postgres:16.1-alpine'
         environment:
           - 'POSTGRES_DB=products'
           - 'POSTGRES_PASSWORD=postgres'
           - 'POSTGRES_USER=postgres'
         ports:
           - '5432:5432'

     ```

2. application-local.yml

   &nbsp;  
   We need to inform the application how to connect to the database. We do this through the application.yml (or for
   when running locally, we define a `local` profile and thus use `application-local.properties`). The content
   should look like:

   ```yaml
   spring.r2dbc.url=r2dbc:postgresql://localhost:5432/products
   spring.r2dbc.username=postgres
   spring.r2dbc.password=postgres
 
   spring.data.r2dbc.repositories.enabled=true
   spring.sql.init.mode=always
   spring.sql.init.schema-locations=classpath:/postgres/schema.sql
   ```

   &nbsp;     
   `spring.r2dbc.url` defines the connection url with the database name that must match `docker-compose.yml`
   database name (in this case the database name is `products`). The `spring.r2dbc.username` and `spring.r2dbc.
   password` must also match the username and password defined in `docker-compose.yml`.

   &nbsp;  
   `spring.data.r2dbc.repositories.enabled=true` will be used when we start creating a repository for our entity.
   `spring.sql.init.mode=always` will indicate to always run `schema.sql` (and `data.sql` if present) on startup.
   `spring.sql.init.schema-locations` defines the location of our `schema.sql` for this project.

   &nbsp;
   ```
   Note:
   ----- 
   We use CREATE TABLE IF NOT EXISTS ... in order to avoid error and re-creation of the database objects 
   during startup.
   ```

&nbsp;

##### Running docker-compose

Once you have the above defined, before starting your application, you need to make sure you define the `local`
profile in your IDE for the `JavaFunWithTypesApplication - Edit Configuration...` (this project). You also need to
start `docker-compose.yml` before starting the application. Simply press the "Play Button" within `docker-compose.yml`
file (IntelliJ Idea).  You can then start the application within your IntelliJ Idea.

You can also start the application on the terminal:

```shell
> ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

From this setup, you can have your `inner development loop` having your database always running inside docker engine 
with the ability to login into the database and verify data or modify tables or other database objects.

&nbsp;  
&nbsp;  

