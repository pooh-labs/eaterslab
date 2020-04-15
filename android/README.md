# Android client application

Android app is written with Kotlin language as the client endpoint for EatersLab system. App allows users to search for nearby or favourite canteens and check occupancy data and other available information for cafeterias.
App is designed for min SDK 21 version so Android Lollipop or newer devices are supported (so really few devices are unsupported)

## App building instructions

App is developed in AndroidStudio and for development should be imported as standard Android app project. Before building the project and indexing files in IDE the generation process is needed.

### Client model generation
All the client model classes need to be generated based on the API specification YAML file which is publicly available. 

The generator script is available in the main android project directory. The script gets single parameter with API specification file location in filesystem.  The script is configured to generate classes usable for Kotlin Android project so [threetenbp date library](https://github.com/ThreeTen/threetenbp) is used for date objects handling in model.

Generation process can be run for *api.yaml* file with
```bash
./api_generate_kotlin.sh api.yaml
```
The generated classes are automatically moved to client model folder in android/app/src/main/java/labs/pooh/eaterslab/client. If needed the script can be easily modified to generate for example Java model as the standard OpenAPI generator is used in this project.
