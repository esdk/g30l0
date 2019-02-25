# Training Setup
Steps to do to setup for an ESDK Training

## Preparation
* Create an empty Github repository `esdk-training` within the [ESDK organization](https://github.com/esdk).
* Add the new repository `esdk-training` to the `training` team with `Write` permissions.
* Ask for Github accounts of training participants. Add all participant's Github accounts to the `training` team.
* Activate Travis CI and Coveralls for the repository `esdk-training`.
* Add https://registry.abas.sh and tenant credentials to Travis CI.
* Create a new project within the repository `esdk-training` called `Training` (Template = Basic Kanban). 
* Add the following issues for the repository `esdk-training` and add `Training` as project to each of them:
  * \#1 Create Infosystem G30L0:
  ```
  Create an Infosystem with search word `G30L0`, classname `GeoLocation`, working directory `ow1` with the following fields:
  * iscustomersel: GL20 (Suchwort)
  * iszipcodesel: GL20 (Postleitzahl)
  * iscustomer: VPKS17 (Geschäftspartner)
  * isstate: PS97:1 (Land)
  * iszipcode: GL20 (Postleitzahl)
  * istown: GL20 (Stadt)
  * islongitude: GL30 (Längengrad)
  * islatitude: GL30 (Breitengrad)
  ```
  * \#2 Display customer info:  
  ```
  For all customers matching the search word pattern provided in `customersel` display the basic information (reference to customer object, zip code, town, country).
  ```
  * \#3 Display trading partner info:  
  ```
  For all trading partners matching the search word specified in `customersel` display the basic info in the infosystem (reference to trading partner, zip code, town, country).
  Make use of the `TradingPartner` interface, which is applied to `Customer`, `CustomerContact`, `Vendor` and `VendorContact`.
  ```
  * \#4 Select based on zip code:  
  ```
  Display all trading partners who's zip code matches the value in `zipcodesel` or the search word pattern in `customersel`.
  ```
  * \#5 Display geo location:  
  ```
  Display the geo location (longitude and latitude) for each table row in the infosystem.
  Use PowerMock to write a unit test that mocks both, the TradingPartner object and the Nominatim web service:
  \```groovy
  testCompile "org.powermock:powermock-module-junit4:1.6.2"
  testCompile "org.powermock:powermock-api-mockito:1.6.2"
  \``` 
  Use the [Nominatim Java API](https://github.com/jeremiehuchet/nominatim-java-api) by adding the following dependency to your `build.gradle` file: 
  `compile "fr.dudie:nominatim-api:3.3"`.
  Using the Nominatim Java API:
  \```java
  NominatimClient jsonNominatimClient = new JsonNominatimClient(new DefaultHttpClient(), "some@email.com");
  return jsonNominatimClient.search("searchString");
  \```
  In order for Jacoco to pick up on the code coverage of a PowerMocked test, the classes need to be instrumented before the tests are run. Since Gradle does not support this yet, the following is needed in the `build.gradle` file:
  \```
  task instrument(dependsOn: [classes, project.configurations.jacocoAnt]) {
  
  	inputs.files classes.outputs.files
  	File outputDir = new File(project.buildDir, 'instrumentedClasses')
  	outputs.dir outputDir
  	doFirst {
  		project.delete(outputDir)
  		ant.taskdef(
  				resource: 'org/jacoco/ant/antlib.xml',
  				classpath: project.configurations.jacocoAnt.asPath,
  				uri: 'jacoco'
  		)
  		def instrumented = false
  			if (file(sourceSets.main.java.outputDir).exists()) {
  				def instrumentedClassedDir = "${outputDir}/${sourceSets.main.java}"
  				ant.'jacoco:instrument'(destdir: instrumentedClassedDir) {
  					fileset(dir: sourceSets.main.java.outputDir, includes: '**/*.class')
  				}
  				//Replace the classes dir in the test classpath with the instrumented one
  				sourceSets.test.runtimeClasspath -= files(sourceSets.main.java.outputDir)
  				sourceSets.test.runtimeClasspath += files(instrumentedClassedDir)
  				instrumented = true
  			}
  		if (instrumented) {
  			//Disable class verification based on https://github.com/jayway/powermock/issues/375
  			test.jvmArgs += '-noverify'
  		}
  	}
  }
  test.dependsOn instrument
  \```
  ```
  * \#6 Add a customer's geo location via the customer screen:
  ```
  Via a button `yg30l0calcgeoloc` the geo location can be determined and registered from the customer screen.
  ```
  * \#7 Add licensing:
  ```
  A valid license is required upon pressing the start button in the G30L0 Infosystem and upon pressing the `yg30l0calcgeoloc` button in the Customer screen.
  Use the [ESDK client api] (https://documentation.abas.cloud/en/esdk/#_use_the_esdk_client_api) for validating the license.
  
  To make the tests run a cloud-connect installation is necessary, for Travis CI add the following to the `.travis.yml` file:
  \```
  - mkdir -p cloud-connect
  - wget -qO- 'https://esdk-hybrid.eu.abas.cloud/cloud-connect.tgz' | tar xfz - --directory cloud-connect
  - sed -e "s/hostname/$(hostname)/g" ci/configuration.json > cloud-connect/configuration.json
  - cloud-connect/setup.sh -n --tenant-configuration $TENANT_SECRET
  - cloud-connect/run.sh
  \```
  
  Also add the file `configuration.json` to the `ci` directory within your project:
  \```json
  {
    "version": 1,
    "installation_mode": 2,
    "tenant": "esdk-hybrid",
    "domain": "eu.abas.cloud",
    "aws_region": "eu-central-1",
    "stage": "prod",
    "options": {
      "useSSH": true,
      "docker_host": "hostname",
      "cloud_connect_host": "hostname",
      "awslogsEnabled": "true"
    },
    "apps": {
      "screenFetcher": {
        "install": false,
        "generate_all_screens": false
      }
    },
    "add_ons": {
      "fts": {
        "install": false
      }
    },
    "services": {
      "environment": {},
      "license_server": {
        "enabled": true
      },
      "jwt": {
        "port": 19951,
        "userSyncMode": "full",
        "workspaces": "26a",
        "permissions": "26"
      },
      "workflow": {
        "host": "localhost",
        "port": 8088,
        "enabled": false
      },
      "bapps": {
        "host": "localhost",
        "port": 9990,
        "enabled": false
      },
      "search": {
        "host": "localhost",
        "port": 9200,
        "enabled": false
      },
      "erp": {
        "host": "hostname",
        "port": 6560,
        "user": null,
        "ssh": {
          "user": "s3",
          "port": 2205
        },
        "password": "sy"
      }
    }
  }
  \```
  
  Due to an open issue you have to add the following lines to your `build.gradle` file in order to have the test coverage calculated correctly:
  \```groovy
  test {
  	jacoco {
  		excludes += ['**/*']
  	}
  }
  
  calculateCodeCoverage {
  	...
  	afterEvaluate {
  		classDirectories = files(classDirectories.files.collect {
  			fileTree(it).matching {
  				include "de/abas/esdk/g30l0/**/*.class"
  			}
  		})
  	}
  }
  
  codeCoverageVerification {
  	afterEvaluate {
  		classDirectories = files(classDirectories.files.collect {
  			fileTree(it).matching {
  				include "de/abas/esdk/g30l0/**/*.class"
  			}
  		})
  	}
  }
  \```
  ```

## Cleanup
To cleanup after the training delete the `esdk-training` repository including the `Training` project.
Also delete the `training` team and revoke access to the organization for all training participants.
