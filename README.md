# otp-business-capability

La especificación técnica de este servicio se encuentra [aquí](https://github.com/piso19/architecture/tree/master/otp) 

### Documentation 

[Swagger UI sandbox](http://lbk-nlb-sand-b4ae09a807e738b7.elb.us-east-1.amazonaws.com:8083/otp/swagger-ui/#/)

Swagger URL pattern: 

```${LoadBalancerURL}:8083/otp/swagger-ui/#/```

## Inicio Rápido 

Iniciar los servicios y componentes de los cuales éste servicio depende. Para esto, corremos el siguiente comando, 
estando en el path `localstack-otp/`. (*Nota*: Si es la primera vez se puede requerir autenticarse en el ecr de lulo, 
[Ver cómo](#login-en-el-ecr-lulo))

```
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 782913492063.dkr.ecr.us-east-1.amazonaws.com
docker-compose up
```

Este comando levantará localmente los siguientes servicios y los aprovisionará con datos e infrastructura de acuerdo 
a la configuración.

- *Mockserver*: Inicia un [mockserver](https://webcache.googleusercontent.com/search?q=cache:twNoVOoJfSIJ:https://www.mock-server.com/where/docker.html+&cd=1&hl=en&ct=clnk&gl=co)
que permitirá definir respuestas mock para simular las  respuestas de los servicios de los que se depende   
- Redis
- Localstack con SQS y SNS 

Ver sección [Aprovisionamiento de Ambiente Local](#aprovisionamiento-de-ambiente-local) para mas detalles sobre cómo aprovisionar infraestructura 
y datos en el ambiente local.

Una vez los servicios hayan iniciado correctamente, ya se podrá correr el servicio localmente en modo `dev` 
ejecutando el siguiente comando, estando en la raiz del proyecto.

```
./gradlew bootRun --args='--spring.profiles.active=dev'
```

El servicio ya debe estar corriendo y listo para aceptar algunos request.

- Generar un OTP para hacer una transferencia.

```curl
curl -X POST \
  http://localhost:8083/otp/v3/id-client/generate/TRANSFER \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
	"target": "example-target!",
	"amount": 123456789
}'
```

- Validar el OTP para una transferencia, usando la verificación por body

```
curl -X POST \
  http://localhost:8083/otp/v3/id-client/verify/TRANSFER \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'otp-token: 1129' \
  -d '{
	"target": "example-target!",
	"amount": 123456789
}'


curl -v -X POST \
  http://localhost:8083/otp/v3/id-client/verify/checksum/TRANSFER \
  -H 'Accept: */*' \
  -H 'Content-Type: application/json' \
  -H 'otp-token: 1360:eyJ0YXJnZXQiOiJleGFtcGxlLXRhcmdldCEiLCJhbW91bnQiOjEyMzQ1Njc4OX0=' \
  -d '{

        "target": "example-target!",
        "amount": 123456789

}'
```

## Aprovisionamiento de ambiente local 

Hay dos formas de aprovisionar el ambiente local; durante el proceso de startup y una vez los servicios ya se encuentran
corriendo con el `docker-compose`

#### 1. Aprovisionamiento durante el startup  

Para agregar cambios en la infra de forma permanente y se garantice su aprovisionamiento al iniciar los servicios.
Esta configuración se encuentra alojada en el path `localstack-otp/setup`. A continuacion se describen estos archivos.

- `local-setup.sh`: Este archivo tiene las configuraciones para crear los recursos de los diferentes servicios que se 
correran localmente, ej. Agregar nuevas colas a SQS, tablas Dynmo, etc.
- `mockserver-expecs.json`: Definiciones (expectations) de Mockserver que serán cargadas durante el proceso de arranque 
de este componente. [Mas información](https://www.mock-server.com/mock_server/initializing_expectations.html)
- `sqs-config.json`: Definición de configuraciones para las colas sqs. 




#### 2. Aprovisinamiento durante runtime

Para cambios temporales y de pruebas, se puede usar este forma de aprovisionamiento.
- Agregar nuevas colas

```shell script

docker exec -it otp-bc_localstack_1 awslocal sqs create-queue --queue-name new-queue --attributes file:///setup/sqs-config.json

```

- Agregar respuestas mock al mockserver (ej. Simulando la interación con clients) 

```shell script
curl -v -X PUT "http://localhost:1080/mockserver/expectation" -d '{
    
    "httpRequest": {
        "path": "/clients.*"
    },
    
    "httpResponse": {
        "headers": {
            "content-type": ["application/json"]
        },
        "body": {
            "content": {
                        "phonePrefix": 57,
                        "emailAddress": "jorgetovar621@gmail.com",
                        "phoneNumber": "3203716258"    
            }
        }
    }
}'
```

## Login en el ECR Lulo 

El ambiente local hace uso de una imagen customizada de [localstack](https://github.com/localstack/localstack) para tener 
 localmente una versión reducida de los servicios que utiliza el servicio de AWS. Para acceder a esta image, es necesario 
 autenticarse en la cuenta de aws de la siguiente forma.
 
 - Configurar las credenciales de acceso a la cuenta Sandbox
 
```
export AWS_ACCESS_KEY_ID="ASIA4K6QXXXXXXXXXXXX"
export AWS_SECRET_ACCESS_KEY="1cpnpySEbvzx4KtjauDgGqKqR0guxxxxxxxxxxxx"
export AWS_SESSION_TOKEN="IQoJb3JpZ2luX2VjEP3//......................"
```

- Ya con las credenciales de acceso configuradas, se puede hacer login con el cliente docker usando el siguiente comando

```
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 848155883266.dkr.ecr.us-east-1.amazonaws.com
```
Usualmente, eso solo será requerido la primera vez y cada vez que haya una actualización de la imagen `localstack-lulo`,
dado que la imagen se descarga localmente.


## Comandos utilitarios

- Realizar pruebas de las respuestas configuradas en el mockserver

```curl
curl -X GET \
  http://localhost:1080/clients/idClient/id-client-postman \
  -H 'Accept: */*' \
  -H 'Accept-Encoding: gzip, deflate' \
  -H 'Authorization: mock-token' \
  -H 'Cache-Control: no-cache' \
  -H 'Connection: keep-alive' \
  -H 'Custom-Token: my-custom-token' \
  -H 'Host: localhost:1080' \
  -H 'Postman-Token: cfd54d13-27bd-4375-9f4a-fa5addcec089,aa1e27db-cd4b-4469-b9ed-100c1bb9d9ab' \
  -H 'User-Agent: PostmanRuntime/7.18.0' \
  -H 'cache-control: no-cache'
```

## Referencias

- [Repositorio de arquitectura](https://github.com/piso19/architecture)
- [Development guidelines Lulo ](https://github.com/piso19/guidelines)
- [Mockserver](https://www.mock-server.com/mock_server/getting_started.html)
- [Redis](https://hub.docker.com/_/redis/)
- [Localstack](https://hub.docker.com/r/localstack/localstack/)
