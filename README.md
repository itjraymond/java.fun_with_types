# To see basic setup with testcontainers or docker-compose checkout the branch: feature/min-testcontainers-setup

# Fun with types:  Value Object / Alias types, Nested Types

---

The objective is to re-enforce usage of more constraining types in Java so that we can leverage help from the
compiler. This is also a principle named as "Having Invalid State Unrepresentable" More often than not, the reason of
having bad data in the data store is often caused by not being able restricting values for a certain abstraction. We
also want to make sure we serialize/deserialize and convert from and to the database properly.

We will start, in this installement, with the representation of simple Value Object.

However, before we do, lets establish a working base line for our inner loop development using testcontainers and
docker-compose.

## 1. Minimal setup to get the application started locally with testcontainers or docker-compose

---

### 1.1 Prerequisite

1. Docker Desktop or Rancher installed locally.


2. In some situation, for instance, at your company you may have software installation policies prohibiting you to
   install Docker or Rancher. In such case, you can install `colima` with docker-engine and docker-compose as
   alternatives (those are free and does not require Admin Privs on the Mac).


3. Also, if you are using Mac M1/M2/M3 with colima, you may need to start colima with arm architecture. See aside
   below. But a note of caution:  I did experience issues using colima on a M1. Same code base works in a Mac Intel
   based processor but had weird behavior with testcontainers for test (running a single test on a M1 could endup with
   few success and few failure; even after running the same test repeatably - I still haven't found the issue hence the
   caution)

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
configurations; mostly about postgres, testcontainers and docker-compose.

```shell
> git checkout feature/min-testcontainers-setup
```

&nbsp;  
&nbsp;

#### 1.2.1 Running basic application with TestContainers

---

With this method, the developer can start an application very quickly with no worries about creating a
docker-compose. With `spring-boot-3.2` and with `Testcontainers` dependency, it automatically creates inside
`src/test/java/«your-package»/TestJava«XYZ»Application.java` an application that can be used for local development
and will start a Testcontainer with postgres (in this setup) and keep the data between startup (with @RestartScope
annotation).

&nbsp;  
Here is a checklist you must do in order to get this setup.
&nbsp;

1. Add in `src/main/resources` a file `application-unittest.properties` and we will use an active profile `local`
   to start the application locally for the developer to manually test code quickly.
   This `application-local.properties` must contain the following:

   &nbsp;

    ```yaml
    spring.r2dbc.url=r2dbc:postgresql://localhost:5432/products
    spring.r2dbc.username=postgres
    spring.r2dbc.password=postgres
    
    spring.data.r2dbc.repositories.enabled=true
    spring.sql.init.mode=always
    spring.sql.init.schema-locations=classpath:/postgres/schema.sql
    ```

   &nbsp;     
   The `spring.sql.init.mode=always` will always execute the `schema.sql` script. So you need to write that script
   according to your needs. By default, it searches the `schema.sql` script within `src/main/resources/` but you can
   put it within another folder such as `src/main/resources/postgres/shcema.sql` and add that location within the
   `application-local.properties` under `spring.sql.init.schema-locations`.

   &nbsp;
2. Add a `schema.sql` script to `src/main/resources/postgres/` and write your own database objects.

   &nbsp;
3. Add the profile `local` by editing (in the present case) the `TestJavaFunWithTypesApplication.java`
   configuration (IntelliJ Idea); Right-click on that file, and select `Modify Run Configuration...` and add the
   profile. You can also export the profile from a terminal: `> export SPRING_PROFILES_ACTIVE=unittest`.

&nbsp;  
To run the app with testcontainers from the terminal with profile `local`:

   ```shell
      > ./mvnw spring-boot:test-run  -Dspring-boot.run.profiles=local
   ```

&nbsp;  
You can check if postgres is running within docker engine:

```shell
   > docker ps -a 
   CONTAINER ID   IMAGE                  COMMAND                  CREATED          STATUS          PORTS                                         NAMES
   a229a0bfb9ce   postgres:16.1-alpine   "docker-entrypoint.s…"   49 minutes ago   Up 13 seconds   0.0.0.0:32783->5432/tcp, :::32783->5432/tcp   db-products
```

&nbsp;  
You can also _login_ into Postgres within your running container named `db-products`.

```shell
   > docker exec -it db-products psql -U postgres
   postgres=#
```

&nbsp;  
From psql prompt, you can show the databases with `\l` and you can switch to the `products` database: `\c products`.

Now you can develop your application with a database of your choice - this is a good setup for `inner development
loop`, I believe a term coined by Thomas Vitale.

&nbsp;

#### 1.2.2 Running basic application with docker-compose

---


To run the application, we must be able to connect to a database. We can use docker-compose to start the database
within an OCI container (Docker, Rancher, Colima). We already have included "Docker Compose Support" dependencies in our
initial spring boot application (from start.springboot.io). By doing so, it automatically created a docker-compose.yml (
or compose.yaml) at the root of the project. I did rename it to `docker-compose.yml` in this project. Spring boot will
be able to pick a few name variation such as compose.yaml, docker-compose.yml, etc.

&nbsp;  
We will need to modify two files: `docker-compose.yml` and `application-local.yml` and re-use our existing
profile `local`.

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
file (IntelliJ Idea). You can then start the application within your IntelliJ Idea.

You can also start the application on the terminal:

```shell
> ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

From this setup, you can have your `inner development loop` having your database always running inside docker engine
with the ability to login into the database and verify data or modify tables or other database objects.

&nbsp;  
&nbsp;

# To see Simple Value Object in action, checkout branch `feature/value-object`

# 2. Fun with types: Simple value object: Sku

---

Principle: Make Invalid State Unrepresentable

Example: Sku - Stock Keeping Unit

We could represent a Sku in our Java program using the type String. E.g. String sku = "10000000";
However, String can have many values that wouldn't be appropriate for a Sku. In addition, we may need to apply some
validation peppered throughout the code.

We could represent a Sku with a type Integer. E.g. Integer sku = 10000000;
This is better but there are still many values that would not be appropriate: e.g. 20, -1, 1356, 02, 1000000.

The reason is that a Sku must have 8 digits and must not start with zero. Hence, a range of [10000000 - 99999999]

I have also seen some exception from a company where they had some sku starting with "B" followed by 8 digits which did
not start with zero.

Last, when we read code, String or Integer does not convey much meaning when it comes to Sku.

Here we are going to create a Sku type which will increase readability and safety. The caveat, it comes at a cost of
some added complexity - but shouldn't add complexity to the overall Domain Design.

In this installment, we will create a Sku type (value object) and we should demonstrate how to POST/GET such a Sku (
serialize and deserialize automatically). In addition, we will show how to save it to a datastore as VARCHAR. We will be
using postgres

## 2.1 Sku Type: a simple value object

To accomplish a simple value object (sometime called Alias Types such as in Kotlin), we can use class or record. Since
we do not want to modify the value once it is set, we will go with record (later we will use class because we will want
the value to be transformed upon creation).

It is always nice to provide a `of` factory method - I find it more readable than using the `new` keyword.
We also want to override the toString() as it can be convenient when passed to methods such as `println(sku)` which call
the `.toString()` internally. Last, we need to define custom serializer and deserializer. Here is what our Sku looks
like:

&nbsp;

```java
public record Sku(@NonNull String value) {

    public Sku {
        if (value.trim().length() != 8 || value.trim().startsWith("0"))
            throw new IllegalArgumentException("Sku must be made of 8 digits and not start with zero");
        // we can add validation to be only digits if needed.
    }

    // Typical "of" factory method
    public static Sku of(String value) {
        return new Sku(value);
    }

    // Nice to have:  e.g. System.out.println(sku); it will call the Sku.toString();
    @Override
    public String toString() {
        return value;
    }

    public static class SkuSerializer extends StdSerializer<Sku> {

        public SkuSerializer() {
            super(Sku.class);
        }

        @Override
        public void serialize(Sku sku, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeString(sku.value);
        }
    }

    public static class SkuDeserializer extends StdDeserializer<Sku> {

        public SkuDeserializer() {
            super(Sku.class);
        }

        @Override
        public Sku deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return Sku.of(parser.getText());
        }
    }

    @ReadingConverter
    public static class SkuReadConverter implements Converter<String, Sku> {

        @Override
        public Sku convert(String source) {
            return Sku.of(source);
        }
    }

    @WritingConverter
    public static class SkuWriteConverter implements Converter<Sku, String> {

        @Override
        public String convert(Sku sku) {
            return sku.value;
        }
    }
}
```

The Serializer and Deserializer are obviously for http Request/Response json payload. The converters are for the
database conversions.

Take a look at the source code to see how it is used within a ProductDefinition. Also take a look at
the unit test which show serialization and deserialization. The nice thing is that it does not serialize to
something like (for ProductDefinition - see code):

ProductDefinition serialized - *NOT PARTICULARLY GOOD*

```json
{
  "id": "",
  "sku": {
    "value": "10000000"
  },
  "name": "Product name"
}
```

Instead, because of our simple custom serializer, ProductDefinition serialized into:

ProductDefinition serialized - *BETTER AND SIMPLER*

```json
{
  "id": "",
  "sku": "10000000",
  "name": "Product name"
}
```

## 2.2 Adding the database layer

We are going to save and retrieve the Domain Object: ProductDefinition which for the moment is made of a UUID for the
identifier, a Sku and a name. The objective is to demonstrate how to map the Sku simple value object type to VARCHAR.

Since we are using R2DBC, we will declare the table name and the identity field as follows:

```java

@Table(name = "product_definition")
public record ProductDefinition(
        @Id UUID id,
        Sku sku,
        String name
) {

    public static ProductDefinition of(UUID id, Sku sku, String name) {
        return new ProductDefinition(id, sku, name);
    }

    @ReadingConverter
    public static class ProductDefinitionReadConverter implements Converter<Row, ProductDefinition> {

        @Override
        public ProductDefinition convert(Row row) {
            return ProductDefinition.of(
                    row.get("id", UUID.class),
                    Sku.of(row.get("sku", String.class)),
                    row.get("name", String.class)
            );
        }
    }

    @WritingConverter
    public static class ProductDefinitionWriteConverter implements Converter<ProductDefinition, OutboundRow> {

        @Override
        public OutboundRow convert(ProductDefinition pd) {
            OutboundRow row = new OutboundRow();
            if (pd.id != null) {
                row.put("id", Parameter.from(pd.id));
            }
            row.put("sku", Parameter.from(pd.sku.value()));
            row.put("name", Parameter.from(pd.name));
            return row;
        }
    }
}
```

Note that because we are not using JPA, we can use record for persistence. We also created Converters for this entity.
Once we have that, we need to create a Repository.

```java

import ca.jent.javafunwithtypes.types.ProductDefinition;

@Repository
public interface ProductDefinitionRepository extends ReactiveCrudRepository<ProductDefinition, UUID> {
    Mono<ProductDefinition> findBySku(Sku sku);
}
```

Notice that we can use the Type Sku in a query (`findBySku(Sku sku)`) because we have created Converters for Sku (see
Sku.java).

Disadvantages:

- Somewhat more complex code.
- Must define custom serializer, deserializer and reading, writing converters.

Advantage:

- More readable as Sku encapsulate conversions.
- Invalid state unrepresentable, but not at compile time.
- Can have more meaningful data structure such as Map<Sku, ProductDefinition> productBySku =...

      ASIDE
      -----

      Note that in my case here, I do assume that the actual value for the Sku will be coming from either upstream 
      service (via exposed api) or downstream service (such as a database or a queue).  This means that we can fail fast
      when it comes to Sku validation - and we should.  Anytime we are given an invalid value for a Sku we should 
      immediatly reject it and not do any further computation.

&nbsp;  
&nbsp;

# To see a more complex Value Object in action, checkout branch `feature/valueObjectBrand`

# 3. Fun with types: a more complex value object: Brand

---

A Brand such as Toyota, Samsung, Apple are common but the number of Brand today is enormous. Moreover, a Brand is "
value" oriented. A Brand rarely change. This could be a good candidate for enum but unfortunately the number of Brand
cannot really be declared in advance and if we need to add one, we must make code change and re-deploy.

I have seen in a database, entries for Brand such as `Apple`, `apple` or `APPLE`. If you have that, it makes database
queries less efficient and more cumbersome. We notice that any of these variants can be viewed as a SYMBOL: they all
mean the same thing. We usually let staff enter a Brand hence prone to consistency error. A staff may
enter `Apple`, `apple` or `APPLE`. What is needed is a type that will keep consistency for the system and allow new
value to be added. We will use the Brand type to demonstrate this.

For the case of Brand, we will use a class instead of record because we want to add a "transformation" of the value so
that we can keep as a SYMBOL. We will always use Brand, in the system, as all uppercase, including in the database.

When the Brand is display for a user (or part of a response body), we can "transform" it again to be "Capitalized"
instead of all uppercase. The principle is that the code will always see a Brand as uppercase.

```java
public final class Brand {
    // value will hold the actual value for Brand
    private final String value;

    // private constructor as we want to control the creation of Brand
    private Brand() {
        this.value = null;
    }

    private Brand(String value) {
        // A new Brand will always be uppercase
        this.value = value.trim().toUpperCase();
    }

    // the only way to create a Brand
    public static Brand of(String brand) {
        return new Brand(brand);
    }

    // Nice to have: toString()
    @Override
    public String toString() {
        return value;
    }

    // No setter: immutable
    public String getValue() {
        return value;
    }

    // We control the serialization and deserialization
    // example:
    // {                         AND NOT   {
    //   "sku" : "10000000",                  "sku" : "10000000",
    //   "brand" : "Apple"                    "brand" : {
    // }                                            "value" : "Apple"
    //                                         }
    //                                      }
    public static class BrandSerializer extends StdSerializer<Brand> {

        public BrandSerializer() {
            super(Brand.class);
        }

        @Override
        public void serialize(Brand brand, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            gen.writeString(brand.getValue());
        }
    }

    public static class BrandDeserializer extends StdDeserializer<Brand> {

        public BrandDeserializer() {
            super(Brand.class);
        }

        @Override
        public Brand deserialize(JsonParser parser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
            return Brand.of(parser.getText());
        }
    }

    // We use Converter (for the database) such that when our Repository will have: findByBrand(Brand brand)
    @ReadingConverter
    public static class BrandReadConverter implements Converter<String, Brand> {

        @Override
        public Brand convert(String value) {
            return Brand.of(value);
        }
    }

    @WritingConverter
    public static class BrandWriteConverter implements Converter<Brand, String> {

        @Override
        public String convert(Brand brand) {
            return brand.value;
        }
    }
}

```

See `ProductDefinitionControllerTest.java` to see E2E test.

We can see that Brand is similar to Sku, but we needed to use a class instead of record so that we could control the
transformation of the encapsulated value.

# Checkout the branch `feature/enums`

# 4. Fun with types: SYMBOLS

An enum is a collection of fixed SYMBOLS that help human to give meaning (sequence of chars forming the symbol) and the
compiler which will only accept for the given type (enum) one of the symbol in the collection. We need to remember that
all values within an enums are singleton and thus only one instance ever exist. We usually use enums when we control the
possible values i.e. no values comes from the outside world and is controlled by the business. This is in contrast to
Brand that we previously worked on, where the values always depended on the outside world.

The serialization and deserialization is taken care by Jackson library and so we have nothing to implement for handling
enums. However, for R2DBC, we need to expand our existing converter for the ProductDefinition. Since this is very
simple, see the source code to see the changes.

# Checkout the branch `feature/weight`

# 5. Fun with types: Weight

The abstraction of `Weight` for a retail store mean how heavy a product is. This weight information is usually used when
the product need to be transported, stored or as information for the customer. We can use Float for those weight values
without worrying to overflow. But the abstraction of weight is not just a decimal number. A unit-of-weight also need to
be specified and considered. Although the weight of an object does not change (on Earth), the meaning of the decimal
value for human changes, and depends on the unit of weight used. To make matter worst, we have two system of weight:
imperial and metric.

The branch `feature/weight` show an implementation which we believe is readable and consistent. The weight is always
standarized to GRAM in the persistence store. This way we can create consistent query. The original unit-of-weight is
also stored in order to re-construct the original weight/unit for a product definition.

The serialization/deserialization of Weight has the following schema:

```
{
   "value" : decimal,
   "unit" : oneOf("MILLIGRAM","GRAM","KILOGRAM","LBS","OUNCE")
}
```

To see this in action, run the `ProductDefinitionControllerTest.java`.