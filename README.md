# itau-deploys-helper

Repositorio con algunas utilidades para armar los despliegues a los ambientes homologación y producción.

## Requisitos

- JDK 11
- SBT 1.9.6 (se puede usar a través de IntelliJ IDEA, sin instalarlo manualmente)

## Utilidades

### Revisar dependencias (llamados REST) de un servicio

Esto es útil para saber a qué otros servicios llama un BFF para luego configurar las network policies correctamente.

```shell
BASE_REPOSITORIES_PATH={path} sbt "runMain com.itau.deploys.devops.checkMSDependenciesMain"
```

donde `BASE_REPOSITORIES_PATH` es un path a un directorio donde se encuentren todos los repositorios.

Dentro de la clase `CheckMSDependencies` se encuentra el atributo `repositories`, el cual es una lista que contiene los
repositorios a revisar. La idea es que cada uno de estos repositorios se encuentre en el tag correspondiente a promover
a ambientes altos para poder ser analizados. La aplicación simplemente escanea el código fuente contra una expresión
regular para encontrar variables que indiquen URLs externas.

Como resultado de la ejecución se obtendría un reporte de este tipo:

```text
{path}/itXXXX-a-repo
		 - users.api.uri
		 - enterprise-payments.api.uri

{path}/itYYYY-another-repo
		 - enterprise-payments.api.uri
		 - provider-payments.api.uri
		 - multiple-salary-payments.api.uri
```

### Revisar componentes Typescript (frontend Angular)

Analiza el código fuente del frontend en busca de imports duplicados (un code smell reportado por Sonar) y posibles
memory leaks relacionados con `Subscriptions` (componentes que los tienen y no llaman al método `unsubscribe`).

```shell
HBE_FRONT_APP_DIRECTORY={path} sbt "runMain com.itau.deploys.devops.checkTSComponentsMain"
```

donde `HBE_FRONT_APP_DIRECTORY` es un path al directorio donde se encuentra la carpeta app del frontend client
(`{path-al-repo-del-front}/clients/projects/client/app`).

Como resultado de la ejecución se obtiene un reporte de la forma:

```text
contact-add.component.ts
	 - Duplicated import: client/app/app/models
bind-user-form.component.ts
	 - Duplicated import: client/app/app/constants
soft-token-email-confirmation.component.ts
	 - Possible memory leak (subscriptions = 1)
...
```

### Generar reporte de versiones

Retorna un listado con las versiones de los servicios desplegados en homologación y en producción.

```shell
API_DEVOPS_BASE_URL={url} sbt "runMain com.itau.deploys.devops.generateVersionsReportMain"
```

donde `API_DEVOPS_BASE_URL` es la URL del servicio de consulta dispuesto por devops. Además, dentro de la
clase `GenerateVersionsReport` se encuentra el atributo `appsToLookFor`, el cual es un listado de los servicios para los
cuales recuperar las versiones.

A continuación se muestra un reporte de ejemplo:

```text
SERVICIO                      | VERSION HOMO       | VERSION PROD       
------------------------------|--------------------|--------------------
bff-agreement-payments        | 0.3.3              | 0.1.0              
api-provider-payments         | 0.10.0             | 0.9.0              
```

### Consultar merge requests relacionados con historias de usuario

Obtiene el listado de merge requests del API de Gitlab y filtra aquellos que coinciden con las historias de usuario
indicadas.

```shell
GITLAB_BASE_URL={url} GITLAB_BEARER_TOKEN={token} sbt "runMain com.itau.deploys.devops.queryMergeRequestsMain"
```

donde `GITLAB_BASE_URL` es la URL del API de Gitlab y `GITLAB_BEARER_TOKEN` es un token para poder realizar solicitudes
autenticadas contra el API de Gitlab.

Dentro de la clase `QueryMergeRequests` existen dos atributos de interés:

1. `baseQueryParams`: Incluye los parámetros base, entre ellos a partir desde qué fecha consultar los
   MRs (`created_after`).
2. `userStoriesNumbers`: Listado de números de historias de usuario de Jira Cloud. NOTA: Sólo indicar los números; el
   programa ya agrega el prefijo `NHEP`.

### Mostrar network policies existentes desde JSON de Openshift

Genera un reporte donde se listan las network policies por servicio destino.

```shell
sbt "runMain com.itau.deploys.devops.showExistingNetworkPoliciesMain"
```

### Revisar si los servicios están configurados en v3

__En construcción__
