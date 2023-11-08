## Testing

Install Appium

    yarn global add appium   

Start Appium

    appium

Start emulator

    export AVD_NAME=AndroidDeviceE2E
    avdmanager delete avd --name $AVD_NAME
    avdmanager create avd --force --name $AVD_NAME --abi google_apis/x86 --package 'system-images;android-29;google_apis;x86' --device "Nexus 6P"
    emulator -avd $AVD_NAME

Run tests:

    ./gradlew clean :e2e:testDebugUnitTest


## Scenarios that require manual testing

### Sign with key that requires authentication, and user got authenticated
### Sign with key that requires authentication, and user not authenticated
### Sign with key that requires authentication, but new biometrics got enrolled
### Sign with key that requires authentication, but no biometrics enrolled

### Encrypt symmetrically with key that requires authentication, and user got authenticated
### Encrypt symmetrically with key that requires authentication, and user not authenticated
### Encrypt symmetrically with key that requires authentication, but new biometrics got enrolled
### Encrypt symmetrically with key that requires authentication, but no biometrics enrolled

### Decrypt symmetrically with key that requires authentication, and user got authenticated
### Decrypt symmetrically with key that requires authentication, and user not authenticated
### Decrypt symmetrically with key that requires authentication, but new biometrics got enrolled
### Decrypt symmetrically with key that requires authentication, but no biometrics enrolled

### Decrypt asymmetrically with key that requires authentication, and user got authenticated
### Decrypt asymmetrically with key that requires authentication, and user not authenticated
### Decrypt asymmetrically with key that requires authentication, but new biometrics got enrolled
### Decrypt asymmetrically with key that requires authentication, but no biometrics enrolled
