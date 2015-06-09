#include <stdio.h>
#define STEP 20
main(){
float farenheit;	
for(farenheit=0; farenheit<=300; farenheit+=STEP){
	float celcius = (5*(farenheit-32))/9;
	printf("%3.0f\t%6.1f\n",farenheit,celcius);
	}
}
