#include <stdio.h>

main(){
	int c;
	int isPreviousCharacterSpace=0;
	while((c=getchar())!=EOF){
		if(c!=' '){
			putchar(c);
			isPreviousCharacterSpace=0;
		}else{
			if(isPreviousCharacterSpace==0){
				putchar(c);
				isPreviousCharacterSpace=1;
			}			
		}	
	}
}
