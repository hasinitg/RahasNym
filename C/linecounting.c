#include <stdio.h>

main(){
	int lines;
	lines = 0;
	while(getchar() != EOF){
		if(getchar()=='\n'){
			++lines;
		}
	}
	printf("%d\n", lines);
}
