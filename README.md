 

# Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

**NOTE**: for the dev configuration there is a database seed.

There are three books and three members inserted into the database whenever the application starts with the above command.

# Testing the APIs

## swagger-ui

Once started, the quarkus server serves the swagger-ui on the local URL http://127.0.0.1:8080/q/swagger-ui

You can explore and test the API endpoints there.

## Bruno (postman alternative)

The service features an auxilliary Bruno library that you can use for testing the API endpoints.

You can open the Bruno collection by opening the library/Bruno folder in Bruno.

## Curl

You can also use the below curl commands, with the relevant variables adjusted to your liking.

### Get book by ID
```
curl --request GET \
  --url http://127.0.0.1:8080/api/books/b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11
```

### Get all books
```
curl --request GET \
  --url http://127.0.0.1:8080/api/books
```

### Search for books by author and/or title
```
curl --request GET \
  --url 'http://127.0.0.1:8080/api/books/search?author=&title=Dune'
```

### Add a book
```
curl --request POST \
  --url http://127.0.0.1:8080/api/books \
  --header 'content-type: application/json' \
  --data '{
  "title": ">",
  "author": "5",
  "isbn": "G",
  "publicationYear": 1000,
  "totalCopies": 1
}'
```

### Update book
```
curl --request PUT \
  --url http://127.0.0.1:8080/api/books/6f34cfc0-b1f9-4f20-855d-2fe2c4756e1b \
  --header 'content-type: application/json' \
  --data '{
  "title": ";",
  "author": "u",
  "isbn": "C",
  "publicationYear": 1000,
  "totalCopies": 3
}'
```

### Delete a book
```
curl --request DELETE \
  --url http://127.0.0.1:8080/api/books/6f34cfc0-b1f9-4f20-855d-2fe2c4756e1b
```

### Get all checkouts belonging to a specific member
```
curl --request GET \
  --url 'http://127.0.0.1:8080/api/checkout?memberId=a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'
```

### Borrow a book
```
curl --request POST \
  --url http://127.0.0.1:8080/api/checkout \
  --header 'content-type: application/json' \
  --data '{
  "bookId": "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11",
  "memberId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
}'
```

### Return a book
```
curl --request POST \
  --url http://127.0.0.1:8080/api/checkout/2bd48431-e6f3-444f-9d34-108d8ddf8a13/return \
  --header 'content-type: application/json' \
  --data '{
  "bookId": "b0eebc99-9c0b-4ef8-bb6d-6bb9bd380b11",
  "memberId": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
}'
```

### Get a member by ID
```
curl --request GET \
  --url http://127.0.0.1:8080/api/members/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11
```

### Get a list of members
```
curl --request GET \
  --url http://127.0.0.1:8080/api/members
```

### Add a new member
```
curl --request POST \
  --url http://127.0.0.1:8080/api/members \
  --header 'content-type: application/json' \
  --data '{
  "firstName": ".",
  "lastName": "i",
  "email": "="
}'
```
