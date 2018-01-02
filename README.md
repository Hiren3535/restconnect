# restconnect


1)	allprojects {
    		repositories {
        		maven { url 'https://jitpack.io' }
    		}
	}

2)	compile 'com.github.Hiren3535:restconnect:v1.4'

3)    

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

