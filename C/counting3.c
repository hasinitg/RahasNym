#include<stdio.h>

main(){
	double count;
	for(count=0; getchar() != EOF; count++)
		;
	printf("%.0f\n", count);
}
