# Fun with types:  ValueObject/Alias types, Nested Type 

### The minimal setup to get the application started locally using testcontainers for postgres

If you are using Mac M1/M2/M3 and you don't want to use Docker Desktop (e.g. company policy), you can alternatively 
use `colima`

```
> brew install colima
> colima start --arch aarch64 --vm-type vz --vz-rosetta
```

You will need a docker engine running (Docker Desktop or colima) before you can start the application.

Checkout the `feature/min-testcontainers-setup` to see the minimum configuration you must provide to get the 
application up and running locally.  Note there is no new classes but just configurations; mostly about postgres.

```
> git checkout feature/min-testcontainers-setup
```

To run the app:

```
> ./mvnw spring-boot:test-run
```