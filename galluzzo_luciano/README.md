LUCIANO GALLUZZO - DESAFIO QUALITY

El proyecto cuenta con 2 controladores **HotelController** y **FlightController**

- En HotelController podemos realizar lo siguiente

- Obtener una lista de todos los hoteles:

Para eso debemos hacer un **GET** con la siguiente ruta "/api/v1/hotels". Aca obtendremos una lista de todos los hoteles, incluso los ya reservados

- Obtener una lista de hoteles filtrado por fecha y ciudad:

Para eso debemos hacer un **GET** con la siguiente ruta "/api/v1/hotels?dateFrom=DD/MM/YYYY&dateTo=DD/MM/YYYY&destination=cityName". Aca obtendremos una lista solamente de los hoteles disponibles que cumplan con esas condiciones

- Reservar una habitación

Para eso debemos hacer un **POST** a la siguiente ruta "/api/v1/booking" y enviar en un json un payload como el del siguiente ejemplo:
```json
{
  "userName" : "seba_gonzalez@unmail.com",
  "booking" : {
    "dateFrom" : "10/02/2021",
    "dateTo" : "23/02/2021",
    "destination" : "Buenos Aires",
    "hotelCode" : "HB-0001",
    "peopleAmount" : 1,
    "roomType" : "SINGLE",
    "people" : [
      {
        "dni" : "12345678",
        "name" : "Pepito",
        "lastName" : "Gomez",
        "birthDate" : "10/11/1982",
        "mail" : "pepitogomez@gmail.com"
      },
      {
        "dni" : "13345678",
        "name" : "Fulanito",
        "lastName" : "Gomez",
        "birthDate" : "10/11/1983",
        "mail" : "fulanitogomez@gmail.com"
      }
    ],
    "paymentMethod" : {
      "type" : "CREDIT",
      "number" : "1234-1234-1234-1234",
      "dues" : 6
    }
  }
}
```
Como respuesta obtendremos un json con los datos de la reserva como en éste ejemplo:
```json
{
    "userName": "seba_gonzalez@unmail.com",
    "amount": 70655.0,
    "interest": 0.1,
    "total": 77720.5,
    "booking": {
        "dateFrom": "10/02/2021",
        "dateTo": "23/02/2021",
        "destination": "Buenos Aires",
        "hotelCode": "HB-0001",
        "peopleAmount": 1,
        "roomType": "SINGLE",
        "people": [
            {
                "dni": "12345678",
                "name": "Pepito",
                "lastName": "Gomez",
                "birthDate": "10/11/1982",
                "mail": "pepitogomez@gmail.com"
            },
            {
                "dni": "13345678",
                "name": "Fulanito",
                "lastName": "Gomez",
                "birthDate": "10/11/1983",
                "mail": "fulanitogomez@gmail.com"
            }
        ],
        "paymentMethod": {
            "type": "CREDIT",
            "number": "1234-1234-1234-1234",
            "dues": 6
        }
    },
    "statusCode": {
        "code": 200,
        "message": "El proceso termino satisfactoriamente"
    }
}
```
Aca podemos obtener excepciones si enviamos un email con formato incorrecto, si no encuentra hoteles con esas caracteristicas, si el destino no existe, etc.
