#include<stdio.h>

main(){
	int c;
	int length = 0;
	while((c=getchar())!=EOF){
		length++;
		printf("length of the input: %d\n", length);
	}
}
