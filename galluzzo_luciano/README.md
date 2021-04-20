LUCIANO GALLUZZO - DESAFIO QUALITY

El proyecto cuenta con 2 controladores **HotelController** y **FlightController**

- En HotelController podemos realizar lo siguiente

- Obtener una lista de todos los hoteles:

Para eso debemos hacer un **GET** con la siguiente ruta "/api/v1/hotels". Aca obtendremos una lista de todos los hoteles, incluso los ya reservados

- Obtener una lista de hoteles filtrado por fecha y ciudad:

Para eso debemos hacer un **GET** con la siguiente ruta "/api/v1/hotels?dateFrom=DD/MM/YYYY&dateTo=DD/MM/YYYY&destination=cityName". Aca obtendremos una lista solamente de los hoteles disponibles que cumplan con esas condiciones
