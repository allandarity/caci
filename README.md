### Starting the application

To run the application from the root directory run 
`docker-compose up` this will give you a databse with the spring boot application running.

If you don't wish to pacakge the full application together, or, you just wish to run the tests for the application you just have to start a postgre image. You can do this by running `podman run --name some-postgres -e POSTGRES_PASSWORD=password -e POSTGRES_USER=elliott -e POSTGRES_DB=etest -d -p 5432:5432 postgres` or the alternate docker command if you use docker and then start the application as you would normally (via IDE configuration or terminal)

### Endpoints

POST `/order/createOrder`  
Body `CreateOrderRequestBody`
Creates an order for a given user id and amount. If user id doesn't exist or isn't supplied a new user id is created and returned.

GET `/order/orderByOrderId/{id}`   
Returns order information for a supplied id. If id doesn't exist or order can't be found returns server error to prevent fishing for orders via id generation.

GET `/order/ordersByCustomerId/{id}`   
Returns all order information for a given user id. Returns server internal error to stop fishing for user ids if something can't be found.

PUT `/order/update`   
Body `UpdateOrderRequestBody`   
Updates the amount of item purchased of a given order. Checks that the order being updated is owned by the user making the request. Will not update any order that is already marked as `dispatched`

PUT `/fulfil/update`   
Body `UpdateFulfilmentRequestBody`   
Updates a given order state to state provided in body.
