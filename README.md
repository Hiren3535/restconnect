# restconnect

1) Add this to Project Level Gradle (build.gradle(Module: app))
      ```java
        allprojects {
            repositories {
                  ...
                  maven { url 'https://jitpack.io' }
            }
        }
	
2)  In Same file

      ```java
        dependencies 
        {
                compile 'com.github.Hiren3535:restconnect:v1.4'
        }
    
3)  In your Activity.java

    ```java
     ArrayMap<String, Object> postParams; 
      postParams = new ArrayMap<>(); 
      postParams.put("email", inputEmail.getText().toString());             
      RestClientHelper.getInstance().pos("URL", postParams, new RestClientHelper.RestClientListener() {
        @Override 
        public void onSuccess(String response) {

        }
        @Override
        public void onError(String error) {
           
        }
    });

