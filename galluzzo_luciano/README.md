LUCIANO GALLUZZO - DESAFIO QUALITY

El proyecto cuenta con 2 controladores `HotelController` y `FlightController`

**En HotelController podemos realizar lo siguiente:**

- Obtener una lista de todos los hoteles:

Para eso debemos hacer un **GET** con la siguiente ruta `/api/v1/hotels` Aca obtendremos una lista de todos los hoteles, incluso los ya reservados

- Obtener una lista de hoteles filtrado por fecha y ciudad:

Para eso debemos hacer un **GET** con la siguiente ruta `/api/v1/hotels?dateFrom=DD/MM/YYYY&dateTo=DD/MM/YYYY&destination=cityName` Aca obtendremos una lista solamente de los hoteles disponibles que cumplan con esas condiciones

- Reservar una habitación:

Para eso debemos hacer un **POST** a la siguiente ruta `/api/v1/booking` y enviar en un json un payload como el del siguiente ejemplo:
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

**En FlightController podemos realizar lo siguiente:**

- Obtener una lista de todos los vuelos:

Para eso debemos hacer un **GET** con la siguiente ruta `/api/v1/flights` Aca obtendremos una lista de todos los vuelos.

- Obtener una lista de vuelos filtrado por fecha  y ruta:

Para eso debemos hacer un **GET** con la siguiente ruta `/api/v1/flights?dateFrom=DD/MM/YYYY&dateTo=DD/MM/YYYY&origin=cityName&destination=cityName`

- Reservar un vuelo:

Para eso debemos hacer un **POST** a la siguiente ruta `/api/v1/flight-reservation` y enviar en un json un payload como el del siguiente ejemplo
```json

{
    "userName" : "seba_gonzalez@unmail.com",
    "flightReservation" : {
        "dateFrom" : "10/02/2021",
        "dateTo" : "15/02/2021",
        "origin" : "Buenos Aires",
        "destination": "Puerto Iguazú",
        "flightNumber" : "BAPI-1235",
        "seats" : 2,
        "seatType" : "ECONOMY",
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

Obtenemos como respuesta un json como el de éste ejemplo:

```json
{
    "userName": "seba_gonzalez@unmail.com",
    "amount": 65000.0,
    "interest": 0.1,
    "total": 71500.0,
    "flightReservation": {
        "dateFrom": "10/02/2021",
        "dateTo": "15/02/2021",
        "origin": "Buenos Aires",
        "destination": "Puerto Iguazú",
        "flightNumber": "BAPI-1235",
        "seats": 2,
        "seatType": "ECONOMY",
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

Tanto en al reserva de vuelos como de hoteles podemos obtener alguna de éstas excepciones:
- Formato de Email invalido
- Vuelo/Hotel inexistente
- Destino inexistente
- Habitación invalida (en el caso de hoteles)
- Tipo de pago invalido
- El hotel o vuelo no tiene disponibilidad para la fecha y condiciones solicitadas

Además. en la consulta de hoteles o vuelos podemos obtener alguna de las siguiente excepciones:
- Formato de fecha incorrecto
- La busqueda no obtuvo ningun resultado
- Destino inexistente
- La fecha de fin debe ser mayor a la fecha de inicio
- Faltan parametros en la búsqueda

**TEST COVERAGE**

El código tiene una cobertura del `80%` de las líneas. Las clases testeadas son las siguientes:
- Flight/Hotel Controller: Se prueban unitariamente los métodos del controller haciendo un mock al service
- Flight/Hotel Service: Se prueban unitariamente los métodos del service haciendo un mock al repositorio (éstas clases utilizan otras clases utils que no están mockeadas ya que no se instancian y no figuran en el constructor)
- Flights/Hotel Repository: Se prueban unitariamente los métodos del repository
- InterestUtil: Se prueban unitariamente los métodos de la clase utilitaria que sirve para validar los intereses del método de pago.
